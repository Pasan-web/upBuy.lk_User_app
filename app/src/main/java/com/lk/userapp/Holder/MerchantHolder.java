package com.lk.userapp.Holder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.Model.Merchant;
import com.lk.userapp.ProductActivity;
import com.lk.userapp.R;

public class MerchantHolder extends RecyclerView.ViewHolder {

    public ImageView imgUrl;
    public TextView merchantName;
    public Merchant model;

    public MerchantHolder(@NonNull View itemView) {
        super(itemView);
        imgUrl = itemView.findViewById(R.id.merchant_img);
        merchantName = itemView.findViewById(R.id.merchant_name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ProductActivity.class);
                intent.putExtra("merchantName",model.getMerchantName()+"");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                itemView.getContext().startActivity(intent);
            }
        });

    }
}
