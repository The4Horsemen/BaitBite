package com.example.matsah.baitbite_chef;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
//import com.example.android.baitbite.Database.Database;
import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.Model.Dish;
//import com.example.android.baitbite.Model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class DishDetailActivity extends AppCompatActivity {

    CoordinatorLayout dishDetailLayout;

    MaterialEditText editName, editDescription, editPrice, editDiscount;
    FButton buttonUpload, buttonSelect;
    Button buttonUpdateQuantity;
    TextView dish_name, dish_price, dish_description;
    ElegantNumberButton dish_quantity, editQuantity;
    ImageView dish_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton button_cart, fButtonEditDish;
    ElegantNumberButton elegantNumberButton_quantity;

    String dishID = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dishes;

    Dish currentDish;

    Uri saveUri;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        dishes = firebaseDatabase.getReference("Dishes");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init view
        elegantNumberButton_quantity = (ElegantNumberButton) findViewById(R.id.elegantNumberButton_quantity);

        dishDetailLayout = (CoordinatorLayout) findViewById(R.id.dishDetailLayout);





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
        }



        buttonUpdateQuantity = (Button) findViewById(R.id.buttonUpdateQuantity);
        buttonUpdateQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDish.setQuantity(dish_quantity.getNumber().toString());
                dishes.child(dishID).setValue(currentDish);

                Toast.makeText(DishDetailActivity.this, "dish quantity is updated", Toast.LENGTH_SHORT).show();

            }
        });

        fButtonEditDish = (FloatingActionButton) findViewById(R.id.fButtonEditDish);
        fButtonEditDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUpdateDishDialog(dishes.child(dishID).getKey(), currentDish);
            }
        });



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
                dish_quantity.setNumber(currentDish.getQuantity());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDishDialog(final String key, final Dish item) {
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


        //set default value for view
        editName.setText(item.getName());
        editDiscount.setText(item.getDiscount());
        editPrice.setText(item.getPrice());
        editDescription.setText(item.getDescription());
        editQuantity.setNumber(item.getQuantity());


        buttonSelect = edit_menu_layout.findViewById(R.id.buttonSelect);
        buttonUpload = edit_menu_layout.findViewById(R.id.buttonUpload);

        //Event for button
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        alertedDialog.setView(edit_menu_layout);
        alertedDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertedDialog.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();




                item.setName(editName.getText().toString());
                item.setPrice(editPrice.getText().toString());
                item.setDiscount(editDiscount.getText().toString());
                item.setDescription(editDescription.getText().toString());
                item.setQuantity(editQuantity.getNumber());


                dishes.child(key).setValue(item);
                Snackbar.make(dishDetailLayout, "Dish "+item.getName()+" was edited", Snackbar.LENGTH_SHORT).show();

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
        }
    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);

    }

    private void changeImage(final Dish item) {
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
                            item.setImage((uri.toString()));

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
}
