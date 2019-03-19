package com.community.jboss.leadmanagement.main;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.community.jboss.leadmanagement.BaseActivity;
import com.community.jboss.leadmanagement.PermissionManager;
import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.SettingsFragment;
import com.community.jboss.leadmanagement.main.callrecord.CallRecordFragment;
import com.community.jboss.leadmanagement.main.contacts.ContactsFragment;
import com.community.jboss.leadmanagement.main.contacts.editcontact.EditContactActivity;
import com.community.jboss.leadmanagement.main.contacts.importcontact.ImportContactActivity;
import com.community.jboss.leadmanagement.main.groups.GroupsFragment;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

import static com.community.jboss.leadmanagement.SettingsFragment.PREF_DARK_THEME;

public class MainActivity extends BaseActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    RelativeLayout drawer;
    @BindView(R.id.nav_view)
    BottomNavigationView navigationView;

    private FirebaseAuth mAuth;


    private MainActivityViewModel mViewModel;
    private PermissionManager permissionManager;

    public static boolean useDarkTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_BG);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mViewModel.getSelectedNavItem().observe(this, this::displayNavigationItem);


        mAuth = FirebaseAuth.getInstance();


        permissionManager = new PermissionManager(this, this);

        int ID = permissionManager.checkAndAskPermissions(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        navigationView.setOnNavigationItemSelectedListener(this);


        // Set initial selected item to Contacts
        if (savedInstanceState == null) {
            selectInitialNavigationItem();
        }

        initFab();
        Crashlytics.log("Happy GCI 2018");
    }

    private void selectInitialNavigationItem() {
        final @IdRes int initialItem = R.id.nav_contacts;
        onNavigationItemSelected(navigationView.getMenu().findItem(initialItem));
        navigationView.setSelectedItemId(initialItem);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_import ){

            // Disable this action if user is not signed in...
            if(mAuth.getCurrentUser() == null){
                Toast.makeText(this, R.string.not_signed, Toast.LENGTH_SHORT).show();
                return false;
            }

            if(permissionManager.permissionStatus(Manifest.permission.READ_CONTACTS)){
                startActivity(new Intent(MainActivity.this,ImportContactActivity.class));
            }else{
                permissionManager.requestPermission(109,Manifest.permission.READ_CONTACTS);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final MainActivityViewModel.NavigationItem navigationItem;
        fab.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(200);
        switch (item.getItemId()) {
            case R.id.nav_contacts:
                navigationItem = MainActivityViewModel.NavigationItem.CONTACTS;
                break;
            case R.id.nav_groups:
                navigationItem = MainActivityViewModel.NavigationItem.GROUPS;
                break;
            case R.id.nav_callrecordings:
                fab.animate().cancel();
                fab.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(200);
                navigationItem = MainActivityViewModel.NavigationItem.RECORDEDCALLS;
                break;
            case R.id.nav_settings:
                fab.animate().cancel();
                fab.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(200);
                navigationItem = MainActivityViewModel.NavigationItem.SETTINGS;
                break;
            default:
                Timber.e("Failed to resolve selected navigation item id");
                throw new IllegalArgumentException();

        }
        mViewModel.setSelectedNavItem(navigationItem);

        return true;
    }

    private void displayNavigationItem(MainActivityViewModel.NavigationItem navigationItem) {
        MainFragment newFragment = null;

        switch (navigationItem) {
            case CONTACTS:
                newFragment = new ContactsFragment();
                break;
            case GROUPS:
                newFragment = new GroupsFragment();
                break;
            case RECORDEDCALLS:
                newFragment= new CallRecordFragment();
                break;
            case SETTINGS:
                SettingsFragment fragment = new SettingsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                setTitle(fragment.getTitle());
                break;
            default:
                Timber.e("Failed to resolve selected NavigationItem");
                throw new IllegalArgumentException();
        }
        if(newFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, newFragment)
                    .commit();
            setTitle(newFragment.getTitle());
        }
    }


    public void initFab() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof ContactsFragment) {
            fab.setOnClickListener(view -> {
                if(mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), EditContactActivity.class));
                }else {
                    Toast.makeText(this, R.string.not_signed, Toast.LENGTH_SHORT).show();
                }
            });
            fab.setImageResource(R.drawable.ic_add_white_24dp);
        }
    }
}
