package com.community.jboss.leadmanagement.main;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

public abstract class MainFragment extends Fragment {
    public abstract @StringRes
    int getTitle();
}
