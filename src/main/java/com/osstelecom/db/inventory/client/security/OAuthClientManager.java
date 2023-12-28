/*
 * Copyright (C) 2023 Lucas Nishimura <lucas.nishimura@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.osstelecom.db.inventory.client.security;

import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.osstelecom.db.inventory.client.config.OauthServerClientConfiguration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Um Cliente OAUTH que é bonito de ver funcionar :)
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 05.01.2023
 */
//@Service
public class OAuthClientManager {

    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private final ConcurrentMap<String, OauthServerClientConfiguration> configuredSso = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AuthToken> currentTokens = new ConcurrentHashMap<>();
    private final Logger logger = LogManager.getLogger(OAuthClientManager.class);

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Thread tokenRefreshThread = new Thread(new TokenRefreshThread());
    private OkHttpClient httpClient;

    public void initOauthClientManager(OauthServerClientConfiguration configuration) throws IOException {
        if (!this.running.get()) {
            this.addSsoConfiguration(configuration);
            this.running.set(true);
            this.tokenRefreshThread.start();

            try {
                TokenResponse r = this.getAccessToken();
                logger.info("Token OK:" + r.getTokenType());
                //
                // Enable Debug ?
                //

//            java.util.logging.Logger httpLogger = java.util.logging.Logger.getLogger("com.google.gdata.client.http.HttpGDataRequest");
//            httpLogger.setLevel(Level.ALL);
            } catch (IOException ex) {
                logger.error("Failed to Get Token", ex);
                this.shutdown();
                throw ex;
            }
        }

    }

    public void shutdown() {
        this.tokenRefreshThread.interrupt();
        this.running.set(false);
    }

    private void addSsoConfiguration(OauthServerClientConfiguration configuration) {
        configuration.setSsoName("default");
        this.configuredSso.put(configuration.getSsoName(), configuration);
    }

    public TokenResponse refreshToken(String ssoName) throws IOException, GeneralSecurityException, SSONotFoundException {
        if (this.currentTokens.containsKey(ssoName)) {
            AuthToken savedResponse = this.currentTokens.get(ssoName);
            return this.refreshToken(savedResponse);
        } else {
            throw new SSONotFoundException("SSO [" + ssoName + "] not found");
        }
    }

