package com.example.matsah.baitbite_chef.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.matsah.baitbite_chef.Interface.ItemClickListener;
import com.example.matsah.baitbite_chef.R;

/**
 * Created by MATSAH on 4/7/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{
    public TextView textViewOrderID, textViewOrderStatus, textViewOrderPhone, textViewOrderAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        textViewOrderAddress = (TextView) itemView.findViewById(R.id.textView_order_address);
        textViewOrderID = (TextView) itemView.findViewById(R.id.textView_order_id);
        textViewOrderStatus = (TextView) itemView.findViewById(R.id.textView_order_status);
        textViewOrderPhone = (TextView) itemView.findViewById(R.id.textView_order_phone);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select The Action");

        contextMenu.add(0, 0, getAdapterPosition(), "Update");
        contextMenu.add(0, 1, getAdapterPosition(), "Delete");
    }
}
