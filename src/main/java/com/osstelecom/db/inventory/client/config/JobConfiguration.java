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

import java.util.Map;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 25.08.2023
 */
public class JobConfiguration {

    private String name;
    private String group;
    private String className;
    private String cron;
    private boolean enabled = false;
    private Integer flushThreads = 16;
    private Map<String, String> fieldMapping;
    private Map<String, String> queries;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the cron
     */
    public String getCron() {
        return cron;
    }

    /**
     * @param cron the cron to set
     */
    public void setCron(String cron) {
        this.cron = cron;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the queries
     */
    public Map<String, String> getQueries() {
        return queries;
    }

    /**
     * @return the fieldMapping
     */
    public Map<String, String> getFieldMapping() {
        return fieldMapping;
    }

    /**
     * @param fieldMapping the fieldMapping to set
     */
    public void setFieldMapping(Map<String, String> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }

    /**
     * @return the flushThreads
     */
    public Integer getFlushThreads() {
        return flushThreads;
    }

    /**
     * @param flushThreads the flushThreads to set
     */
    public void setFlushThreads(Integer flushThreads) {
        this.flushThreads = flushThreads;
    }

}
