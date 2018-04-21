package com.example.android.baitbite.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.baitbite.Interface.ItemClickListener;
import com.example.android.baitbite.R;

public class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textViewDishName;
    public ImageView imageViewDish;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DishViewHolder(View itemView) {
        super(itemView);

        textViewDishName = (TextView) itemView.findViewById(R.id.dish_name);
        imageViewDish = (ImageView) itemView.findViewById(R.id.dish_image);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
