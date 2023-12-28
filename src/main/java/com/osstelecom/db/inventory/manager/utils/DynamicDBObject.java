/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.osstelecom.db.inventory.manager.utils;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 10.03.2023
 */
public class DynamicDBObject implements Serializable {

    private Object obj;
    private boolean isNull = false;

    public DynamicDBObject(Object obj) {
        if (obj != null) {
            this.obj = obj;
        } else {
            this.isNull = true;
        }
    }

    /**
     * Retorna True se o Objeto for null
     * @return 
     */
    public boolean isNull() {
        return this.isNull;
    }

    /**
     * Retorna true se o objeto não for null
     * @return 
     */
    public boolean isNotNull() {
        return !this.isNull;
    }

    public String asString() {
        if (this.obj != null) {
            if (this.obj instanceof String) {
                return (String) this.obj;
            } else {
                return this.obj.toString();
            }
        }
        return null;
    }

    public String asString(String defaultValue) {
        if (this.obj != null) {
            if (this.obj instanceof String) {
                return (String) this.obj;
            } else {
                return this.obj.toString();
            }
        }
        return defaultValue;
    }

    public Integer asInteger() throws NumberFormatException {
        if (this.obj != null) {
            if (this.obj instanceof Integer) {
                return (Integer) this.obj;
            } else {
                return Integer.valueOf(this.obj.toString());
            }
        }
        return null;
    }

    public Integer asInteger(Integer defaultValue) throws NumberFormatException {
        if (this.obj != null) {
            if (this.obj instanceof Integer) {
                return (Integer) this.obj;
            } else {
                return Integer.valueOf(this.obj.toString());
            }
        }
        return defaultValue;
    }

    public LocalDate asLocalDate() throws NumberFormatException {
        if (this.obj != null) {
            if (this.obj instanceof java.sql.Date) {
                return ((java.sql.Date) this.obj).toLocalDate();
            }
        }
        return null;
    }

    public Long asTimeStamp() {
        if (this.obj != null) {
            if (this.obj instanceof java.sql.Timestamp) {
                return ((java.sql.Timestamp) this.obj).getTime();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.obj.toString();
    }

}
