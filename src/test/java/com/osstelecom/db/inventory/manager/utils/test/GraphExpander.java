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
package com.osstelecom.db.inventory.manager.utils.test;

import com.osstelecom.db.inventory.client.NClient;
import com.osstelecom.db.inventory.client.config.OauthServerClientConfiguration;
import com.osstelecom.db.inventory.client.config.OauthServerClientConfigurationManager;
import com.osstelecom.db.inventory.manager.resources.CircuitResource;
import com.osstelecom.db.inventory.manager.resources.ManagedResource;
import com.osstelecom.db.inventory.manager.resources.ResourceConnection;
import com.osstelecom.db.inventory.visualization.dto.ThreeJSLinkDTO;
import com.osstelecom.db.inventory.visualization.dto.ThreeJSViewDTO;
import com.osstelecom.db.inventory.visualization.dto.ThreeJsNodeDTO;
import com.osstelecom.db.inventory.visualization.exception.InvalidGraphException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 16.08.2023
 */
public class GraphExpander {

    private OauthServerClientConfigurationManager configManager;
    private OauthServerClientConfiguration configuration;
    private NClient client;
    private String domain = "transporte_unificado";

    public GraphExpander() {
        configManager = new OauthServerClientConfigurationManager();
        configuration = configManager.loadConfiguration();
        configuration.setPassword("Lucas@321");
        client = new NClient("https://dev-netcompass.tdigital-vivo.com.br/inventory/v1/", configuration);
    }

    public void execute() throws IOException, InvalidGraphException {
        try {
            client.connect();

            ThreeJSViewDTO view = client.domains().get(domain).graph().expandByNode("b5bdb987d7159fa27b223bdc53f9366f");
            List<ManagedResource> nodesReleated = fetchNodes(view);
            List<ResourceConnection> connectionsRelated = fetchConnections(view);
            List<CircuitResource> circuitsRelated = fetchCircuits(view);

            expandCircuits(circuitsRelated, nodesReleated, connectionsRelated);

            printResults(nodesReleated, connectionsRelated, circuitsRelated);
        } catch (IOException ex) {
            throw ex;
        } finally {
            client.disconnect();
        }
    }

    private List<ManagedResource> fetchNodes(ThreeJSViewDTO view) throws IOException {
        List<ManagedResource> nodesReleated = new ArrayList<>();
        for (ThreeJsNodeDTO node : view.getNodes()) {
            ManagedResource managedResource = client.domains().get(domain).resources().get(node.getId());
            if (!nodesReleated.contains(managedResource)) {
                nodesReleated.add(managedResource);
            }
        }
        return nodesReleated;
    }

    private List<ResourceConnection> fetchConnections(ThreeJSViewDTO view) throws IOException {
        List<ResourceConnection> connectionsRelated = new ArrayList<>();
        for (ThreeJSLinkDTO connection : view.getLinks()) {
            ResourceConnection resourceConnection = client.domains().get(domain).connections().get(connection.getId());
            if (!connectionsRelated.contains(resourceConnection)) {
                connectionsRelated.add(resourceConnection);
            }
        }
        return connectionsRelated;
    }

    private List<CircuitResource> fetchCircuits(ThreeJSViewDTO view) throws IOException {
        Set<String> tempCircuitIds = new HashSet<>();
        for (ThreeJSLinkDTO connection : view.getLinks()) {
            if (connection.getCircuits() != null) {
                tempCircuitIds.addAll(connection.getCircuits());
            }
        }

        List<CircuitResource> circuitsRelated = new ArrayList<>();
        for (String cicid : tempCircuitIds) {
            CircuitResource circuitResource = client.domains().get(domain).circuits().get(cicid);
            circuitsRelated.add(circuitResource);
        }

        return circuitsRelated;
    }

    private void expandCircuits(List<CircuitResource> circuits, List<ManagedResource> nodes, List<ResourceConnection> connections) throws IOException {
        for (CircuitResource cic : circuits) {
            ThreeJSViewDTO circuitDto = client.domains().get(domain).graph().expandByCircuit(cic.getKey());
            nodes.addAll(fetchNodes(circuitDto));
            connections.addAll(fetchConnections(circuitDto));
        }
    }

    private void printResults(List<ManagedResource> nodes, List<ResourceConnection> connections, List<CircuitResource> circuits) {
        System.out.println("Total Nodes: " + nodes.size());
        System.out.println("Total Connections: " + connections.size());
        System.out.println("Total Circuits: " + circuits.size());
    }

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        GraphExpander expander = new GraphExpander();
        try {
            expander.execute();
        } catch (IOException ex) {
            Logger.getLogger(GraphExpander.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidGraphException ex) {
            Logger.getLogger(GraphExpander.class.getName()).log(Level.SEVERE, null, ex);
        }
        Long end = System.currentTimeMillis();
        Long took = end - start;
        System.out.println("Took: " + took + "ms");
    }
}
