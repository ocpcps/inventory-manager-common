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
import com.osstelecom.db.inventory.visualization.dto.ThreeJSViewDTO;
import com.osstelecom.db.inventory.visualization.exception.InvalidGraphException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 15.08.2023
 */
public class NClientTest {

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        OauthServerClientConfigurationManager configManager = new OauthServerClientConfigurationManager();
        OauthServerClientConfiguration configuration = configManager.loadConfiguration();
        configuration.setPassword("Lucas@321");
        System.out.println();
        NClient c = new NClient("https://dev-netcompass.tdigital-vivo.com.br/inventory/v1/", configuration);
        try {

            c.connect();

            // 
            // 1o. Precisamos identificar o nó. e suas Conexões no grafo.
            //
            /**
             * Pesquisa o Graph
             */
            
            String startNodeId = "d36db1178b44972ff8a37fd9665a6859";
            ThreeJSViewDTO view = c.domains().get("transporte_unificado").graph().expandByNode(startNodeId);
            List<CircuitResource> circuitsRelated = new ArrayList<>();
            List<ResourceConnection> connectionsRelated = new ArrayList<>();
            List<ManagedResource> nodesReleated = new ArrayList<>();

            view.getNodes().forEach(node -> {
                try {
                    ManagedResource managedResource = c.domains().get("transporte_unificado").resources().get(node.getId());

                    if (!nodesReleated.contains(managedResource)) {
                        nodesReleated.add(managedResource);
                        System.out.println("Added: " + managedResource.getNodeAddress());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(NClientTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            Optional<ManagedResource> inElement = nodesReleated.stream().filter(r -> r.getKey().equals(startNodeId)).findFirst();

            /**
             * Agora vamos fornecer alguns dados
             */
            List<String> tempCircuitIds = new ArrayList<>();
            view.getLinks().forEach(connection -> {
                try {
                    ResourceConnection resourceConnection = c.domains().get("transporte_unificado").connections().get(connection.getId());
                    if (!connectionsRelated.contains(resourceConnection)) {
                        connectionsRelated.add(resourceConnection);
                    }
                    System.out.println("Connection Found:[" + connection.getId() + "]");
                    if (connection.getCircuits() != null) {
                        connection.getCircuits().forEach(circuit -> {
                            System.out.println("\tCircuit Found:[" + circuit + "]");
                            if (!tempCircuitIds.contains(circuit)) {
                                //
                                // garante ids unicos
                                //                                
                                tempCircuitIds.add(circuit);
                            }

                        });
                    }
                } catch (IOException ex) {
                    Logger.getLogger(NClientTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            tempCircuitIds.forEach(cicid -> {
                try {
                    CircuitResource circuitResource = c.domains().get("transporte_unificado").circuits().get(cicid);
                    if (!circuitsRelated.contains(circuitResource)) {
                        circuitsRelated.add(circuitResource);

                    }
                } catch (IOException ex) {
                    Logger.getLogger(NClientTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            /**
             * Vamos expandir os circuitos
             */
            circuitsRelated.forEach(cic -> {
                try {
                    ThreeJSViewDTO circuitDto = c.domains().get("transporte_unificado").graph().expandByCircuit(cic.getKey());
                    circuitDto.getNodes().forEach(node -> {
                        try {
                            ManagedResource managedResource = c.domains().get("transporte_unificado").resources().get(node.getId());
                            if (!nodesReleated.contains(managedResource)) {
                                nodesReleated.add(managedResource);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(NClientTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    circuitDto.getLinks().forEach(connection -> {
                        try {
                            ResourceConnection resourceConnection = c.domains().get("transporte_unificado").connections().get(connection.getId());
                            if (!connectionsRelated.contains(resourceConnection)) {
                                connectionsRelated.add(resourceConnection);
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(NClientTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                } catch (IOException ex) {
                    Logger.getLogger(NClientTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            System.out.println("Total Nodes:" + nodesReleated.size());
            System.out.println("Total Connections:" + connectionsRelated.size());
            System.out.println("Total Circuits:" + circuitsRelated.size());
            System.out.println("------------------------------------------------");
            System.out.println("Elemento de Entrada: " + inElement.get().getNodeAddress());
            System.out.println("Circuitos Relacionados: ");
            circuitsRelated.forEach(cic -> {
                System.out.println("\t Circuito:" + cic.getNodeAddress());
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidGraphException ex) {
            Logger.getLogger(NClientTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            c.disconnect();
            Long end = System.currentTimeMillis();
            Long took = end - start;
            System.out.println("Took: " + took + "ms");
        }
    }
}
