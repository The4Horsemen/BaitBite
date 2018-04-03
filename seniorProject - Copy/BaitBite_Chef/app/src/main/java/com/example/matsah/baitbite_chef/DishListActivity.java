package com.example.matsah.baitbite_chef;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.Interface.ItemClickListener;
import com.example.matsah.baitbite_chef.Model.Category;
import com.example.matsah.baitbite_chef.Model.Dish;
import com.example.matsah.baitbite_chef.ViewHolder.DishViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class DishListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;

    FloatingActionButton fButtonAddDish;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference dishList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId ="";

    FirebaseRecyclerAdapter<Dish, DishViewHolder> adapter;

    //Add new Dish

    MaterialEditText editName, editDescription, editPrice, editDiscount;
    FButton buttonSelect, buttonUpload;

    Dish newDish;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        dishList = database.getReference("Dishes");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //init
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_dish);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        fButtonAddDish = (FloatingActionButton) findViewById(R.id.fButtonAddDish);
        fButtonAddDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDishDialog();
            }
        });

        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("categoryId");
        }
       if(!categoryId.isEmpty() && categoryId != null){
            loadListDish(categoryId);
        }

        //Toast.makeText(this,"categry ID is "+categoryId, Toast.LENGTH_SHORT).show();

    }

    private void loadListDish(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Dish, DishViewHolder>(Dish.class,
                R.layout.dish_item,
                DishViewHolder.class,
                dishList.orderByChild("categoryID").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(DishViewHolder viewHolder, Dish model, int position) {
                viewHolder.DishName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.DishImage);

                viewHolder.setItemClicListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //later
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showAddDishDialog() {
        AlertDialog.Builder alertedDialog = new AlertDialog.Builder(DishListActivity.this);
        alertedDialog.setTitle("Add new Dish");
        alertedDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_dish_layout,null);

        editName = add_menu_layout.findViewById(R.id.editName);
        editDescription = add_menu_layout.findViewById(R.id.editDescription);
        editPrice = add_menu_layout.findViewById(R.id.editPrice);
        editDiscount = add_menu_layout.findViewById(R.id.editDiscount);

        buttonSelect = add_menu_layout.findViewById(R.id.buttonSelect);
        buttonUpload = add_menu_layout.findViewById(R.id.buttonUpload);

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
                uploadImage();
            }
        });

        alertedDialog.setView(add_menu_layout);
        alertedDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertedDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


                if(newDish != null){
                    dishList.push().setValue(newDish);
                    Snackbar.make(rootLayout, "New Dish "+newDish.getName()+" was added", Snackbar.LENGTH_SHORT).show();
                }
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

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
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
                    Toast.makeText(DishListActivity.this, "Uploaed !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newDish = new Dish();
                            newDish.setName(editName.getText().toString());
                            newDish.setDescription(editDescription.getText().toString());
                            newDish.setPrice(editPrice.getText().toString());
                            newDish.setDiscount(editDiscount.getText().toString());
                            newDish.setCategoryId(categoryId);
                            newDish.setImage(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(DishListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            saveUri = data.getData();
            buttonSelect.setText("Image Selected !");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDishDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if(item.getTitle().equals(Common.DELETE)){

        }
        return super.onContextItemSelected(item);

    }

    private void showUpdateDishDialog(final String key, final Dish item) {
        AlertDialog.Builder alertedDialog = new AlertDialog.Builder(DishListActivity.this);
        alertedDialog.setTitle("Edit Dish");
        alertedDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_dish_layout,null);

        editName = add_menu_layout.findViewById(R.id.editName);
        editDescription = add_menu_layout.findViewById(R.id.editDescription);
        editPrice = add_menu_layout.findViewById(R.id.editPrice);
        editDiscount = add_menu_layout.findViewById(R.id.editDiscount);

        //set default value for view
        editName.setText(item.getName());
        editDiscount.setText(item.getDiscount());
        editPrice.setText(item.getPrice());
        editDescription.setText(item.getDescription());

        buttonSelect = add_menu_layout.findViewById(R.id.buttonSelect);
        buttonUpload = add_menu_layout.findViewById(R.id.buttonUpload);

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

        alertedDialog.setView(add_menu_layout);
        alertedDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertedDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();




                    item.setName(editName.getText().toString());
                    item.setPrice(editPrice.getText().toString());
                    item.setDiscount(editDiscount.getText().toString());
                    item.setDescription(editDescription.getText().toString());


                    dishList.child(key).setValue(newDish);
                    Snackbar.make(rootLayout, "Dish "+newDish.getName()+" was edited", Snackbar.LENGTH_SHORT).show();

            }
        });

        alertedDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertedDialog.show();
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
                    Toast.makeText(DishListActivity.this, "Uploaed !!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DishListActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
