package com.community.jboss.leadmanagement.data.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.community.jboss.leadmanagement.data.entities.Tag;

import java.util.List;

@Dao
public interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Tag... tags);

    @Query("SELECT * FROM tag")
    List<Tag> getAllTags();
}
