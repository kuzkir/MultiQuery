/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.entity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kuzkir
 */
public class Source {

    private List<DatabaseGroup> groups;

    public Source() {
        groups = new ArrayList<>();
    }
    
    public List<DatabaseGroup> getGroups() {
        return this.groups;
    }

    public Source setGroups(List<DatabaseGroup> groups) {
        this.groups = groups;
        return this;
    }
}
