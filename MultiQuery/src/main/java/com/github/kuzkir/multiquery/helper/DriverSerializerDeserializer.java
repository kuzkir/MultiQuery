/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.helper;

import com.github.kuzkir.fxcontrol.message.MessageBox;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.lang.reflect.Type;
import java.sql.Driver;

/**
 *
 * @author kuzkir
 */
public class DriverSerializerDeserializer implements JsonSerializer<Driver>, JsonDeserializer<Driver>{

    private final static String POSTGRESQL = "postgresql";
    private final static String SQLSERVER = "sqlserver";
    
    @Override
    public JsonElement serialize(Driver src, Type typeOfSrc, JsonSerializationContext context) {
        if(src instanceof org.postgresql.Driver)
            return new JsonPrimitive(POSTGRESQL);
        if(src instanceof SQLServerDriver)
            return new JsonPrimitive(SQLSERVER);
        
        return new JsonPrimitive("");
    }

    @Override
    public Driver deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String data = json.getAsString();
        Driver driver = null;
        try {
            if(data.equals(POSTGRESQL))
            driver = (Driver) Class.forName("org.postgresql.Driver").newInstance();
        else if(data.equals(SQLSERVER))
            driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        }
        catch(Exception e) {
            MessageBox.showException("Загрузка объекта", e);
        }
        
        return driver;
    }
    
}
