package com.example.riley.androidfoodhub.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.battleent.ribbonviews.RibbonLayout;
import com.example.riley.androidfoodhub.FoodList;
import com.example.riley.androidfoodhub.Interface.ItemClickListener;
import com.example.riley.androidfoodhub.R;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;

/**
 * Created by riley on 12/11/2017.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,OnLikeListener,OnAnimationEndListener {
    public RibbonLayout ribbonLayout;
    public TextView food_name, food_price;
    public ImageView food_image, btn_quickAdd,btnShare;
    public LikeButton fav;

    private ItemClickListener itemClickListener;
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public FoodViewHolder(View itemView) {
        super(itemView);
        ribbonLayout = (RibbonLayout)itemView.findViewById(R.id.ribbonLayout);
        food_name = (TextView) itemView.findViewById(R.id.food_name);
        food_image = (ImageView)itemView.findViewById(R.id.food_image);
        food_price = (TextView)itemView.findViewById(R.id.food_price);
        btn_quickAdd = (ImageView) itemView.findViewById(R.id.btn_quickAdd);
        fav = (LikeButton) itemView.findViewById(R.id.fav);
        fav.setOnLikeListener(this);
        fav.setOnAnimationEndListener(this);
        btnShare = (ImageView) itemView.findViewById(R.id.btnShare);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onAnimationEnd(LikeButton likeButton) {
    }

    @Override
    public void liked(LikeButton likeButton) {

    }

    @Override
    public void unLiked(LikeButton likeButton) {

    }
}
