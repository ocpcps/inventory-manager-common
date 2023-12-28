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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonIOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 16.02.2023
 */
public class OauthServerClientConfigurationManager {

    private final ObjectMapper yamlSerializer = new ObjectMapper(new YAMLFactory());

    private final Logger logger = LoggerFactory.getLogger(OauthServerClientConfigurationManager.class);
    private OauthServerClientConfiguration configuration;

    /**
     * Procura os paths em ordem! o primeiro que encontrar ele vai usar
     *
     * @return
     */
    private ArrayList<String> getConfigurationPaths() {
        ArrayList<String> paths = new ArrayList<>();
        paths.add("config/netcompass-client.yaml");
        paths.add("/app/inventory-adapter/config/netcompass-client.yaml");
        paths.add("/etc/inventory-adapter/netcompass-client.yaml");
        paths.add("c:/temp/netcompass-client.yaml");
        String envPath = System.getenv("NC_CLIENT_CONFIG");
        if (envPath != null) {
            //
            // Variaveis de ambiente tem prioridade
            //
            paths.add(0, envPath);
        }
        return paths;
    }

    /**
     * Carrega ou Cria o arquivo de configuração
     *
     * @return
     */
    public OauthServerClientConfiguration loadConfiguration() {
        if (this.configuration == null) {
            logger.debug("Loading System Configuration:");
            for (String configPath : this.getConfigurationPaths()) {
                File configFile = new File(configPath);
                logger.debug(" Trying Configuration File at: [" + configPath + "] Exists: [" + configFile.exists() + "]");
                if (configFile.exists()) {
                    try {

                        try (FileReader reader = new FileReader(configFile)) {
                            this.configuration = this.yamlSerializer.readValue(reader, OauthServerClientConfiguration.class);
                        }
                        logger.info("Configuration file Loaded from :[" + configFile.getPath() + "]");
                        return this.configuration;
                    } catch (FileNotFoundException ex) {
                        logger.error("Configuration File , not found... weird exception..", ex);
                    } catch (IOException ex) {
                        logger.error("Failed to Close IO..", ex);
                    }
                    break;
                }
            }
            //
            // Não existe arquivo de configuração... vamos tentar criar...UM DEFAULT
            //
            this.configuration = new OauthServerClientConfiguration();

            configuration.setSsoName("default");
            configuration.setAuthScopes(Collections.singletonList("read"));
            configuration.setUserName("nishimura");
            configuration.setPassword("Luas@15");
            configuration.setClientId("inventory-api");
            configuration.setClientSecret("OXy66zx1pZL4HYqmsgWCVa5rgGFNS91U");
            configuration.setTokenServerUrl("https://dev-sso-netcompass.tdigital-vivo.com.br/realms/netcompass/protocol/openid-connect/token");
            configuration.setAuthorizationServerUrl("https://dev-sso-netcompass.tdigital-vivo.com.br/realms/netcompass/protocol/openid-connect/auth");

            File theConfigurationFile = new File(getConfigurationPaths().get(0));
            if (theConfigurationFile.getParentFile().exists()) {
                try {
                    FileWriter writer = new FileWriter(theConfigurationFile);
//                    gson.toJson(this.configuration, writer);
                    this.yamlSerializer.writeValue(writer, this.configuration);
//                    writer.flush();
//                    writer.close();
                    logger.info("Default Configuration file Created at: [" + theConfigurationFile.getPath() + "]");
                } catch (IOException ex) {
                    logger.error("Failed to save configuration file", ex);
                } catch (JsonIOException ex) {
                    logger.error("Configuration File Produced an invalid JSON", ex);
                }
            } else {
                logger.warn("Cannot Create Default Configuration file at: [" + getConfigurationPaths().get(0) + "]");
            }
        }
        return this.configuration;
    }

}
