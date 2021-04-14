package com.lk.userapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.R;

public class CartHolder extends RecyclerView.ViewHolder {

        public ImageView cartItemImg;
        public TextView cartItemName,cartItemPrice,qty,qtyIncrease,qtyReduce;

public CartHolder(@NonNull View itemView) {
        super(itemView);
        cartItemImg = itemView.findViewById(R.id.wishlist_image_view);
        cartItemName = itemView.findViewById(R.id.wishlist_item_name);
        cartItemPrice = itemView.findViewById(R.id.wishlist_item_price);
        qty = itemView.findViewById(R.id.qty_lbl);
        qtyIncrease = itemView.findViewById(R.id.qty_increase);
        qtyReduce = itemView.findViewById(R.id.qty_reduce);

        }
}
