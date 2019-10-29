/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.entity;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author kuzkir
 */
public class Source {

    private Set<DatabaseGroup> groups;

    public Source() {
        groups = new HashSet<>();
    }
    
    public Set<DatabaseGroup> getGroups() {
        return this.groups;
    }

    public Source setGroups(Set<DatabaseGroup> groups) {
        this.groups = groups;
        return this;
    }
}
