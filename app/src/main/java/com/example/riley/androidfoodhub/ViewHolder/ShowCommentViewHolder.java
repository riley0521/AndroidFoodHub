package com.example.riley.androidfoodhub.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.riley.androidfoodhub.R;

/**
 * Created by riley on 1/23/2018.
 */

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserName, txtComment;
    public RatingBar ratingBar;

    public ShowCommentViewHolder(View itemView) {
        super(itemView);

        txtUserName = (TextView)itemView.findViewById(R.id.txtUserName);
        txtComment = (TextView)itemView.findViewById(R.id.txtComment);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBarComment);
    }
}
