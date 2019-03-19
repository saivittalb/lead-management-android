package com.community.jboss.leadmanagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.community.jboss.leadmanagement.data.entities.Contact;
import com.community.jboss.leadmanagement.main.contacts.ContactsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.community.jboss.leadmanagement.SettingsFragment.PREF_DARK_THEME;


public class CustomDialogBox{

    @BindView(R.id.dialog_yes)
    Button yes;
    @BindView(R.id.dialog_no)
    Button no;
    @BindView(R.id.dialog_layout)
    RelativeLayout layout;
    @BindView(R.id.dialog_txt)
    TextView text;


    private Contact contact;
    private ContactsAdapter adapter;
    private Dialog dialog;

    public void showAlert(Activity activity, final Contact contact, ContactsAdapter adapter){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(dialog.getContext()).inflate(R.layout.custom_dialog_box,null,false);
        ButterKnife.bind(this,view);
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(dialog.getContext());
        this.contact = contact;
        this.adapter = adapter;
        this.dialog = dialog;

        if(mPref.getBoolean(PREF_DARK_THEME,false)){
            layout.setBackgroundColor(Color.parseColor("#303030"));
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.parseColor("#303030"));
            yes.setBackgroundColor(Color.GRAY);
            yes.setTextColor(Color.WHITE);
            no.setTextColor(Color.WHITE);
            no.setBackgroundColor(Color.GRAY);
        }


        dialog.setContentView(view);
        dialog.show();
    }

    @OnClick(R.id.dialog_yes)
    void onYesClicked(){
        adapter.mListener.onContactDeleted(contact);
        dialog.dismiss();
    }

    @OnClick(R.id.dialog_no)
    void onNoClicked(){
        dialog.dismiss();
    }

}
