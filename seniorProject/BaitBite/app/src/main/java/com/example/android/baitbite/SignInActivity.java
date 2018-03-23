package com.example.android.baitbite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Model.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignInActivity extends AppCompatActivity {

    EditText editPhone, editPassword;

    //Button SignInActivity in SignInActivity page
    Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editPhone = (MaterialEditText) findViewById(R.id.editPhone);
        editPassword = (MaterialEditText) findViewById(R.id.editPassword);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        //Init Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_customer = firebaseDatabase.getReference("Customer");

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();

                table_customer.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Check Customer existence in Database
                        if(dataSnapshot.child(editPhone.getText().toString()).exists()){
                            //Get Customer info
                            mDialog.dismiss();
                            Customer customer = dataSnapshot.child(editPhone.getText().toString()).getValue(Customer.class);
                            if(customer.getPassword().equals(editPassword.getText().toString())) {
                                Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
                                Common.currentCustomer = customer;
                                startActivity(homeIntent);
                                finish();
                            }else{
                                Toast.makeText(SignInActivity.this, "Wrong Password !",Toast.LENGTH_LONG).show();
                            }

                        }else{
                            mDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "Customer not exist, Sign Up please!",Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
