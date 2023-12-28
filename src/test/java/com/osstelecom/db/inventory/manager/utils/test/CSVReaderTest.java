/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.osstelecom.db.inventory.manager.utils.test;

import com.osstelecom.db.inventory.manager.utils.CsvBufferedReader;


/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 09.03.2023
 */
public class CSVReaderTest {

    public static void main(String[] args) {
        CsvBufferedReader reader = new CsvBufferedReader("/home/lucas/Documents/INVENTARIO_FSP.txt", true, ";");
        try {

            reader.forEachAsMap((field, value) -> {
                
            });
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
