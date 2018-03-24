package com.example.android.baitbite;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.baitbite.Model.Dish;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        dishes = firebaseDatabase.getReference("Dishes");

        //Init view
        elegantNumberButton_quantity = (ElegantNumberButton) findViewById(R.id.elegantNumberButton_quantity);
        button_cart = (FloatingActionButton) findViewById(R.id.button_cart);

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
            getDetailDish(dishID);
        }

    }

    private void getDetailDish(String dishID) {
        dishes.child(dishID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dish dish = dataSnapshot.getValue(Dish.class);

                //Set Dish Image
                Picasso.with(getBaseContext()).load(dish.getImage()).into(dish_image);

                collapsingToolbarLayout.setTitle(dish.getName());

                dish_price.setText(dish.getPrice());
                dish_name.setText(dish.getName());
                dish_description.setText(dish.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
