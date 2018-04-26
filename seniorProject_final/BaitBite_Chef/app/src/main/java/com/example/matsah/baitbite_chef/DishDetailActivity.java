package com.example.matsah.baitbite_chef;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
//import com.example.android.baitbite.Database.Database;
import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.Model.Dish;
//import com.example.android.baitbite.Model.Order;
import com.example.matsah.baitbite_chef.Model.Rate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class DishDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CoordinatorLayout dishDetailLayout;

    MaterialEditText editName, editDescription, editPrice, editDiscount;
    FButton buttonUpload, buttonSelect;
    Button buttonUpdateQuantity;
    TextView dish_name, dish_price, dish_description;
    ElegantNumberButton dish_quantity, editQuantity;
    ImageView dish_image, edit_dish_picture;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton choose_pic, fButtonEditDish;
    ElegantNumberButton elegantNumberButton_quantity;
    RatingBar ratingBar;

    String dishID = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dishes, chefs, rating_table;

    Dish currentDish;

    Uri saveUri;

    FirebaseStorage storage;
    StorageReference storageReference;

    DrawerLayout drawer;

    int dishOldQuantity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dish Detail");
        setSupportActionBar(toolbar);
        */

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        dishes = firebaseDatabase.getReference("Dishes");
        chefs = firebaseDatabase.getReference("Chef");
        rating_table = firebaseDatabase.getReference("Rating");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init view
        elegantNumberButton_quantity = (ElegantNumberButton) findViewById(R.id.elegantNumberButton_quantity);

        dishDetailLayout = (CoordinatorLayout) findViewById(R.id.dishDetailLayout);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);


        dish_description = (TextView) findViewById(R.id.dish_description);
        dish_name = (TextView) findViewById(R.id.dish_name);
        dish_price = (TextView) findViewById(R.id.dish_price);
        dish_image = (ImageView) findViewById(R.id.imageView_dish);
        dish_quantity = (ElegantNumberButton) findViewById(R.id.elegantNumberButton_quantity);



        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);

        //Get Dish ID from the Intent
        if(getIntent() != null){
            dishID = getIntent().getStringExtra("DishID");
        }

        if(!dishID.isEmpty()){
            getDetailDish(dishID);
            getRatingDish(dishID);
        }



        buttonUpdateQuantity = (Button) findViewById(R.id.buttonUpdateQuantity);
        buttonUpdateQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDish.setQuantity(dish_quantity.getNumber().toString());
                updateDishQuantity(Integer.parseInt(dish_quantity.getNumber().toString()));
                dishes.child(dishID).setValue(currentDish);

                Toast.makeText(DishDetailActivity.this, "dish quantity is updated", Toast.LENGTH_SHORT).show();

            }
        });

        fButtonEditDish = (FloatingActionButton) findViewById(R.id.fButtonEditDish);
        fButtonEditDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUpdateDishDialog(dishes.child(dishID).getKey());
            }
        });



    }

    private void updateDishQuantity(int newQuan) {
        int oldAvailability = Common.currentChef.getAvailability();
        if(newQuan > dishOldQuantity){
            Common.currentChef.setAvailability(oldAvailability + (newQuan-dishOldQuantity));
        }else if(newQuan < dishOldQuantity){
            Common.currentChef.setAvailability(oldAvailability - (dishOldQuantity-newQuan));
        }
        dishOldQuantity = newQuan;
        chefs.child(Common.currentChef.getPhone_Number()).setValue(Common.currentChef);
    }

    private void getDetailDish(String dishID) {
        dishes.child(dishID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDish = dataSnapshot.getValue(Dish.class);

                if(currentDish != null){

                    //Set Dish Image
                    if(!currentDish.getImage().isEmpty()){
                        Picasso.with(getBaseContext()).load(currentDish.getImage()).into(dish_image);
                    }

                    collapsingToolbarLayout.setTitle(currentDish.getName());

                    dish_price.setText(currentDish.getPrice());
                    dish_name.setText(currentDish.getName());
                    dish_description.setText(currentDish.getDescription());
                    dish_quantity.setNumber(currentDish.getQuantity());

                    //init the dish old quantity
                    dishOldQuantity = Integer.parseInt(currentDish.getQuantity());
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDishDialog(final String key) {
        AlertDialog.Builder alertedDialog = new AlertDialog.Builder(DishDetailActivity.this);
        alertedDialog.setTitle("Edit Dish");
        alertedDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View edit_menu_layout = inflater.inflate(R.layout.edit_dish_layout,null);

        editName = edit_menu_layout.findViewById(R.id.editName);
        editDescription = edit_menu_layout.findViewById(R.id.editDescription);
        editPrice = edit_menu_layout.findViewById(R.id.editPrice);
        editDiscount = edit_menu_layout.findViewById(R.id.editDiscount);
        editQuantity = edit_menu_layout.findViewById(R.id.elegantNumberButton_quantity);
        edit_dish_picture = (ImageView) edit_menu_layout.findViewById(R.id.dish_pic);

        choose_pic = (FloatingActionButton) edit_menu_layout.findViewById(R.id.choosePic);


        //set default value for view
        editName.setText(currentDish.getName());
        editDiscount.setText(currentDish.getDiscount());
        editPrice.setText(currentDish.getPrice());
        editDescription.setText(currentDish.getDescription());
        editQuantity.setNumber(currentDish.getQuantity());
        if(!currentDish.getImage().isEmpty()){
            Picasso.with(getBaseContext()).load(currentDish.getImage()).into(edit_dish_picture);
        }



        //Event for button
        choose_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });


        alertedDialog.setView(edit_menu_layout);

        alertedDialog.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();



                //set the edited the values
                currentDish.setName(editName.getText().toString());
                currentDish.setPrice(editPrice.getText().toString());
                currentDish.setDiscount(editDiscount.getText().toString());
                currentDish.setDescription(editDescription.getText().toString());
                currentDish.setQuantity(editQuantity.getNumber());



                //increase the availability of the chef
                updateDishQuantity(Integer.parseInt(editQuantity.getNumber().toString()));

                dishes.child(key).setValue(currentDish);
                Snackbar.make(dishDetailLayout, "Dish "+currentDish.getName()+" was edited", Snackbar.LENGTH_SHORT).show();

            }
        });

        alertedDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertedDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            saveUri = data.getData();
            buttonSelect.setText("Image Selected !");
            changeImage();
        }
    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);

    }

    private void changeImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(DishDetailActivity.this, "Uploaed !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            currentDish.setImage((uri.toString()));
                            if(!currentDish.getImage().isEmpty()){
                                Picasso.with(getBaseContext()).load(currentDish.getImage()).into(edit_dish_picture);
                            }

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(DishDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress =  (100.0 * (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                    mDialog.setMessage("Upload "+progress+"%");
                }
            });

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {

            Intent ChefProfile = new Intent(DishDetailActivity.this, ChefProfileActivity.class);
            startActivity(ChefProfile);

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_sign_out) {
            //Delete Remembered Chef
            Paper.book().destroy();

            //Signout
            Intent signInIntent = new Intent(DishDetailActivity.this, SignInActivity.class);
            signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signInIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(DishDetailActivity.this, Home.class);
        startActivity(homeIntent);
        finish();


    }
}
