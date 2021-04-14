package com.lk.userapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.R;

public class ProductHolder extends RecyclerView.ViewHolder {

    public ImageView productImage;
    public TextView productName;
    public TextView productPrice;
    public TextView productDescription;

    public ProductHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.p_image_view);
        productName = itemView.findViewById(R.id.address_lbl);
        productDescription = itemView.findViewById(R.id.product_description);
        productPrice = itemView.findViewById(R.id.category_price);

    }
}
