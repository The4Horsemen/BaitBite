package com.example.android.baitbite;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Database.Database;
import com.example.android.baitbite.Model.Dish;
import com.example.android.baitbite.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DishDetailActivity extends AppCompatActivity {

    TextView dish_name, dish_price, dish_description;
    ImageView dish_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton button_cart;
    ElegantNumberButton elegantNumberButton_quantity;

    String dishID = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dishes;

    Dish currentDish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        dishes = firebaseDatabase.getReference("Dishes");

        //Init view
        elegantNumberButton_quantity = (ElegantNumberButton) findViewById(R.id.elegantNumberButton_quantity);

        //FloatingActionButton Cart functionality
        button_cart = (FloatingActionButton) findViewById(R.id.button_cart);
        button_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        dishID,
                        currentDish.getName(),
                        elegantNumberButton_quantity.getNumber(),
                        currentDish.getPrice(),
                        currentDish.getDiscount()
                ));

                Toast.makeText(DishDetailActivity.this, "Added to Cart", Toast.LENGTH_LONG).show();
            }
        });

        dish_description = (TextView) findViewById(R.id.dish_description);
        dish_name = (TextView) findViewById(R.id.dish_name);
        dish_price = (TextView) findViewById(R.id.dish_price);
        dish_image = (ImageView) findViewById(R.id.imageView_dish);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);

        //Get Dish ID from the Intent
        if(getIntent() != null){
            dishID = getIntent().getStringExtra("DishID");
        }

        if(!dishID.isEmpty()){
            if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailDish(dishID);
            }else {
                Toast.makeText(DishDetailActivity.this, "Please check your intenet connection !!!", Toast.LENGTH_LONG).show();
                return;
            }
        }

    }

    private void getDetailDish(String dishID) {
        dishes.child(dishID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDish = dataSnapshot.getValue(Dish.class);

                //Set Dish Image
                Picasso.with(getBaseContext()).load(currentDish.getImage()).into(dish_image);

                collapsingToolbarLayout.setTitle(currentDish.getName());

                dish_price.setText(currentDish.getPrice());
                dish_name.setText(currentDish.getName());
                dish_description.setText(currentDish.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
