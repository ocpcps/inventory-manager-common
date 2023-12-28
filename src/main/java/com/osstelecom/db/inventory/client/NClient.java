/*
 * Copyright (C) 2022 Lucas Nishimura <lucas.nishimura@gmail.com>
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

import com.osstelecom.db.inventory.client.config.OauthServerClientConfiguration;
import com.osstelecom.db.inventory.client.security.OAuthClientManager;
import java.io.IOException;

/**
 * The netcompass API Client
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 15.08.2023
 */
public class NClient {

    private final OauthServerClientConfiguration configuration;

    private OAuthClientManager oauthClientManager;
    private final String apiUrl;
    private DomainClient domains;

    public NClient(String apiUrl, OauthServerClientConfiguration configuration) {
        this.configuration = configuration;
        this.apiUrl = apiUrl;
    }

    public void connect() throws IOException {
        if (this.oauthClientManager == null) {
            this.oauthClientManager = new OAuthClientManager();
        }
        this.oauthClientManager.initOauthClientManager(configuration);
       
        this.domains = new DomainClient(apiUrl, this.oauthClientManager);

    }

    public void disconnect(){
        this.oauthClientManager.shutdown();
    }
    
    public DomainClient domains() {
        return domains;
    }
    
    

}
