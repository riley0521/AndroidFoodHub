package com.example.riley.androidfoodhub.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.riley.androidfoodhub.Interface.ItemClickListener;
import com.example.riley.androidfoodhub.R;

/**
 * Created by riley on 2/14/2018.
 */

public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView food_name, food_price;
    public ImageView food_image, btn_quickAdd, btnShare;

    private ItemClickListener itemClickListener;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public FavoriteViewHolder(View itemView) {
        super(itemView);
        food_name = (TextView) itemView.findViewById(R.id.food_name);
        food_image = (ImageView)itemView.findViewById(R.id.food_image);
        food_price = (TextView)itemView.findViewById(R.id.food_price);
        btn_quickAdd = (ImageView) itemView.findViewById(R.id.btn_quickAdd);
        btnShare = itemView.findViewById(R.id.btnShare);

        view_background =itemView.findViewById(R.id.view_background);
        view_foreground = itemView.findViewById(R.id.view_foreground);


        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
