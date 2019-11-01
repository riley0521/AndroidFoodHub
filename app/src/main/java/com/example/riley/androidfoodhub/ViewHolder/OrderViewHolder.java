package com.example.riley.androidfoodhub.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.riley.androidfoodhub.Interface.ItemClickListener;
import com.example.riley.androidfoodhub.R;

import info.hoang8f.widget.FButton;

/**
 * Created by riley on 12/13/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress,
    txtOrderPM,
    txtOrderPS;
    public FButton btnDetail;

    private ItemClickListener itemClickListener;
    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPM = (TextView) itemView.findViewById(R.id.order_pM);
        txtOrderPS = (TextView) itemView.findViewById(R.id.order_pS);
        btnDetail = (FButton) itemView.findViewById(R.id.btnDetail);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
