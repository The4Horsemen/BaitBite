package com.example.android.baitbite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.baitbite.Interface.ItemClickListener;
import com.example.android.baitbite.Model.Dish;
import com.example.android.baitbite.ViewHolder.DishViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DishListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dishList;

    RecyclerView recyclerView_dish;
    RecyclerView.LayoutManager layoutManager;

    String categoryId ="";

    FirebaseRecyclerAdapter<Dish, DishViewHolder> dishAdapter;

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
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        if(!categoryId.isEmpty() && categoryId != null){
            loadListDish(categoryId);
        }

    }

    private void loadListDish(String categoryId) {
        dishAdapter = new FirebaseRecyclerAdapter<Dish, DishViewHolder>(Dish.class, R.layout.dish_item, DishViewHolder.class, dishList.orderByChild("CategoryID").equalTo(categoryId)) {
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
                        dishDetail.putExtra("DishID", dishAdapter.getRef(position).getKey());
                        startActivity(dishDetail);
                    }
                });
            }
        };

        //Set Adapter
        recyclerView_dish.setAdapter(dishAdapter);
    }
}
