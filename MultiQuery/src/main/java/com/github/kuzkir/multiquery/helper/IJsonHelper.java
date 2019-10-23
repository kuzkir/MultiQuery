/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.helper;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author kuzkir
 */
public interface IJsonHelper {

    <T> T get(File file, Class<T> classOfT) throws IOException;

    void set(File file, Object obj) throws IOException;
}
