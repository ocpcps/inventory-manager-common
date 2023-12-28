/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.osstelecom.db.inventory.manager.utils;

import java.util.HashMap;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 29.05.2023
 */
public class DynamicInventoryMap extends HashMap<String, DynamicDBObject> {

    /**
     * Garante que vamos tratar valores Null da forma correta xD
     * @param key
     * @return 
     */
    @Override
    public DynamicDBObject get(Object key) {
        return super.getOrDefault(key, new DynamicDBObject(null));
    }

}
