package com.example.matsah.baitbite_chef;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.Interface.ItemClickListener;
import com.example.matsah.baitbite_chef.Model.Category;
import com.example.matsah.baitbite_chef.Model.Dish;
import com.example.matsah.baitbite_chef.ViewHolder.DishViewHolder;
import com.example.matsah.baitbite_chef.ViewHolder.MenuViewHolder;
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

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference dishList;
    FirebaseStorage storage;
    StorageReference storageRefrence;
    FirebaseRecyclerAdapter<Dish, DishViewHolder> adapter;

    //View
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    // Add New Mwenu Layout
    MaterialEditText editName, editDescription, editPrice, editDiscount;
    ElegantNumberButton editQuantity;
    FButton buttonUpload, buttonSelect;

    Dish newDish;

    Uri saveUri;


    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Init Firebase

        database = FirebaseDatabase.getInstance();
        dishList = database.getReference("Dishes");
        storage = FirebaseStorage.getInstance();
        storageRefrence = storage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDishDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set name for chef

        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.textView_chefName);
        txtFullName.setText(Common.currentChef.getName());

        //Init View
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadListDish();
        
    }

    private void showAddDishDialog() {
        AlertDialog.Builder alertedDialog = new AlertDialog.Builder(Home.this);
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
        newDish = new Dish();

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
                newDish.setName(editName.getText().toString());
                newDish.setDescription(editDescription.getText().toString());
                newDish.setPrice(editPrice.getText().toString());
                newDish.setDiscount(editDiscount.getText().toString());
                newDish.setCategoryId("");
                newDish.setChefID(Common.currentChef.getPhone_Number());
                newDish.setQuantity("0");


                if(newDish != null){
                    dishList.push().setValue(newDish);
                    Snackbar.make(drawer, "New Dish "+newDish.getName()+" was added", Snackbar.LENGTH_SHORT).show();
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


    private void uploadImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRefrence.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(Home.this, "Uploaed !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newDish.setImage(uri.toString());


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress =  (100.0 * (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                    mDialog.setMessage("Uploading "+progress+"%");
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

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);

    }


    private void loadListDish() {
        adapter = new FirebaseRecyclerAdapter<Dish, DishViewHolder>(Dish.class,
                R.layout.dish_item,
                DishViewHolder.class,
                dishList.orderByChild("chefID").equalTo(Common.currentChef.getPhone_Number())) {
            @Override
            protected void populateViewHolder(DishViewHolder viewHolder, Dish model, int position) {
                viewHolder.DishName.setText(model.getName());
                viewHolder.DishPrice.setText(model.getPrice()+" SAR");
                viewHolder.DishQuantity.setText("QTY: "+model.getQuantity());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.DishImage);

                viewHolder.setItemClicListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start Dish Detail Activity
                        Intent dishDetail = new Intent(Home.this, DishDetailActivity.class);

                        //Send DishID to Dish Detail Activity
                        dishDetail.putExtra("DishID", adapter.getRef(position).getKey());
                        startActivity(dishDetail);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_sign_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Update / Delete

    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDishDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if(item.getTitle().equals(Common.DELETE)){
            deleteDish(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);

    }


    private void deleteDish(String key) {
        dishList.child(key).removeValue();
    }

    private void showUpdateDishDialog(final String key, final Dish item) {
        AlertDialog.Builder alertedDialog = new AlertDialog.Builder(Home.this);
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


                dishList.child(key).setValue(item);
                Snackbar.make(drawer, "Dish "+item.getName()+" was edited", Snackbar.LENGTH_SHORT).show();

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

    private void changeImage(final Dish item) {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRefrence.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(Home.this, "Uploaed !!!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
