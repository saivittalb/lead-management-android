package com.community.jboss.leadmanagement.main.callrecord;

import android.graphics.Color;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.community.jboss.leadmanagement.R;
import com.community.jboss.leadmanagement.data.models.CallRecords;

import java.io.File;
import java.util.ArrayList;


public class CallRecordsAdapter extends RecyclerView.Adapter<CallRecordsAdapter.ViewHolder> {
    private ArrayList<CallRecords> list;
    public MyAdapterListener onClickListener;
    public Boolean useDarkTheme;

    public CallRecordsAdapter(ArrayList<CallRecords> list, MyAdapterListener onClickListener, Boolean useDarkTheme) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.useDarkTheme = useDarkTheme;
    }

    public CallRecordsAdapter(ArrayList<CallRecords> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recorded_calls_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (useDarkTheme) {
            holder.card.setCardBackgroundColor(Color.parseColor("#212121"));
        }
        holder.phone_card.setImageResource(R.drawable.round_phone_in_talk_white_48);
        holder.MobileNumber.setText(list.get(position).getNumber());
        holder.MobileNumber.setTextColor(Color.WHITE);
        holder.time.setText(list.get(position).getTime());
        holder.backupstatus.setImageResource(R.drawable.round_cloud_upload_white_48);
        if (list.get(position).getDriveid() != null) {
            holder.backupstatus.setImageResource(R.drawable.round_cloud_done_white_48);
            if (!new File(list.get(position).getLocalpath()).exists()) {
                holder.backupstatus.setImageResource(R.drawable.round_cloud_download_white_48);
            }

        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView MobileNumber, time;
        public ImageView playbutton;
        public ImageView backupstatus;
        public MaterialCardView card;
        public ImageView phone_card;

        public ViewHolder(View itemView) {
            super(itemView);
            phone_card = itemView.findViewById(R.id.card_phone);
            card = itemView.findViewById(R.id.calls_card_cell);
            MobileNumber = itemView.findViewById(R.id.mobilenumber_textview);
            playbutton = itemView.findViewById(R.id.playbutton);
            time = itemView.findViewById(R.id.time_textview);
            playbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.playbuttonOnClick(v, getAdapterPosition());
                }
            });
            backupstatus = itemView.findViewById(R.id.backup_status);
            backupstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.backupstatusOnClick(v, getAdapterPosition());
                }
            });
        }


    }


    public interface MyAdapterListener {

        void playbuttonOnClick(View v, int position);

        void backupstatusOnClick(View v, int position);
    }
}
