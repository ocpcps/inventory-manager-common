/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.osstelecom.db.inventory.manager.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura@gmail.com>
 * @created 09.03.2023
 */
public class CsvBufferedReader implements AutoCloseable {

    private final String filePath;
    private final boolean hasHeaders;
    private final String separator;
    private BufferedReader br;
    private final List<String> headers = new ArrayList<>();
    private boolean exceptionOnError = false;
    private AtomicLong lineCounter = new AtomicLong(0L);
    private int skipLines = 0;
    private long currentLine = 0L;

    public CsvBufferedReader(String filePath, boolean hasHeaders) {
        this.filePath = filePath;
        this.hasHeaders = hasHeaders;
        this.separator = ",";
    }

    public CsvBufferedReader(String filePath, boolean hasHeaders, int skipLines) {
        this.filePath = filePath;
        this.hasHeaders = hasHeaders;
        this.separator = ",";
        this.skipLines = skipLines;
    }

    public CsvBufferedReader(String filePath, boolean hasHeaders, String separator) {
        this.filePath = filePath;
        this.separator = separator;
        this.hasHeaders = hasHeaders;
    }

    public CsvBufferedReader(String filePath, boolean hasHeaders, String separator, int skipLines) {
        this.filePath = filePath;
        this.separator = separator;
        this.hasHeaders = hasHeaders;
        this.skipLines = skipLines;
    }

    private void initReader() throws IOException {
        if (this.br == null) {
            this.br = new BufferedReader(new FileReader(filePath));
            if (this.hasHeaders) {
                List<String> h = this.readLine();
                if (headers != null) {
                    this.headers.addAll(h);
                }
            }
        }
    }

    /**
     * Lê uma linha aplicando o separador
     *
     * @return
     * @throws IOException
     */
    private List<String> readLine() throws IOException {

        if (this.currentLine < this.skipLines) {
            if (this.skipLines > 0) {
                while (this.currentLine < this.skipLines) {
                    br.readLine();
                    currentLine++;
                }
            }
        }
        String firstLine = br.readLine();
        currentLine++;
        if (firstLine == null) {
            return null;
        }
        String[] d = firstLine.split(this.separator);
        if (d != null) {
            List<String> result = Arrays.asList(d);
            //
            // Aqui a gente pode tratar dados que estejam entre delimitadores
            //
            result.forEach(elemento -> result.set(result.indexOf(elemento), elemento));
            lineCounter.incrementAndGet();
            return result;
        }
        return null;
    }

    /**
     * Pega a próxima linha, retorna null se não houver resultados
     *
     * @return
     * @throws IOException
     */
    public List<String> getNext() throws IOException {
        this.initReader();
        return this.readLine();
    }

    public void forEachAsList(Consumer<? super List<String>> action) throws IOException {
        Objects.requireNonNull(action);
        List<String> fields;
        while ((fields = this.getNext()) != null) {
            action.accept(fields);
        }

    }

    public void forEach(Consumer<? super Map<String, String>> action) throws IOException, IllegalStateException {
        Objects.requireNonNull(action);
        if (!this.hasHeaders) {
            throw new IllegalStateException("Header not present cant return as MAP of <Key,Value>");
        }
        List<String> fields;
        int line = 0;
        while ((fields = this.getNext()) != null) {
            line++;
            int index = 0;
            if (exceptionOnError) {
                if (fields.size() > headers.size()) {
                    System.out.println(String.join(",", fields));
                    throw new IllegalStateException("Collumn Size is Greater than Headers size: Cols: (" + fields.size() + ") Headers:(" + headers.size() + ") On Line:" + line);
                }
            }
            Map<String, String> m = new HashMap<>();
            for (String v : fields) {

                if (index > headers.size() - 1) {
                } else {
                    if (headers.get(index) != null) {
                        m.put(headers.get(index), v);
                    }
                }
                index++;
            }
            action.accept(m);
        }
    }

    public void forEachAsMap(BiConsumer<String, String> action) throws IOException, IllegalStateException {
        Objects.requireNonNull(action);
        if (!this.hasHeaders) {
            throw new IllegalStateException("Header not present cant return as MAP of <Key,Value>");
        }
        List<String> fields;
        int line = 0;
        while ((fields = this.getNext()) != null) {
            line++;
            int index = 0;
            if (exceptionOnError) {
                if (fields.size() > headers.size()) {
                    System.out.println(String.join(",", fields));
                    throw new IllegalStateException("Collumn Size is Greater than Headers size: Cols: (" + fields.size() + ") Headers:(" + headers.size() + ") On Line:" + line);
                }
            }
            for (String v : fields) {
                if (index > headers.size() - 1) {
                } else {
                    if (headers.get(index) != null) {
                        action.accept(headers.get(index), v);
                    }
                }
                index++;
            }
        }

    }

    @Override
    public void close() throws Exception {

        if (this.br != null) {
            try {
                this.br.close();
                System.out.println("Closed Totla Line Read:" + lineCounter.get());
            } catch (IOException ex) {
                throw ex;
            }
        }
    }
}
