package com.example.android.baitbite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.baitbite.Database.Database;
import com.example.android.baitbite.Model.Order;
import com.example.android.baitbite.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class CartActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference request;

    RecyclerView recyclerView_order_dishes;
    RecyclerView.LayoutManager layoutManager;

    TextView textView_total;
    FButton fButton_placeOrder;

    List<Order> cartList = new ArrayList<>();

    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        request = firebaseDatabase.getReference("Request");

        //Load the data from Firebase DB to the RecyclerView
        recyclerView_order_dishes = (RecyclerView) findViewById(R.id.listCart);
        recyclerView_order_dishes.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_order_dishes.setLayoutManager(layoutManager);

        textView_total = (TextView) findViewById(R.id.total);
        fButton_placeOrder = (FButton) findViewById(R.id.buttonPlaceOrder);

        fButton_placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CartActivity.this, "Place Order", Toast.LENGTH_LONG).show();
            }
        });

        loadDishList();

    }

    private void loadDishList() {
        cartList = new Database(this).getCarts();
        cartAdapter = new CartAdapter(cartList, this);
        recyclerView_order_dishes.setAdapter(cartAdapter);

        //Calculate total price
        int total = 0;
        for(Order order:cartList){
            total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }

        Locale locale = new Locale("en", "US");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        textView_total.setText(numberFormat.format(total));
    }
}
