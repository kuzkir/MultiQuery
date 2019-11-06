/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Driver;

/**
 *
 * @author kuzkir
 */
public class JsonHelper {

    private final Gson gson;
    
    public JsonHelper() {
        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting()
            .registerTypeAdapter(Driver.class, new DriverSerializerDeserializer());
        
        gson = gb.create();
    }
    
    public <T> T get(File file, Class<T> classOfT) throws IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s not found", file.getAbsolutePath()));
        }

        try (FileReader fr = new FileReader(file)) {
            return gson.fromJson(fr, classOfT);
        }
    }

    public void set(File file, Object obj) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(gson.toJson(obj));
        }
    }
    
}
