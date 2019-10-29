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

/**
 *
 * @author kuzkir
 */
public class JsonHelper {

    public <T> T get(File file, Class<T> classOfT) throws IOException {

        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s not found", file.getAbsolutePath()));
        }

        try (FileReader fr = new FileReader(file)) {
            Gson gson = new Gson();
            return gson.fromJson(fr, classOfT);
        }
    }

    public void set(File file, Object obj) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileWriter fw = new FileWriter(file)) {
            GsonBuilder gb = new GsonBuilder();
            Gson gson = gb.setPrettyPrinting().create();
            fw.write(gson.toJson(obj));
        }
    }
}
