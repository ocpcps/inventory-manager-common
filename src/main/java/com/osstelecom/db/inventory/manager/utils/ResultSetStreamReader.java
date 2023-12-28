/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.osstelecom.db.inventory.manager.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Porque eu acho que é chato fazer um loop em um resultset, então eu fiz
 * isso,que ajuda a gente a não trabalhar com listas e economiza memória
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 10.03.2023
 */
public class ResultSetStreamReader implements AutoCloseable {

    private final PreparedStatement pst;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private final List<String> colNames = new ArrayList<>();
    private Boolean consumed = false;

    public ResultSetStreamReader(PreparedStatement pst) {
        this.pst = pst;
    }

    /**
     * Run The Query
     *
     * @throws SQLException
     */
    private void runQuery() throws SQLException, IllegalStateException {
        if (!this.consumed) {
            this.resultSet = this.pst.executeQuery();
            this.metaData = this.resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                colNames.add(metaData.getColumnLabel(i));
            }
        } else {
            throw new IllegalStateException("Already Consumed");
        }
    }

    /**
     * Intera um foreach para salvar a memória, eu acho bem mais bonito do que
     * puxar os dados das maneiras convencionais
     *
     * @param action
     * @throws SQLException
     */
    public void forEach(Consumer<? super DynamicInventoryMap> action) throws SQLException, IllegalStateException {
        Objects.requireNonNull(action);
        this.runQuery();
        while (resultSet.next()) {
            DynamicInventoryMap map = new DynamicInventoryMap();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(this.metaData.getColumnLabel(i), new DynamicDBObject(this.resultSet.getObject(i)));
            }

            action.accept(map);
        }
        this.resultSet.close();
        this.pst.close();
    }

    @Override
    public void close() throws SQLException {
        this.pst.close();
    }

}
