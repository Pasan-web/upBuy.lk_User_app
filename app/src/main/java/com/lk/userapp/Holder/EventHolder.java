package com.lk.userapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.R;

public class EventHolder extends RecyclerView.ViewHolder{


    public TextView title;
    public TextView description;
    public TextView merchant;
    public ImageView eventImage;
    public ImageView publishIcon;
    public TextView dateTime;

    public EventHolder(@NonNull View itemView) {
        super(itemView);
        eventImage = itemView.findViewById(R.id.event_image_view);
        publishIcon = itemView.findViewById(R.id.status_lbl);
        title = itemView.findViewById(R.id.order_id);
        merchant = itemView.findViewById(R.id.order_date);
        dateTime = itemView.findViewById(R.id.order_amount);
        description = itemView.findViewById(R.id.complain_time);
    }

}