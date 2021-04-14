package com.lk.userapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.R;

public class AddressHolder extends RecyclerView.ViewHolder {

public ImageView deleteItemImg;
public TextView address;

public AddressHolder(@NonNull View itemView) {
        super(itemView);

        deleteItemImg = itemView.findViewById(R.id.delet_lbl);
        address = itemView.findViewById(R.id.address_lbl);


    }
}
