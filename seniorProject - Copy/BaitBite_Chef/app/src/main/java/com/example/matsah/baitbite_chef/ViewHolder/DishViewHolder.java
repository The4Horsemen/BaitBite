package com.example.matsah.baitbite_chef.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.Interface.ItemClickListener;
import com.example.matsah.baitbite_chef.R;

/**
 * Created by MATSAH on 4/1/2018.
 */

public class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView DishName;
    public ImageView DishImage;

    private ItemClickListener itemClicListener;

    public DishViewHolder(View itemView) {
        super(itemView);

        DishName = (TextView) itemView.findViewById(R.id.dish_name);
        DishImage = (ImageView) itemView.findViewById(R.id.dish_image);


        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClicListener(ItemClickListener itemClicListener) {
        this.itemClicListener = itemClicListener;
    }

    @Override
    public void onClick(View view) {
        itemClicListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
