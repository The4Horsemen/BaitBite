package com.example.matsah.baitbite_chef;

/**
 * Created by MATSAH on 3/29/2018.
 */

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.example.android.baitbite.GPS.GPSTracker;
import com.example.matsah.baitbite_chef.GPS.GPSTracker;
import com.example.matsah.baitbite_chef.Model.Chef;
import com.example.matsah.baitbite_chef.Model.Chef;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {
    private GPSTracker  gpsTracker ;
    MaterialEditText editPhone, editName, editPassword;
    protected PermissionManager permissionnManager;
    //Button SignUpActivity in SignUpActivity page
    Button buttonSignUp;


    Chef chef;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        editPhone = (MaterialEditText) findViewById(R.id.editPhone);
        editName = (MaterialEditText) findViewById(R.id.editName);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        //Init Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_chef = firebaseDatabase.getReference("Chef");

        // Checking the service of GPS



        // End of checking

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = "+966"+editPhone.getText().toString().substring(1);

                if(phone.matches("")){
                    Toast.makeText(SignUpActivity.this, "please enter the phone number",Toast.LENGTH_LONG).show();
                    return;
                }
                permissionnManager = new PermissionManager() {
                     };
                gpsTracker = new GPSTracker(SignUpActivity.this);
                if (permissionnManager.checkAndRequestPermissions(SignUpActivity.this) && gpsTracker.canGetLocation()) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();
                    table_chef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {



                            //check if the phone number already exist
                            if (dataSnapshot.child(phone).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "The phone number is already registered by a chef", Toast.LENGTH_LONG).show();
                            } else {
                                mDialog.dismiss();
                                chef = new Chef("", gpsTracker.getLatitude(), gpsTracker.getLongitude(), editName.getText().toString(), phone);
                                table_chef.child(phone).setValue(chef);
                                Toast.makeText(SignUpActivity.this, "Sign up successfully !", Toast.LENGTH_LONG).show();

                            CreateStoreLocation(chef);

                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });


    }



    //function that create the store location
    public void CreateStoreLocation(Chef chef){

        GeoFire geoFire;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("StoreLocation");
        geoFire = new GeoFire(ref);


            geoFire.setLocation(chef.getPhone_Number(), new GeoLocation(chef.getLocationX(), chef.getLocationY()), new GeoFire.CompletionListener(){
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }


        });




    }


}
