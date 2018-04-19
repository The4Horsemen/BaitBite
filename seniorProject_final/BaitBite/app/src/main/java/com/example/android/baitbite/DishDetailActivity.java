package com.example.android.baitbite;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Database.Database;
import com.example.android.baitbite.Model.Dish;
import com.example.android.baitbite.Model.Order;
import com.example.android.baitbite.Model.Rate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class DishDetailActivity extends AppCompatActivity implements RatingDialogListener{

    TextView dish_name, dish_price, dish_description;
    ImageView dish_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton button_cart, button_rating;
    ElegantNumberButton elegantNumberButton_quantity;
    RatingBar ratingBar;

    String dishID = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dishes;
    DatabaseReference rating_table;

    Dish currentDish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        dishes = firebaseDatabase.getReference("Dishes");
        rating_table = firebaseDatabase.getReference("Rating");

        //Init view
        elegantNumberButton_quantity = (ElegantNumberButton) findViewById(R.id.elegantNumberButton_quantity);

        //Rating functionality
        button_rating = (FloatingActionButton) findViewById(R.id.button_rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        button_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

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
            dishID = getIntent().getStringExtra("dishId");
        }

        if(!dishID.isEmpty()){
            if(Common.isConnectedToInternet(getBaseContext())) {
                getDetailDish(dishID);
                getRatingDish(dishID);
                
            }else {
                Toast.makeText(DishDetailActivity.this, "Please check your intenet connection !!!", Toast.LENGTH_LONG).show();
                return;
            }
        }

    }

    private void getRatingDish(String dishID) {

        Query dishRating = rating_table.orderByChild("dishID").equalTo(dishID);

        dishRating.addValueEventListener(new ValueEventListener() {
            int counter = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnapshot:dataSnapshot.getChildren()){
                    Rate item = postDataSnapshot.getValue(Rate.class);
                    sum += Integer.parseInt(item.getRateValue());
                    counter++;
                }

                if(counter != 0){
                    float averageRating = sum/counter;
                    ratingBar.setRating(averageRating);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Bad", "Normal", "Good", "Very Good"))
                .setDefaultRating(1)
                .setTitle("Rate This Dish")
                .setDescription("Please select some star & give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Write your comment(s) here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(DishDetailActivity.this)
                .show();
    }

    private void getDetailDish(String dishID) {
        dishes.child(dishID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDish = dataSnapshot.getValue(Dish.class);

                //Set Dish Image
                if(!currentDish.getImage().isEmpty()){
                    Picasso.with(getBaseContext()).load(currentDish.getImage()).into(dish_image);
                }

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

    @Override
    public void onPositiveButtonClicked(int value, String comments) {

        //Get Rating & Upload it to Firebase DB
        final Rate rating = new Rate(Common.currentCustomer.getPhone(),
                dishID,
                String.valueOf(value),
                comments);

        rating_table.child(Common.currentCustomer.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentCustomer.getPhone()).exists()){

                    //Remove previous rating to the dish
                    rating_table.child(Common.currentCustomer.getPhone()).removeValue();

                    //Update new rating to the dish
                    rating_table.child(Common.currentCustomer.getPhone()).setValue(rating);
                }else {
                    //Update new rating to the dish
                    rating_table.child(Common.currentCustomer.getPhone()).setValue(rating);
                }

                Toast.makeText(DishDetailActivity.this, "Thanks for your Rating & Feedback!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
