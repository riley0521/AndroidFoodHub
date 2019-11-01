package com.example.riley.androidfoodhub;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.riley.androidfoodhub.Common.Common;
import com.example.riley.androidfoodhub.Database.Database;
import com.example.riley.androidfoodhub.Interface.ItemClickListener;
import com.example.riley.androidfoodhub.Model.Favorites;
import com.example.riley.androidfoodhub.Model.Food;
import com.example.riley.androidfoodhub.Model.Order;
import com.example.riley.androidfoodhub.ViewHolder.FoodViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity implements OnLikeListener, OnAnimationEndListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId="";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    Database localDB;

    SwipeRefreshLayout swipeRefreshLayout;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target = new Target() {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Common.restaurantSelected).child("detail").child("Food");
        localDB = new Database(this);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null) {
                    categoryId = getIntent().getStringExtra("CategoryId");
                }
                if(!categoryId.isEmpty() && categoryId != null) {
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        loadListFood(categoryId);
                        if(adapter == null) {
                            Toast.makeText(FoodList.this, "No Foods", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(FoodList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null) {
                    categoryId = getIntent().getStringExtra("CategoryId");
                }
                if(!categoryId.isEmpty() && categoryId != null) {
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        loadListFood(categoryId);
                        if(adapter == null) {
                            Toast.makeText(FoodList.this, "No Foods", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(FoodList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_slide_from_bottom);
        recyclerView.setLayoutAnimation(controller);

        if(getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null) {
            if(Common.isConnectedToInternet(getBaseContext())) {
                loadListFood(categoryId);
                if(adapter == null) {
                    Toast.makeText(FoodList.this, "No Foods", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(FoodList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search Food");
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList) {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        loadSuggest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.startListening();
        }
    }

    private void startSearch(CharSequence text) {
        Query searchByName = foodList.orderByChild("name").equalTo(text.toString());
        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>().setQuery(searchByName,Food.class).build();
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {

                if(model.getType() == 0) {
                    viewHolder.ribbonLayout.setShowHeader(true);
                    viewHolder.ribbonLayout.setHeaderRibbonColor(Color.parseColor("#f27762"));
                    viewHolder.ribbonLayout.setHeaderTextColor(Color.parseColor("#FFFFFF"));
                    viewHolder.ribbonLayout.setHeaderText(model.getHeaderText());

                    viewHolder.food_name.setText(model.getName());
                    viewHolder.food_price.setText(String.format("₱ %s",model.getPrice().toString()));
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                }

                else if(model.getType() == 1) {
                    viewHolder.ribbonLayout.setShowHeader(true);
                    viewHolder.ribbonLayout.setHeaderRibbonColor(Color.parseColor("#FF0000"));
                    viewHolder.ribbonLayout.setHeaderTextColor(Color.parseColor("#FFFFFF"));
                    viewHolder.ribbonLayout.setHeaderText(model.getHeaderText());

                    viewHolder.food_name.setText(model.getName());
                    viewHolder.food_price.setText(String.format("₱ %s",model.getPrice().toString()));
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                }

                else {
                    viewHolder.ribbonLayout.setShowHeader(false);

                    viewHolder.food_name.setText(model.getName());
                    viewHolder.food_price.setText(String.format("₱ %s",model.getPrice().toString()));
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                }

                if(localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.fav.setLikeDrawableRes(R.drawable.heart_on);

                final Favorites favorites = new Favorites();
                favorites.setFoodId(adapter.getRef(position).getKey());
                favorites.setFoodName(model.getName());
                favorites.setFoodDescription(model.getDescription());
                favorites.setFoodDiscount(model.getDiscount());
                favorites.setFoodImage(model.getImage());
                favorites.setFoodMenuId(model.getMenuId());
                favorites.setUserPhone(Common.currentUser.getPhone());
                favorites.setFoodPrice(model.getPrice());

                viewHolder.fav.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        if(!localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone())) {
                            localDB.addToFavorites(favorites);
                            likeButton.setLiked(true);
                            Toast.makeText(FoodList.this, model.getName() + " was added to Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        localDB.removeFromFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                        likeButton.setLiked(false);
                        Toast.makeText(FoodList.this, model.getName() + " was removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                final boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone());

                viewHolder.btn_quickAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!isExists) {
                                new Database(getBaseContext()).addToCart(new Order(
                                        Common.currentUser.getPhone(),
                                        adapter.getRef(position).getKey(),
                                        model.getName(),
                                        "1",
                                        model.getPrice(),
                                        model.getDiscount(),
                                        model.getImage()
                                ));

                            } else {
                                new Database(getBaseContext()).increaseCart(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                            }
                            Toast.makeText(FoodList.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        }
                        catch (SQLiteException e) {
                            Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }
            private void loadSuggest() {
                foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            private void loadListFood(String categoryId) {
                Query searchByName = foodList.orderByChild("menuId").equalTo(categoryId);
                FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>().setQuery(searchByName,Food.class).build();
                adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {


                        if(model.getType() == 0) {
                            viewHolder.ribbonLayout.setShowHeader(true);
                            viewHolder.ribbonLayout.setHeaderRibbonColor(Color.parseColor("#f27762"));
                            viewHolder.ribbonLayout.setHeaderTextColor(Color.parseColor("#FFFFFF"));
                            viewHolder.ribbonLayout.setHeaderText(model.getHeaderText());

                            viewHolder.food_name.setText(model.getName());
                            viewHolder.food_price.setText(String.format("₱ %s",model.getPrice().toString()));
                            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                        }

                        else if(model.getType() == 1) {
                            viewHolder.ribbonLayout.setShowHeader(true);
                            viewHolder.ribbonLayout.setHeaderRibbonColor(Color.parseColor("#FF0000"));
                            viewHolder.ribbonLayout.setHeaderTextColor(Color.parseColor("#FFFFFF"));
                            viewHolder.ribbonLayout.setHeaderText(model.getHeaderText());

                            viewHolder.food_name.setText(model.getName());
                            viewHolder.food_price.setText(String.format("₱ %s",model.getPrice().toString()));
                            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                        }

                        else {
                            viewHolder.ribbonLayout.setShowHeader(false);

                            viewHolder.food_name.setText(model.getName());
                            viewHolder.food_price.setText(String.format("₱ %s",model.getPrice().toString()));
                            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                        }

                        if(localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                            viewHolder.fav.setLiked(true);

                        final Favorites favorites = new Favorites();
                        favorites.setFoodId(adapter.getRef(position).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodDescription(model.getDescription());
                        favorites.setFoodDiscount(model.getDiscount());
                        favorites.setFoodImage(model.getImage());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setUserPhone(Common.currentUser.getPhone());
                        favorites.setFoodPrice(model.getPrice());

                        viewHolder.fav.setOnLikeListener(new OnLikeListener() {
                            @Override
                            public void liked(LikeButton likeButton) {
                                if(!localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone())) {
                                    localDB.addToFavorites(favorites);
                                    likeButton.setLiked(true);
                                    Toast.makeText(FoodList.this, model.getName() + " was added to Favorites", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void unLiked(LikeButton likeButton) {
                                localDB.removeFromFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                                likeButton.setLiked(false);
                                Toast.makeText(FoodList.this, model.getName() + " was removed from Favorites", Toast.LENGTH_SHORT).show();
                            }
                        });
                        viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Picasso.with(getApplicationContext())
                                        .load(model.getImage())
                                        .into(target);
                            }
                        });

                        final boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(),Common.currentUser.getPhone());

                            viewHolder.btn_quickAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (!isExists) {
                                            new Database(getBaseContext()).addToCart(new Order(
                                                    Common.currentUser.getPhone(),
                                                    adapter.getRef(position).getKey(),
                                                    model.getName(),
                                                    "1",
                                                    model.getPrice(),
                                                    model.getDiscount(),
                                                    model.getImage()
                                            ));

                                        } else {
                                            new Database(getBaseContext()).increaseCart(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                                        }
                                        Toast.makeText(FoodList.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (SQLiteException e) {
                                        Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });




                        final Food local = model;
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                                foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                                startActivity(foodDetail);
                            }
                        });
                    }

                    @Override
                    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
                        return new FoodViewHolder(itemView);
                    }
                };
                adapter.startListening();
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
            }

    @Override
    protected void onStop() {
        super.onStop();
//        searchAdapter.stopListening();
        adapter.stopListening();
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