    /**
     * Get the Access TOKEN using the OLD password flow
     *
     * @return
     * @throws IOException
     */
    public TokenResponse getAccessToken() throws IOException {
        if (this.currentTokens.containsKey("default")) {
            //
            // Opa temos um token salvo
            //
            AuthToken savedResponse = this.currentTokens.get("default");
            if (savedResponse.getCurrentResponse().getExpiresInSeconds() != null) {
                //
                // Vamos calcular a data de expiração
                // 
                Calendar cal = Calendar.getInstance();

                cal.setTime(savedResponse.getLastTimeTokenRefreshed());
                cal.add(Calendar.SECOND, savedResponse.getCurrentResponse().getExpiresInSeconds().intValue());

                Date now = new Date(System.currentTimeMillis());
                if (now.after(cal.getTime())) {
                    //
                    // Token Expirou , precisa de um refresh
                    //
                    this.currentTokens.remove("default");
                    try {
                        //
                        // Tenta fazer o Refresh
                        //
                        this.refreshToken(savedResponse);
                        //
                        // Se conseguiu o refresh atualiza o cache
                        //
                        this.currentTokens.put("default", savedResponse);
                        logger.info("New Token Just Created For:[{}]", "default");
                        return savedResponse.getCurrentResponse();
                    } catch (GeneralSecurityException | IOException ex) {
                        //
                        // Não conseguiu fazer o Refresh, vai seguir no fluso normal.
                        //
                    }

                } else {
//                    logger.info("SSO [{}] Using Cached Token", "default");
                    return savedResponse.getCurrentResponse();
                }
            }
        }
        //
        // Fluxo Normal
        //
        OauthServerClientConfiguration oauthServerClientConfiguration = configuredSso.get("default");
        TokenResponse response = this.getAccessTokenFromPassword(oauthServerClientConfiguration);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, response.getExpiresInSeconds().intValue());
        this.currentTokens.put("default", new AuthToken(response, oauthServerClientConfiguration, "default", new Date(), cal.getTime()));
        return response;

    }

    /**
     * Realiza o flow do grant_type password no OAM
     *
     * @param oauthServerClientConfiguration
     * @return
     * @throws IOException
     */
    public TokenResponse getAccessTokenFromPassword(OauthServerClientConfiguration oauthServerClientConfiguration) throws IOException {
        GenericUrl url = new GenericUrl(oauthServerClientConfiguration.getTokenServerUrl());

        PasswordTokenRequest passwordTokenRequest
                = new PasswordTokenRequest(new NetHttpTransport(),
                        JSON_FACTORY, url,
                        oauthServerClientConfiguration.getUserName(),
                        oauthServerClientConfiguration.getPassword());
        passwordTokenRequest.setGrantType("password");

        passwordTokenRequest.setScopes(oauthServerClientConfiguration.getAuthScopes());
        Map<String, String> headers = new HashMap<>();
        if (oauthServerClientConfiguration.getOam()
                != null && oauthServerClientConfiguration.getAppKey() != null) {

            headers.put("app-key", oauthServerClientConfiguration.getAppKey());
            headers.put("oam", oauthServerClientConfiguration.getOam());

        }
        passwordTokenRequest.setRequestInitializer(new CustomHttpRequestInitializer(headers));

        passwordTokenRequest.setClientAuthentication(
                new BasicAuthentication(oauthServerClientConfiguration.getClientId(),
                        oauthServerClientConfiguration.getClientSecret()));
        TokenResponse response = passwordTokenRequest.execute();

        if (!this.currentTokens.containsKey(oauthServerClientConfiguration.getSsoName())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, response.getExpiresInSeconds().intValue());
            logger.info("Token [{}] Created New Expiration AT: [{}]", oauthServerClientConfiguration.getSsoName(), cal.getTime());
            this.currentTokens.put(oauthServerClientConfiguration.getSsoName(), new AuthToken(response, oauthServerClientConfiguration, oauthServerClientConfiguration.getSsoName(), new Date(), cal.getTime()));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, response.getExpiresInSeconds().intValue());
            logger.info("Token [{}] Renewed New Expiration AT: [{}]", oauthServerClientConfiguration.getSsoName(), cal.getTime());
            this.currentTokens.replace(oauthServerClientConfiguration.getSsoName(), new AuthToken(response, oauthServerClientConfiguration, oauthServerClientConfiguration.getSsoName(), new Date(), cal.getTime()));

        }

        return response;
    }

    /**
     * Solicita um Refresh do token
     *
     * @param previousToken
     * @return
     * @throws IOException
     */
    public TokenResponse refreshToken(AuthToken previousToken) throws IOException, GeneralSecurityException {
        GenericUrl tokenServerUrl = new GenericUrl(previousToken.getConfiguration().getTokenServerUrl());

        RefreshTokenRequest refreshTokenRequest
                = new RefreshTokenRequest(new NetHttpTransport.Builder().doNotValidateCertificate().build(),
                        JSON_FACTORY, tokenServerUrl,
                        previousToken.getCurrentResponse().getRefreshToken());

        refreshTokenRequest.setClientAuthentication(
                new BasicAuthentication(previousToken.getConfiguration().getClientId(),
                        previousToken.getConfiguration().getClientSecret()));
        Map<String, String> headers = new HashMap<>();
        if (previousToken.getConfiguration().getOam() != null && previousToken.getConfiguration().getAppKey() != null) {

            headers.put("app-key", previousToken.getConfiguration().getAppKey());
            headers.put("oam", previousToken.getConfiguration().getOam());

        }
        HttpRequestInitializer requestInitializer = new CustomHttpRequestInitializer(headers);
        refreshTokenRequest.setRequestInitializer(requestInitializer);

        TokenResponse response = refreshTokenRequest.execute();
        previousToken.setCurrentResponse(response);
        previousToken.setLastTimeTokenRefreshed(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, response.getExpiresInSeconds().intValue());
        previousToken.setExpiresIn(cal.getTime());
        previousToken.setLastTimeTokenRefreshed(new Date());
        return response;
    }

    /**
     * Esta classizinha fica verificando se temos algum token próximo de
     * expiração e pró ativamente faz o refresh do mesmo
     */
    private class TokenRefreshThread implements Runnable {

        @Override
        public void run() {

            Calendar cal = Calendar.getInstance();
            List<String> tokensToBeRemoved = new ArrayList<>();
            while (running.get()) {

//                logger.info("Total Tokens Found: [{}]", currentTokens.size());
                currentTokens.forEach((tokenName, tokenAuth) -> {
                    if (tokenAuth.getCurrentResponse() != null) {
                        if (tokenAuth.getConfiguration().getUseRefreshToken()) {
                            if (tokenAuth.getCurrentResponse().getExpiresInSeconds() != null) {
                                //
                                // Verifica se temos tokens vencendos nos próxios 30s
                                //
                                cal.setTime(new Date());
                                cal.add(Calendar.SECOND, tokenAuth.getConfiguration().getRenewBeforeSecs());
                                if (cal.getTime().after(tokenAuth.getExpiresIn())) {
                                    logger.info("Token [{}] Will Expire in 30s, refreshing", tokenName);
                                    try {
                                        refreshToken(tokenAuth);
                                        logger.info("Token [{}] Refreshed New Expiration AT: [{}]", tokenName, tokenAuth.getExpiresIn());
                                    } catch (IOException | GeneralSecurityException ex) {
                                        logger.error("Failed to Refresh Token:[" + tokenName + "]", ex);
                                        tokensToBeRemoved.add(tokenName);
                                    }
                                }

                            }
                        } else {
                            //
                            // Refresh Token Not Enabled for 
                            //
                            if (tokenAuth.getCurrentResponse().getExpiresInSeconds() != null) {
                                //
                                // Verifica se temos tokens vencendos nos próxios 30s
                                //
                                cal.setTime(new Date());
                                cal.add(Calendar.SECOND, tokenAuth.getConfiguration().getRenewBeforeSecs());
                                if (cal.getTime().after(tokenAuth.getExpiresIn())) {
                                    logger.warn("Refresh Token Not Enabled for :[{}]", tokenName);
                                    tokensToBeRemoved.add(tokenName);
                                }
                            }
                        }
                    }
                });

                if (!tokensToBeRemoved.isEmpty()) {
                    tokensToBeRemoved.forEach(e -> {
                        currentTokens.remove(e);
                        logger.warn("Token Removed due to errors");
                    });
                    tokensToBeRemoved.clear();
                }

                //
                // Descansa um pouquinho para poupar CPU xD
                //
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            logger.info("Done OauthManager Thread");
        }
    }

    public OkHttpClient getAuthHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        if (this.httpClient == null) {
            // Delega um TrustManager para aceitar todos os certificados
            final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            //
            // Se tivermos uma configuração de Oauth para o back
            // Cria o interceptor para carimbar o header
            //
            this.httpClient = new OkHttpClient.Builder().addInterceptor((chain) -> {
                Request original = chain.request();

                Request newRequest = original.newBuilder()
                        //                            .headers(original.headers())
                        //                            .method(original.method(), original.body())
                        .header("User-Agent", this.configuredSso.get("default").getAgentName())
                        //                        .header("x-show-errors", "1")
                        .header("Authorization", "Bearer " + this.getAccessToken().getAccessToken())
                        .build();

//            logger.info("Interceptor Called");
//            logger.info("Target: Authenticated URL:[{}] Method:[{}]", newRequest.url().uri(), newRequest.method());
                return chain.proceed(newRequest);
            }).sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    //                    .addInterceptor(new GzipInterceptor())
                    .build();
        }

        return this.httpClient;
    }
}
