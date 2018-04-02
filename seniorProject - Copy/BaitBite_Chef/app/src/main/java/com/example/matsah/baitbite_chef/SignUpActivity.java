package com.example.matsah.baitbite_chef;

/**
 * Created by MATSAH on 3/29/2018.
 */

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private GPSTracker gpsTracker;
    MaterialEditText editPhone, editName, editPassword;
    protected PermissionManager permissionnManager;
    //Button SignUpActivity in SignUpActivity page
    Button buttonSignUp;
    Button buttonLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editPhone = (MaterialEditText) findViewById(R.id.editPhone);
        editName = (MaterialEditText) findViewById(R.id.editName);
        editPassword = (MaterialEditText) findViewById(R.id.editPassword);
        buttonLocation = (Button) findViewById(R.id.buttonLocation);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        //Init Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_chef = firebaseDatabase.getReference("Chef");

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();
                table_chef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //check if the phone number already exist
                        if(dataSnapshot.child(editPhone.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "The phone number is already registered by a chef", Toast.LENGTH_LONG).show();
                        }else {
                            mDialog.dismiss();
                            Chef chef = new Chef("rook",90,90,editName.getText().toString(), editPhone.getText().toString());
                            table_chef.child(editPhone.getText().toString()).setValue(chef);
                            Toast.makeText(SignUpActivity.this, "Sign up successfully !", Toast.LENGTH_LONG).show();
                            /* this will add store location for each chef created
                            CreateStoreLocation(chef);
                             */
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });





        buttonLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                permissionnManager = new PermissionManager() {
                };
                permissionnManager.checkAndRequestPermissions(SignUpActivity.this);
                gpsTracker = new GPSTracker(SignUpActivity.this);


                if (gpsTracker.canGetLocation())
                {
                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();

                    Toast.makeText(getApplicationContext(), "Location : \nX " + latitude + "\nY " + longitude, Toast.LENGTH_LONG).show();
                }
                else
                {

                    gpsTracker.showSettingsAlert();
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
