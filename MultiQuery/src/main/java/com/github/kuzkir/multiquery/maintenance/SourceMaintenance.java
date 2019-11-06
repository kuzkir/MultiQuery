/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.maintenance;

import com.github.kuzkir.multiquery.entity.DatabaseGroup;
import com.github.kuzkir.multiquery.entity.Source;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author kuzkir
 */
public class SourceMaintenance extends Maintenance<Source> {

    public SourceMaintenance() {
        super(Source.class, new File("source.json"));

    }

    public Set<DatabaseGroup> getGroups() throws IOException {
        return get().getGroups();
    }

    public void addGroup(DatabaseGroup group) throws IOException {
        get().getGroups().add(group);
        save();
    }

    public boolean removeGroup(String title) throws IOException {
        if (get().getGroups().removeIf(a -> a.getTitle().equals(title))) {
            save();
            return true;
        }
        
        return false;
    }

    @Override
    Source createNew() {
        return new Source();
    }
}
