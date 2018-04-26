package com.example.android.baitbite;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Database.Database;
import com.example.android.baitbite.Model.Dish;
import com.example.android.baitbite.Model.Order;
import com.example.android.baitbite.Model.Request;
import com.example.android.baitbite.ViewHolder.CartAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class CartActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference requests;
    DatabaseReference dishes;

    RecyclerView recyclerView_order_dishes;
    RecyclerView.LayoutManager layoutManager;

    TextView textView_total, textView_empty;
    FButton fButton_placeOrder;

    List<Order> cartList = new ArrayList<>();

    CartAdapter cartAdapter;

    Dish currentDish;
    int currentDishQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        requests = firebaseDatabase.getReference("OrderNow");
        dishes = firebaseDatabase.getReference("Dishes");

        //Load the data from Firebase DB to the RecyclerView
        recyclerView_order_dishes = (RecyclerView) findViewById(R.id.listCart);
        recyclerView_order_dishes.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_order_dishes.setLayoutManager(layoutManager);

        textView_total = (TextView) findViewById(R.id.total);
        textView_empty = (TextView) findViewById(R.id.textView_empty);

        fButton_placeOrder = (FButton) findViewById(R.id.buttonPlaceOrder);

        fButton_placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cartList.size() > 0) {
                    showAlertDialog();
                }else {
                    Toast.makeText(CartActivity.this, "Your Cart is Empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        loadDishList();

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter your note if you have any:");

        //Add edit text to alert dialog
        final EditText editTextAddress = new EditText(CartActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        editTextAddress.setLayoutParams(layoutParams);
        alertDialog.setView(editTextAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Create new Request
                Request request = new Request(
                        Common.currentCustomer.getPhone(),
                        Common.chefId,
                        Common.currentCustomer.getName(),
                        editTextAddress.getText().toString(),
                        textView_total.getText().toString(),
                        cartList
                );

                //Reduce dishes quantity
                for(Order order:cartList){
                    reduceDishQuantity(order.getDishID(), Integer.parseInt(order.getQuantity()));
                }

                //Submit to Firebase using System.CurrentMilli to Key
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                //Delete cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(CartActivity.this, "Thank you, Order placed.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void loadDishList() {
        cartList = new Database(this).getCarts();
        cartAdapter = new CartAdapter(cartList, this);
        cartAdapter.notifyDataSetChanged();
        recyclerView_order_dishes.setAdapter(cartAdapter);

        //Calculate total price
        double total = 0.0;
        for(Order order:cartList){
            total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }

        textView_total.setText(""+total);

        if(cartList.size() > 0) {
            textView_empty.setText("");
        }else{
            textView_empty.setText("Your cart is empty");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)){
            deleteItemFromCart(item.getOrder());
        }
        return true;
    }

    private void deleteItemFromCart(int position) {
        //Remove item from List<Order> by position
        cartList.remove(position);

        //Delete old data from SQLite
        new Database(this).cleanCart();

        //Update new data in List<Order> to SQLite
        for(Order item:cartList){
            new Database(this).addToCart(item);
        }

        //Refresh the Cart after deleting the item
        loadDishList();
    }

    private void reduceDishQuantity(final String dishID, final int orderQuantity) {

        dishes.child(dishID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDish = dataSnapshot.getValue(Dish.class);
                currentDishQuantity = Integer.parseInt(currentDish.getQuantity());

                currentDishQuantity = currentDishQuantity - orderQuantity;
                Log.d("currentDishQuantity", currentDishQuantity+" "+orderQuantity+" currentDishQuantity after "+currentDishQuantity);
                currentDish.setQuantity(currentDishQuantity+"");
                dishes.child(dishID).setValue(currentDish);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error!!!","the read failed "+databaseError.getCode());
            }
        });
    }
}
