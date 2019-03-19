package com.community.jboss.leadmanagement.data.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by carbonyl on 09/12/2017.
 */
@Entity
public class Tag {
    @PrimaryKey @NonNull
    private final String id;
    @NonNull
    private String tag;

    public Tag(@NonNull String id, @NonNull String tag) {
        this.id = id;
        this.tag = tag;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    @NonNull
    public String getId() {
        return id;
    }
}
