package com.community.jboss.leadmanagement.main.groups;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.main.MainFragment;

public class GroupsFragment extends MainFragment {

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // hide contact search item in GroupsFragment
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem importItem = menu.findItem(R.id.action_import);
        searchItem.setVisible(false);
        importItem.setVisible(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public int getTitle() {
        return R.string.title_groups;
    }
}
