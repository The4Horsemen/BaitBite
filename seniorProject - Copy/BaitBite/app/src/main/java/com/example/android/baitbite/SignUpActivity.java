package com.example.android.baitbite;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.baitbite.Model.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {

    MaterialEditText editPhone, editName, editPassword;

    //Button SignUpActivity in SignUpActivity page
    Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editPhone = (MaterialEditText) findViewById(R.id.editPhone);
        editName = (MaterialEditText) findViewById(R.id.editName);
        editPassword = (MaterialEditText) findViewById(R.id.editPassword);

        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        //Init Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_customer = firebaseDatabase.getReference("Customer");

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();

                table_customer.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //check if the phone number already exist
                        if(dataSnapshot.child(editPhone.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "The phone number is already registered by a customer", Toast.LENGTH_LONG).show();
                        }else {
                            mDialog.dismiss();
                            Customer customer = new Customer(editName.getText().toString(), editPassword.getText().toString());
                            table_customer.child(editPhone.getText().toString()).setValue(customer);
                            Toast.makeText(SignUpActivity.this, "Sign up successfully !", Toast.LENGTH_LONG).show();
                            finish();
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
