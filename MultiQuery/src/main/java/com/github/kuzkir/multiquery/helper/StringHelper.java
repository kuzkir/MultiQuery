/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.helper;

/**
 *
 * @author kuzkir
 */
public class StringHelper {

    public static String endAfterInt(int i) {
        i = Math.abs(i);
        if (i % 100 > 4 && i % 100 < 21) {
            return "ов";
        }
        if (i % 10 == 1) {
            return "";
        }
        if (i % 10 > 1 && i % 10 < 5) {
            return "а";
        }
        return "ов";
    }
}
