package com.lk.userapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.R;

public class WishListHolder extends RecyclerView.ViewHolder {

public ImageView wishListImg,addToCart,deleteItem;
public TextView itemName,itemPrice,itemTimeDate;

public WishListHolder(@NonNull View itemView) {
        super(itemView);
     wishListImg = itemView.findViewById(R.id.wishlist_image_view);
     addToCart = itemView.findViewById(R.id.add_to_cart_lbl);
     deleteItem = itemView.findViewById(R.id.close_lbl);
     itemName = itemView.findViewById(R.id.wishlist_item_name);
     itemPrice = itemView.findViewById(R.id.wishlist_item_price);
     itemTimeDate = itemView.findViewById(R.id.wishlist_datetime);

    }
}