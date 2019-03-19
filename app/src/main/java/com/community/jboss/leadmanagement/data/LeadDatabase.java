package com.community.jboss.leadmanagement.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.community.jboss.leadmanagement.data.daos.ContactDao;
import com.community.jboss.leadmanagement.data.daos.ContactNumberDao;
import com.community.jboss.leadmanagement.data.daos.ContactTagJoinDao;
import com.community.jboss.leadmanagement.data.daos.TagDao;
import com.community.jboss.leadmanagement.data.entities.Contact;
import com.community.jboss.leadmanagement.data.entities.ContactNumber;
import com.community.jboss.leadmanagement.data.entities.ContactTagJoin;
import com.community.jboss.leadmanagement.data.entities.Tag;

@Database(entities = {
        Contact.class,
        ContactNumber.class,
        Tag.class,
        ContactTagJoin.class,
}, version = 1, exportSchema = false)
public abstract class LeadDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "leadmanagement.db";

    private static volatile LeadDatabase sInstance;

    public static synchronized LeadDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    LeadDatabase.class,
                    DATABASE_NAME
                    // TODO: Disallow main thread queries
            ).allowMainThreadQueries().build();
        }
        return sInstance;
    }

    public abstract ContactDao getContactDao();

    public abstract ContactNumberDao getContactNumberDao();

    public abstract TagDao getTagDao();

    public abstract ContactTagJoinDao getContactTagJoinDao();
}
