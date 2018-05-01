package com.example.android.baitbite;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Interface.ItemClickListener;
import com.example.android.baitbite.Model.Request;
import com.example.android.baitbite.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import static com.example.android.baitbite.Common.Common.convertCodeToStatus;

public class OrderStatusActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    RecyclerView recyclerView_orderList;
    RecyclerView.LayoutManager layoutManager;

    TextView order_number, order_customer_name, order_status, order_total_price;
    ListView dishList;

    MaterialSpinner spinner;

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
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.textViewOrderID.setText(adapter.getRef(position).getKey());
                viewHolder.textViewOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.textViewOrderAddress.setText(model.getNote());
                viewHolder.textViewOrderPhone.setText(model.getPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        showOrderDetailDialog(adapter.getRef(position).getKey(), model);
                    }
                });

            }
        };
        recyclerView_orderList.setAdapter(adapter);
    }

    private void showOrderDetailDialog(final String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusActivity.this);
        alertDialog.setTitle("Order details");
        //alertDialog.setMessage("Please Choose Status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View orderView = inflater.inflate(R.layout.order_details_layout, null);

        order_number = orderView.findViewById(R.id.order_Number);
        order_customer_name = orderView.findViewById(R.id.Customer_Name);
        order_status = orderView.findViewById(R.id.order_Status);
        order_total_price= orderView.findViewById(R.id.Order_Total_Price);
        dishList = orderView.findViewById(R.id.Order_Dish_List);


        //set values
        order_number.setText(key);
        order_customer_name.setText(item.getName());
        order_status.setText(Common.convertCodeToStatus(item.getStatus()));
        order_total_price.setText(item.getTotal());

        String[] dishes= new String[item.getDishes().size()];

        for(int i =0; i<dishes.length; i++){
            dishes[i] =item.getDishes().get(i).getQuantity()+" X "+ item.getDishes().get(i).getDishName();
        }



        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.list_view, dishes);

        dishList.setAdapter(adapter);



        alertDialog.setView(orderView);

        final String localKey = key;
        alertDialog.setPositiveButton("update dish status", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showUpdateDialog(key, item);
            }

        });

        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusActivity.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose Status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "Preparing", "Ready to pickup");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);
            }

        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

}
