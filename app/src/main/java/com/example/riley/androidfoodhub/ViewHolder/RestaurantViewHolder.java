package com.example.riley.androidfoodhub.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.riley.androidfoodhub.Interface.ItemClickListener;
import com.example.riley.androidfoodhub.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtRestaurantName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public RestaurantViewHolder(View itemView) {
        super(itemView);

        txtRestaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
        imageView = (ImageView)itemView.findViewById(R.id.restaurant_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
