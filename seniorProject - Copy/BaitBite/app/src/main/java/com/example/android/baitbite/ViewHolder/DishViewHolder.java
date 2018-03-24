package com.example.android.baitbite.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.baitbite.Interface.ItemClicListener;
import com.example.android.baitbite.R;

/**
 * Created by janbi on 3/24/2018.
 */

public class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textViewDishName;
    public ImageView imageViewDish;

    private ItemClicListener itemClicListener;

    public void setItemClicListener(ItemClicListener itemClicListener) {
        this.itemClicListener = itemClicListener;
    }

    public DishViewHolder(View itemView) {
        super(itemView);

        textViewDishName = (TextView) itemView.findViewById(R.id.dish_name);
        imageViewDish = (ImageView) itemView.findViewById(R.id.dish_image);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClicListener.onClick(view, getAdapterPosition(), false);
    }
}
