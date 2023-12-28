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
package com.osstelecom.db.inventory.client.config;

import java.util.List;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 05.01.2023
 */
public class OauthServerClientConfiguration {

    private String ssoName;
    private String clientId;
    private String clientSecret;
    private String userName;
    private String password;
    private String tokenServerUrl;
    private String authorizationServerUrl;
    private String appKey;
    private String oam;
    private List<String> authScopes;
    private Boolean useRefreshToken = true;
    private Integer renewBeforeSecs = 30;
    private String agentName = "Netcompass API Client v0.1";

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public OauthServerClientConfiguration() {
    }

    public OauthServerClientConfiguration(String ssoName, String clientId, String clientSecret, String userName, String password, String tokenServerUrl, String authorizationServerUrl, List<String> authScopes) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userName = userName;
        this.password = password;
        this.tokenServerUrl = tokenServerUrl;
        this.authorizationServerUrl = authorizationServerUrl;
        this.authScopes = authScopes;
        this.ssoName = ssoName;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret the clientSecret to set
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the tokenServerUrl
     */
    public String getTokenServerUrl() {
        return tokenServerUrl;
    }

    /**
     * @param tokenServerUrl the tokenServerUrl to set
     */
    public void setTokenServerUrl(String tokenServerUrl) {
        this.tokenServerUrl = tokenServerUrl;
    }

    /**
     * @return the authorizationServerUrl
     */
    public String getAuthorizationServerUrl() {
        return authorizationServerUrl;
    }

    /**
     * @param authorizationServerUrl the authorizationServerUrl to set
     */
    public void setAuthorizationServerUrl(String authorizationServerUrl) {
        this.authorizationServerUrl = authorizationServerUrl;
    }

    public List<String> getAuthScopes() {
        return authScopes;
    }

    public void setAuthScopes(List<String> authScopes) {
        this.authScopes = authScopes;
    }

    /**
     * @return the ssoName
     */
    public String getSsoName() {
        return ssoName;
    }

    /**
     * @param ssoName the ssoName to set
     */
    public void setSsoName(String ssoName) {
        this.ssoName = ssoName;
    }

    /**
     * @return the appKey
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * @param appKey the appKey to set
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /**
     * @return the oam
     */
    public String getOam() {
        return oam;
    }

    /**
     * @param oam the oam to set
     */
    public void setOam(String oam) {
        this.oam = oam;
    }

    /**
     * @return the useRefreshToken
     */
    public Boolean getUseRefreshToken() {
        return useRefreshToken;
    }

    /**
     * @param useRefreshToken the useRefreshToken to set
     */
    public void setUseRefreshToken(Boolean useRefreshToken) {
        this.useRefreshToken = useRefreshToken;
    }

    /**
     * @return the renewBeforeSecs
     */
    public Integer getRenewBeforeSecs() {
        return renewBeforeSecs;
    }

    /**
     * @param renewBeforeSecs the renewBeforeSecs to set
     */
    public void setRenewBeforeSecs(Integer renewBeforeSecs) {
        this.renewBeforeSecs = renewBeforeSecs;
    }

}
