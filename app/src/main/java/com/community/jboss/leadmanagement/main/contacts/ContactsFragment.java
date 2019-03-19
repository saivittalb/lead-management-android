package com.community.jboss.leadmanagement.main.contacts;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.data.entities.Contact;
import com.community.jboss.leadmanagement.main.MainActivity;
import com.community.jboss.leadmanagement.main.MainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ContactsFragment extends MainFragment implements ContactsAdapter.AdapterListener, SearchView.OnQueryTextListener {

    @BindView(R.id.contact_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.text_no_result)
    TextView textView;

    private Unbinder mUnbinder;
    private ContactsFragmentViewModel mViewModel;
    private ContactsAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        setHasOptionsMenu(true);
        mUnbinder = ButterKnife.bind(this, view);

        mViewModel = ViewModelProviders.of(this).get(ContactsFragmentViewModel.class);
        mViewModel.getContacts().observe(this, contacts -> {
            mAdapter.replaceData(contacts);
        });

        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.initFab();
        }

        mAdapter = new ContactsAdapter(this);
        recyclerView.setAdapter(mAdapter);

        textView.setVisibility(View.GONE);

        swipeToRefresh.setOnRefreshListener(() -> {
            mAdapter.replaceData(mViewModel.getContacts().getValue());
            swipeToRefresh.setRefreshing(false);
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItem importItem = menu.findItem(R.id.action_import);
        importItem.setVisible(true);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryRefinementEnabled(false);
        searchView.setOnQueryTextListener(this);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if(item==searchMenuItem){
                    mAdapter.getFilter().filter(searchView.getQuery());
                    if( mAdapter.getDataSize() == 0){
                        textView.setVisibility(View.VISIBLE);
                    } else{
                        textView.setVisibility(View.GONE);
                    }
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(item==searchMenuItem){
                    mAdapter.getFilter().filter("");
                    textView.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }

    @Override
    public int getTitle() {
        return R.string.title_contacts;
    }

    @Override
    public void onContactDeleted(Contact contact) {
        mViewModel.deleteContact(contact);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        if (mAdapter.getDataSize() == 0){
            textView.setVisibility(View.VISIBLE);
        }
        else {
            textView.setVisibility(View.GONE);
        }
        return true;
    }
}
