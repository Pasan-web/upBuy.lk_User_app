package com.lk.userapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.R;

public class ComplainHolder extends RecyclerView.ViewHolder{

    public ImageView complainImg;
    public ImageView statusImg;
    public TextView reason;
    public TextView datetime;
    public TextView status;

    public ComplainHolder(@NonNull View itemView) {
        super(itemView);
        complainImg = itemView.findViewById(R.id.complain_pic);
        statusImg = itemView.findViewById(R.id.complain_status_img);
        reason = itemView.findViewById(R.id.complain_reason);
        datetime = itemView.findViewById(R.id.complain_time);
        status = itemView.findViewById(R.id.complain_status_lbl);

    }

}