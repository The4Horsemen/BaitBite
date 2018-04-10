package com.example.android.baitbite;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Database.Database;
import com.example.android.baitbite.Interface.ItemClickListener;
import com.example.android.baitbite.Model.Dish;
import com.example.android.baitbite.Model.Request;
import com.example.android.baitbite.ViewHolder.DishViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChefDishListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dishList;

    RecyclerView recyclerView_dish;
    RecyclerView.LayoutManager layoutManager;

    String chefID ="";

    boolean isDelete = false;

    FirebaseRecyclerAdapter<Dish, DishViewHolder> dishAdapter;

    //Search Function
    FirebaseRecyclerAdapter<Dish, DishViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_dish_list);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        dishList = firebaseDatabase.getReference("Dishes");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_chefDishList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(ChefDishListActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });

        //Load the data from Firebase DB to the RecyclerView
        recyclerView_dish = (RecyclerView) findViewById(R.id.recyclerView_chefDishList);
        recyclerView_dish.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_dish.setLayoutManager(layoutManager);

        //Get Intent
        if(getIntent() != null){
            chefID = getIntent().getStringExtra("chefID");
        }

        if(!chefID.isEmpty() && chefID != null){
            if(Common.isConnectedToInternet(getBaseContext())) {
                Common.chefId = chefID;
                loadListDish(chefID);
            }else {
                Toast.makeText(ChefDishListActivity.this, "Please check your intenet connection !!!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        //Search
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar_chefDishList);
        materialSearchBar.setHint("Enter your Dish");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggestions = new ArrayList<String>();
                for(String search:suggestList){
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        suggestions.add(search);
                    }
                }

                materialSearchBar.setLastSuggestions(suggestions);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When Search Bar is closed, Restore original adapter
                if(!enabled){
                    recyclerView_dish.setAdapter(dishAdapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search is finished, Show the result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Dish, DishViewHolder>(
                Dish.class,
                R.layout.dish_item,
                DishViewHolder.class,
                dishList.orderByChild("name").equalTo(text.toString())

        ) {
            @Override
            protected void populateViewHolder(DishViewHolder viewHolder, Dish model, int position) {
                viewHolder.textViewDishName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageViewDish);

                final Dish local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start Dish Detail Activity
                        Intent dishDetail = new Intent(ChefDishListActivity.this, DishDetailActivity.class);

                        //Send DishID to Dish Detail Activity
                        dishDetail.putExtra("dishId", searchAdapter.getRef(position).getKey());
                        startActivity(dishDetail);
                    }
                });
            }
        };
        //Set adapter for RecyclerView as Search result
        recyclerView_dish.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        dishList.orderByChild("chefID").equalTo(chefID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnapshot:dataSnapshot.getChildren()){
                    Dish dishItem = postDataSnapshot.getValue(Dish.class);
                    // Adding name of the Dish to Suggest List
                    suggestList.add(dishItem.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListDish(String chefID) {
        dishAdapter = new FirebaseRecyclerAdapter<Dish, DishViewHolder>(Dish.class, R.layout.dish_item, DishViewHolder.class, dishList.orderByChild("chefID").equalTo(chefID)) {
            @Override
            protected void populateViewHolder(DishViewHolder viewHolder, Dish model, int position) {
                viewHolder.textViewDishName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageViewDish);

                final Dish local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start Dish Detail Activity
                        Intent dishDetail = new Intent(ChefDishListActivity.this, DishDetailActivity.class);

                        //Send DishID to Dish Detail Activity
                        dishDetail.putExtra("dishId", dishAdapter.getRef(position).getKey());
                        startActivity(dishDetail);
                    }
                });
            }
        };

        //Set Adapter
        recyclerView_dish.setAdapter(dishAdapter);
    }

    @Override
    public void onBackPressed() {

        showAlertDialog();

        if (isDelete) {
            super.onBackPressed();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChefDishListActivity.this);
        alertDialog.setTitle("Are sure you want to go back?");
        alertDialog.setMessage("Your cart will be deleted !!!");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Delete cart
                new Database(getBaseContext()).cleanCart();

                Toast.makeText(ChefDishListActivity.this, "Your cart items have been deleted !", Toast.LENGTH_SHORT).show();
                isDelete = true;
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }
}
