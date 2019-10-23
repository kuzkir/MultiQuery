/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.maintenance;

import com.github.kuzkir.multiquery.entity.Source;
import com.github.kuzkir.multiquery.helper.JsonHelper;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author kuzkir
 */
public class SourceMaintenance {

    private final File file = new File("source.json");
    private Source source;

    public SourceMaintenance() {
        JsonHelper jh = new JsonHelper();
        try {
            source = jh.get(file, Source.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
