package com.community.jboss.leadmanagement.utils;

import android.content.Context;

import com.community.jboss.leadmanagement.data.LeadDatabase;
import com.community.jboss.leadmanagement.data.daos.ContactDao;
import com.community.jboss.leadmanagement.data.daos.ContactNumberDao;

public class DbUtil {
    private DbUtil() {
        // Prevent this util class from being instantiated
    }

    public static ContactDao contactDao(Context context) {
        final LeadDatabase database = LeadDatabase.getInstance(context);
        return database.getContactDao();
    }

    public static ContactNumberDao contactNumberDao(Context context) {
        final LeadDatabase database = LeadDatabase.getInstance(context);
        return database.getContactNumberDao();
    }
}
