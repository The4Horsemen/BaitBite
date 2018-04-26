package com.example.android.baitbite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Interface.ItemClickListener;
import com.example.android.baitbite.Model.Dish;
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

public class DishListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dishList;

    RecyclerView recyclerView_dish;
    RecyclerView.LayoutManager layoutManager;

    String categoryId ="";

    FirebaseRecyclerAdapter<Dish, DishViewHolder> dishAdapter;

    //Search Function
    FirebaseRecyclerAdapter<Dish, DishViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        dishList = firebaseDatabase.getReference("Dishes");

        //Load the data from Firebase DB to the RecyclerView
        recyclerView_dish = (RecyclerView) findViewById(R.id.recyclerView_dish);
        recyclerView_dish.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_dish.setLayoutManager(layoutManager);

        //Get Intent
        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("categoryId");
        }

        if(!categoryId.isEmpty() && categoryId != null){
            if(Common.isConnectedToInternet(getBaseContext())) {
                loadListDish(categoryId);
            }else {
                Toast.makeText(DishListActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                return;
            }
        }

        //Search
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar_dishList);
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
                        Intent dishDetail = new Intent(DishListActivity.this, DishDetailActivity.class);

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
        dishList.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
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

    private void loadListDish(String categoryId) {
        dishAdapter = new FirebaseRecyclerAdapter<Dish, DishViewHolder>(Dish.class, R.layout.dish_item, DishViewHolder.class, dishList.orderByChild("categoryId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(DishViewHolder viewHolder, Dish model, int position) {
                viewHolder.textViewDishName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageViewDish);

                final Dish local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start Dish Detail Activity
                        Intent dishDetail = new Intent(DishListActivity.this, DishDetailActivity.class);

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
}
