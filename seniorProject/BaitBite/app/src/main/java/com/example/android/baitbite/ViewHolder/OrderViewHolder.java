package com.example.android.baitbite.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.android.baitbite.Interface.ItemClickListener;
import com.example.android.baitbite.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView textViewOrderID, textViewOrderStatus, textViewOrderPhone, textViewOrderAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        textViewOrderAddress = (TextView) itemView.findViewById(R.id.textView_order_address);
        textViewOrderID = (TextView) itemView.findViewById(R.id.textView_order_id);
        textViewOrderStatus = (TextView) itemView.findViewById(R.id.textView_order_status);
        textViewOrderPhone = (TextView) itemView.findViewById(R.id.textView_order_phone);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
      // itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
