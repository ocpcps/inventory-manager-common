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

import com.osstelecom.db.inventory.client.security.OAuthClientManager;
import com.osstelecom.db.inventory.manager.resources.CircuitResource;
import com.osstelecom.db.inventory.manager.resources.Domain;
import com.osstelecom.db.inventory.manager.response.GetCircuitResponse;
import java.io.IOException;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 16.08.2023
 */
public class CircuitClient extends BaseNHttpClient {

    private final Domain domain;

    public CircuitClient(Domain domain, String apiUrl, OAuthClientManager oauthClientManager) {
        super(apiUrl, oauthClientManager);
        this.domain = domain;
    }

    public CircuitResource get(String circuitId) throws IOException {
        String url = "/" + this.domain.getDomainName() + "/circuit/" + circuitId;
        GetCircuitResponse response = this.get(url, GetCircuitResponse.class);
        return response.getPayLoad();
    }
}
