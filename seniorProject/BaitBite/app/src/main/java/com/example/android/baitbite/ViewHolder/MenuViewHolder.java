package com.example.android.baitbite.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.baitbite.Interface.ItemClicListener;
import com.example.android.baitbite.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView textViewMenuName;
    public ImageView imageView;

    private ItemClicListener itemClicListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        textViewMenuName = (TextView) itemView.findViewById(R.id.menu_name);
        imageView = (ImageView) itemView.findViewById(R.id.menu_image);

        itemView.setOnClickListener(this);

    }

    public void setItemClicListener(ItemClicListener itemClicListener) {
        this.itemClicListener = itemClicListener;
    }

    @Override
    public void onClick(View view) {
        itemClicListener.onClick(view, getAdapterPosition(), false);
    }
}
