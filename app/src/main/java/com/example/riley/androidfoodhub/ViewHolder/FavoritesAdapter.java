package com.example.riley.androidfoodhub.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.riley.androidfoodhub.Common.Common;
import com.example.riley.androidfoodhub.Database.Database;
import com.example.riley.androidfoodhub.FoodDetail;
import com.example.riley.androidfoodhub.FoodList;
import com.example.riley.androidfoodhub.Interface.ItemClickListener;
import com.example.riley.androidfoodhub.Model.Favorites;
import com.example.riley.androidfoodhub.Model.Food;
import com.example.riley.androidfoodhub.Model.Order;
import com.example.riley.androidfoodhub.R;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by riley on 2/14/2018.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    private Context context;
    private List<Favorites> favoritesList;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };



    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favorites_item,parent,false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder viewHolder, final int position) {
        viewHolder.food_name.setText(favoritesList.get(position).getFoodName());
        viewHolder.food_price.setText(String.format("₱ %s",favoritesList.get(position).getFoodPrice().toString()));

        final boolean isExists = new Database(context).checkFoodExists(favoritesList.get(position).getFoodId(),Common.currentUser.getPhone());

        viewHolder.btn_quickAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isExists) {
                        new Database(context).addToCart(new Order(
                                Common.currentUser.getPhone(),
                                favoritesList.get(position).getFoodId(),
                                favoritesList.get(position).getFoodName(),
                                "1",
                                favoritesList.get(position).getFoodPrice(),
                                favoritesList.get(position).getFoodDiscount(),
                                favoritesList.get(position).getFoodImage()
                        ));

                    } else {
                        new Database(context).increaseCart(favoritesList.get(position).getFoodId(), Common.currentUser.getPhone());
                    }
                    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                }
                catch (SQLiteException e) {
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog((Activity) context);
                Picasso.with(context)
                        .load(favoritesList.get(position).getFoodImage())
                        .into(target);
            }
        });

        Picasso.with(context).load(favoritesList.get(position).getFoodImage()).into(viewHolder.food_image);

        final Favorites local = favoritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetail = new Intent(context, FoodDetail.class);
                foodDetail.putExtra("FoodId", favoritesList.get(position).getFoodId());
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public Favorites getItem(int position) {
        return favoritesList.get(position);
    }

    public void removeItem(int position) {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favorites item, int position) {
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }
}
