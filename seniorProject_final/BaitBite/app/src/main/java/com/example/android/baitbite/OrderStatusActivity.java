package com.example.android.baitbite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Model.Request;
import com.example.android.baitbite.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.android.baitbite.Common.Common.convertCodeToStatus;

public class OrderStatusActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    RecyclerView recyclerView_orderList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        requests = firebaseDatabase.getReference("OrderNow");

        recyclerView_orderList = (RecyclerView) findViewById(R.id.ordersList);
        recyclerView_orderList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_orderList.setLayoutManager(layoutManager);



        if(getIntent() != null) {
            loadOrder(Common.currentCustomer.getPhone());
        }else {
            loadOrder(getIntent().getStringExtra("customerId"));
        }

    }

    private void loadOrder(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.textViewOrderID.setText(adapter.getRef(position).getKey());
                viewHolder.textViewOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.textViewOrderAddress.setText(model.getNote());
                viewHolder.textViewOrderPhone.setText(model.getPhone());

            }
        };
        recyclerView_orderList.setAdapter(adapter);
    }

}
