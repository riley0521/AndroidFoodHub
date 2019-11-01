package com.example.riley.androidfoodhub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.riley.androidfoodhub.Common.Common;
import com.example.riley.androidfoodhub.Interface.ItemClickListener;
import com.example.riley.androidfoodhub.Model.Category;
import com.example.riley.androidfoodhub.Model.Restaurant;
import com.example.riley.androidfoodhub.ViewHolder.MenuViewHolder;
import com.example.riley.androidfoodhub.ViewHolder.RestaurantViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RestaurantList extends AppCompatActivity {

    AlertDialog waitingDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseRecyclerOptions<Restaurant> options = new FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Restaurants"),Restaurant.class)
            .build();
    FirebaseRecyclerAdapter<Restaurant,RestaurantViewHolder> adapter = new FirebaseRecyclerAdapter<Restaurant,RestaurantViewHolder>(options) {

        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item,parent,false);
            return new RestaurantViewHolder(itemView);
        }

        @Override
        protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull Restaurant model) {
            holder.txtRestaurantName.setText(model.getName());
            Picasso.with(getBaseContext()).load(model.getImage()).into(holder.imageView);
            final Restaurant clickItem = model;
            holder.setItemClickListener(new ItemClickListener() {
                @Override

                public void onClick(View view, int position, boolean isLongClick) {
                    Intent foodList = new Intent(RestaurantList.this,Home.class);
                    Common.restaurantSelected=adapter.getRef(position).getKey();
                    startActivity(foodList);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadRestaurant();
                    if(adapter == null) {
                        Toast.makeText(RestaurantList.this, "No Foods", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadRestaurant();
                    if(adapter == null) {
                        Toast.makeText(RestaurantList.this, "No Foods", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadRestaurant() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
