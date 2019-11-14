/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.controller;

import com.github.kuzkir.multiquery.entity.Database;
import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import com.github.kuzkir.multiquery.entity.Source;
import com.github.kuzkir.multiquery.maintenance.ConnectionSource;
import com.github.kuzkir.multiquery.maintenance.JSONMaintenance;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author kuzkir
 */
class ConnectionSourceMaintenance extends JSONMaintenance<Source> implements ConnectionSource {

    ConnectionSourceMaintenance() {
        super(Source.class, new File("source.json"));
    }

    @Override
    public Source createNew() {
        return new Source();
    }

    @Override
    public List<String> getGroupList() throws Exception {
        return get().getGroups().stream()
            .sorted((a, b) -> a.getTitle().compareTo(b.getTitle()))
            .map(a -> a.getTitle())
            .collect(Collectors.toList());
    }

    @Override
    public DatabaseGroup getGroupByTitle(String title) throws Exception {
        Optional<DatabaseGroup> group = get().getGroups().stream()
            .filter(a -> a.getTitle().equals(title))
            .findAny();

        if (group.isPresent()) {
            return group.get();
        }
        return null;
    }

    @Override
    public DatabaseGroup addGroup(DatabaseGroup group) throws Exception {
        if (get().getGroups().stream().anyMatch(a -> a.getTitle().equals(group.getTitle()))) {
            return null;
        }

        get().getGroups().add(group);
        save();
        return group;
    }

    @Override
    public DatabaseGroup editGroup(String title, DatabaseGroup group) throws Exception {
        if (get().getGroups().stream().anyMatch(a -> a.getTitle().equals(group.getTitle()))) {
            return null;
        }

        get().getGroups().stream()
            .filter(a -> a.getTitle().equals(title))
            .findAny()
            .ifPresent(a -> a.setTitle(group.getTitle()).setDriver(group.getDriver()));
        return getGroupByTitle(group.getTitle());
    }

    @Override
    public boolean removeGroup(String title) throws Exception {
        boolean b = get().getGroups().removeIf(a -> a.getTitle().equals(title));
        save();
        return b;
    }

    @Override
    public Database addBase(String groupTitle, Database base) throws Exception {
        Optional<DatabaseGroup> group = get().getGroups().stream()
            .filter(a -> a.getTitle().equals(groupTitle))
            .findAny();

        if (!group.isPresent()) {
            return null;
        }

        if (group.get().getDatabases().stream().anyMatch(a -> a.getTitle().equals(base.getTitle()))) {
            return null;
        }

        group.get().getDatabases().add(base);
        save();
        return base;
    }

    @Override
    public Database editBase(String groupTitle, String baseTitle, Database base) throws Exception {
        Optional<DatabaseGroup> group = get().getGroups().stream()
            .filter(a -> a.getTitle().equals(groupTitle))
            .findAny();

        if (!group.isPresent()) {
            return null;
        }

        if (group.get().getDatabases().stream().anyMatch(a -> a.getTitle().equals(base.getTitle()))) {
            return null;
        }

        group.get().getDatabases().stream()
            .filter(a -> a.getTitle().equals(baseTitle))
            .findAny()
            .ifPresent(a -> a.setIsActive(base.getIsActive())
            .setTitle(base.getTitle())
            .setHost(base.getHost())
            .setBase(base.getBase())
            .setUser(base.getUser())
            .setPassword(base.getPassword()));

        save();
        return base;
    }

    @Override
    public boolean removeBase(String groupTitle, String baseTitle) throws Exception {
        boolean b = false;
        Optional<DatabaseGroup> group = get().getGroups().stream()
            .filter(a -> a.getTitle().equals(groupTitle))
            .findAny();
        if (group.isPresent()) {
            b = group.get().getDatabases().removeIf(a -> a.getTitle().equals(baseTitle));
        }
        save();
        return b;
    }
}
