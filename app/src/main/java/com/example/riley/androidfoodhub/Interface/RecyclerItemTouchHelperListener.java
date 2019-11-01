package com.example.riley.androidfoodhub.Interface;

import android.support.v7.widget.RecyclerView;

/**
 * Created by riley on 2/14/2018.
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
