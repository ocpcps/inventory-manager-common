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
package com.osstelecom.db.inventory.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.osstelecom.db.inventory.client.security.OAuthClientManager;
import com.osstelecom.db.inventory.manager.request.BasicRequest;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 15.08.2023
 */
public abstract class BaseNHttpClient {

    protected final OAuthClientManager oauthClientManager;
    protected final String apiUrl;
    protected Gson gson;

    public BaseNHttpClient(String apiUrl, OAuthClientManager oauthClientManager) {
        this.oauthClientManager = oauthClientManager;
        this.apiUrl = apiUrl;
        if (this.gson == null) {
            this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").setPrettyPrinting().create();
        }
    }

    protected <T> T get(String url, Class<T> type) throws IOException, JsonSyntaxException {
        if (url.startsWith("/") && apiUrl.endsWith("/")) {
            if (url.length() > 0) {
                url = url.substring(1);
            }
        }
        Request request = new Request.Builder()
                .url(apiUrl + url)
                .get().build();

        System.out.println(":GET:[URL]:" + apiUrl + url);

        try (Response r = this.oauthClientManager.getAuthHttpClient().newCall(request).execute()) {
            if (r.code() == 200) {
                String responseBody = r.body().string();
                T result = gson.fromJson(responseBody, type);
                return result;
            } else {
                String responseBody = r.body().string();
                throw new IOException("GET: Error CODE:[" + r.code() + "] Response Body:[" + responseBody + "]");

            }

        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    protected <T> T post(String url, BasicRequest req, Class<T> type) throws IOException {
        if (url.startsWith("/") && apiUrl.endsWith("/")) {
            if (url.length() > 0) {
                url = url.substring(1);
            }
        }
        RequestBody body = RequestBody.create(this.gson.toJson(req), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(apiUrl + url)
                .post(body).build();

        System.out.println(":POST:[URL]:" + apiUrl + url);
        System.out.println(":POST:[BODY]:" + this.gson.toJson(req));
        try (Response r = this.oauthClientManager.getAuthHttpClient().newCall(request).execute()) {
            if (r.code() == 200) {
                String responseBody = r.body().string();
                T result = gson.fromJson(responseBody, type);
                return result;
            } else {
                throw new IOException("POST: Error CODE:[" + r.code() + "]");
            }

        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    protected <T> T patch(String url, BasicRequest req, Class<T> type) throws IOException {
        if (url.startsWith("/") && apiUrl.endsWith("/")) {
            if (url.length() > 0) {
                url = url.substring(1);
            }
        }
        RequestBody body = RequestBody.create(this.gson.toJson(req), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(apiUrl + url)
                .patch(body).build();

        System.out.println(":PATCH:[URL]:" + apiUrl + url);
        System.out.println(":PATCH:[BODY]:" + this.gson.toJson(req));
        try (Response r = this.oauthClientManager.getAuthHttpClient().newCall(request).execute()) {
            if (r.code() == 200) {
                String responseBody = r.body().string();
                T result = gson.fromJson(responseBody, type);
                return result;
            } else {
                String resp = r.body().string();
                throw new IOException("PATCH: Error CODE:[" + r.code() + "] " + resp);

            }

        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }
}
