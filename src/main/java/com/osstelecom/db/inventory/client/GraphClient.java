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
import com.osstelecom.db.inventory.manager.resources.Domain;
import com.osstelecom.db.inventory.visualization.dto.ThreeJSViewDTO;
import com.osstelecom.db.inventory.visualization.exception.InvalidGraphException;
import com.osstelecom.db.inventory.visualization.response.ThreeJsViewResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 15.08.2023
 */
public class GraphClient extends BaseNHttpClient {

    private final Domain domain;

    public GraphClient(Domain domain, String apiUrl, OAuthClientManager oauthClientManager) {
        super(apiUrl, oauthClientManager);
        this.domain = domain;
    }

    public ThreeJSViewDTO expandByNode(String id, String direction, Integer depth, ThreeJSViewDTO previous) throws IOException, InvalidGraphException {
        String url = "/graph-view/" + domain.getDomainName() + "/resource/" + id + "/expand/" + direction + "/" + depth;
        ThreeJsViewResponse response = this.get(url, ThreeJsViewResponse.class);
        if (previous != null) {
            //
            // Vai fazer o merge
            //
            if (response.getPayLoad().getNodes() != null && !response.getPayLoad().getNodes().isEmpty()) {
                response.getPayLoad().getNodes().forEach(node -> {
                    if (!previous.getNodes().contains(node)) {
                        previous.getNodes().add(node);
                    }
                });

            }

            if (response.getPayLoad().getLinks() != null && !response.getPayLoad().getLinks().isEmpty()) {
                response.getPayLoad().getLinks().forEach(link -> {
                    if (!previous.getLinks().contains(link)) {
                        previous.getLinks().add(link);
                    }
                });

            }
            previous.validate(true);
            return previous;
        }
        response.getPayLoad().validate(true);
        return response.getPayLoad();
    }

    public ThreeJSViewDTO expandByNodes(List<String> ids) throws IOException, InvalidGraphException {
        ThreeJSViewDTO result = null;
        for (String id : ids) {
            result = this.expandByNode(id, "any", 1, result);
        }
        return result;
    }

    public ThreeJSViewDTO expandByNode(String id) throws IOException, InvalidGraphException {
        return this.expandByNode(id, "any", 1, null);
    }

    public ThreeJSViewDTO expandByNode(String id, ThreeJSViewDTO previous) throws IOException, InvalidGraphException {
        return this.expandByNode(id, "any", 1, previous);
    }

    public ThreeJSViewDTO expandByCircuit(String id) throws IOException {
        String url = "/graph-view/" + domain.getDomainName() + "/circuit/" + id + "/expand/";
        ThreeJsViewResponse response = this.get(url, ThreeJsViewResponse.class);
        return response.getPayLoad();
    }

}
