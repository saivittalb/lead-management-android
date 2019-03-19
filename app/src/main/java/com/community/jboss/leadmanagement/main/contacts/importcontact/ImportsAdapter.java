package com.community.jboss.leadmanagement.main.contacts.importcontact;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.jboss.leadmanagement.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class ImportsAdapter extends RecyclerView.Adapter<ImportsAdapter.ViewHolder> {
    private List<ImportContact> mDataset;
    List<ImportContact> contactsToImport = new ArrayList<>();


    final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.impContactImg)
        ImageView contactImg;
        @BindView(R.id.impContactName)
        TextView name;
        @BindView(R.id.impContactNumber)
        TextView number;

        private ImportContact mContact;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
            v.setOnClickListener(this);
        }

        void bind(ImportContact contact){
            this.mContact = contact;
            name.setText(contact.getName());
            number.setText(contact.getNumber());
        }


        @Override
        public void onClick(View v) {
            if(mContact.isChecked()){
                contactsToImport.remove(mContact);
                mContact.setChecked(false);
                contactImg.setBackgroundResource(R.drawable.ic_unchecked_24dp);
            }else{
                contactsToImport.add(mContact);
                mContact.setChecked(true);
                contactImg.setBackgroundResource(R.drawable.ic_checked_24dp);
            }
        }
    }


    ImportsAdapter(List<ImportContact> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ImportsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.import_contact_cell, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<ImportContact> getContactsToImport(){
        return this.contactsToImport;
    }
}

