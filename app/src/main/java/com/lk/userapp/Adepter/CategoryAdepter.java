package com.lk.userapp.Adepter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lk.userapp.Model.Category;
import com.lk.userapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdepter extends RecyclerView.Adapter<CategoryAdepter.ViewHolder> {

    private List<Category> categoryList;

    public CategoryAdepter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String icon = categoryList.get(position).getImgUrl();
        String name = categoryList.get(position).getCategoryName();
        holder.setCategoryName(name);
       // holder.setImgUrl(icon);


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUrl;
        private TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUrl = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_price);

        }

        private void setImgUrl(String icon){
            Picasso.with(itemView.getContext()).load(icon).into(imgUrl);
        }

        private void setCategoryName(String name){
            categoryName.setText(name);
        }
    }
}
