package com.example.android.baitbite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import info.hoang8f.widget.FButton;

public class CartActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference request;

    RecyclerView recyclerView_order_dishes;
    RecyclerView.LayoutManager layoutManager;

    TextView textView_total;
    FButton fButton_placeOrder;

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

        loadDishList();

    }

    private void loadDishList() {
    }
}
