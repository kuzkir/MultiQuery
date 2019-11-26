/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.helper;

import java.util.Base64;

/**
 *
 * @author kuzkir
 */
public class EncodeHelper {
 
    public static String encode(String in) {
        String out = Base64.getEncoder().encodeToString(in.getBytes());        
        return out;
    }
    
    public static String decode(String in) {
        String out = new String(Base64.getDecoder().decode(in));        
        return out;
    }
}
