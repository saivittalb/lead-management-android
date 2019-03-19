package com.community.jboss.leadmanagement;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.community.jboss.leadmanagement.main.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String PREF_DARK_THEME = "dark_theme";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_REQUEST_ID_TOKEN")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preferences);



        if(getActivity()!=null) {
            Activity mActivity = getActivity();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            final SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
            final String currentServer = sharedPref.getString(getString(R.string.saved_server_ip), "https://github.com/jboss-outreach");
            final EditTextPreference mPreference = (EditTextPreference) findPreference("server_location");
            final SwitchPreference mToggleMode = (SwitchPreference) findPreference("dark_theme");
            final Preference login = findPreference("signInButton");
            final Preference signOut = findPreference("sign_out");

            mAuth.addAuthStateListener(firebaseAuth -> {
                if(mAuth.getCurrentUser() == null) {
                    signOut.setVisible(false);
                    login.setVisible(true);
                    login.setEnabled(true);
                } else {
                    signOut.setVisible(true);
                    signOut.setEnabled(true);
                    login.setVisible(false);
                }
            });


            login.setOnPreferenceClickListener(preference -> {
                signIn();
                return true;
            });

            mToggleMode.setOnPreferenceChangeListener( (preference, newValue) -> {
                mActivity.startActivity(new Intent(mActivity, MainActivity.class));
                mActivity.finish();
                return true;
            });

            signOut.setOnPreferenceClickListener( preference ->  {
                signOut();
                return true;
            });
            mPreference.setSummary(currentServer);
            mPreference.setText(currentServer);

        }
    }

    public int getTitle() {
        return R.string.action_settings;
    }

    private void signIn() {
        Intent signInIntent = new Intent(getContext(), SignActivity.class);
        startActivity(signInIntent);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut();
    }
}
