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
import com.osstelecom.db.inventory.manager.dto.FilterDTO;
import com.osstelecom.db.inventory.manager.request.FilterRequest;
import com.osstelecom.db.inventory.manager.request.PatchManagedResourceRequest;
import com.osstelecom.db.inventory.manager.resources.Domain;
import com.osstelecom.db.inventory.manager.resources.ManagedResource;
import com.osstelecom.db.inventory.manager.response.FilterResponse;
import com.osstelecom.db.inventory.manager.response.FindManagedResourceResponse;
import com.osstelecom.db.inventory.manager.response.PatchManagedResourceResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 15.08.2023
 */
public class ResourceClient extends BaseNHttpClient {

    private final Domain domain;

    public ResourceClient(Domain domain, String apiUrl, OAuthClientManager oauthClientManager) {
        super(apiUrl, oauthClientManager);
        this.domain = domain;
    }

    public ManagedResource updateManagedResource(ManagedResource resource) throws IOException {
        String url = "/" + this.domain.getDomainName() + "/resource/" + resource.getKey();
        PatchManagedResourceRequest request = new PatchManagedResourceRequest();
        request.setPayLoad(resource);
        PatchManagedResourceResponse response = this.patch(url, request, PatchManagedResourceResponse.class);
        return response.getPayLoad();
    }

    public ManagedResource get(String id) throws IOException {
        String url = "/" + this.domain.getDomainName() + "/resource/" + id;
        FindManagedResourceResponse response = this.get(url, FindManagedResourceResponse.class);
        return response.getPayLoad();
    }

    public List<ManagedResource> findByCorrelationId(String correlationId) throws IOException {
        String url = "/" + this.domain.getDomainName() + "/filter/";
        FilterRequest request = new FilterRequest();
        FilterDTO filter = new FilterDTO("doc.eventSourceIds == @correlationId "
                + "or @correlationId in doc.correlationIds[*]", Map.of("correlationId", correlationId), domain.getDomainName());
        filter.getObjects().add("nodes");
        request.setPayLoad(filter);
        FilterResponse response = this.post(url, request, FilterResponse.class);
        return response.getPayLoad().getNodes();
    }

}
