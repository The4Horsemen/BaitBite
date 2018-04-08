package com.example.android.baitbite.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Interface.ItemClickListener;
import com.example.android.baitbite.Model.Order;
import com.example.android.baitbite.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> orderList = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return  new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder().buildRound(""+orderList.get(position).getQuantity(), Color.RED);
        holder.imageView_cart_count.setImageDrawable(drawable);

        Locale  locale = new Locale("en", "US");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(orderList.get(position).getPrice()))*(Integer.parseInt(orderList.get(position).getQuantity()));
        holder.textView_cart_price.setText(numberFormat.format(price));

        holder.textView_cart_name.setText(orderList.get(position).getDishName());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView textView_cart_name, textView_cart_price;
    public ImageView imageView_cart_count;

    private ItemClickListener itemClickListener;

    public void setTextView_cart_name(TextView textView_cart_name) {
        this.textView_cart_name = textView_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);

        textView_cart_name = (TextView) itemView.findViewById(R.id.textView_cart_item_name);
        textView_cart_price = (TextView) itemView.findViewById(R.id.textView_cart_item_price);
        imageView_cart_count = (ImageView) itemView.findViewById(R.id.imageView_cart_item_count);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Action: ");
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
