package com.lk.userapp.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.R;

public class CategoryHolder extends RecyclerView.ViewHolder {

    public ImageView imgUrl;
    public TextView categoryName;

    public CategoryHolder(@NonNull View itemView) {
        super(itemView);
        imgUrl = itemView.findViewById(R.id.category_icon);
        categoryName = itemView.findViewById(R.id.category_price);

    }
}
