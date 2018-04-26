package com.example.matsah.baitbite_chef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.GPS.GPSTracker;
import com.example.matsah.baitbite_chef.Model.Dish;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karan.churi.PermissionManager.PermissionManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class ChefProfileActivity extends AppCompatActivity {

    MaterialEditText editName, editEmail, editStoreSummary, editCredit;
    ImageView profile_picture;
    FloatingActionButton choosePic;
    FButton setLocation, updateProfile;


    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference chefs;

    FirebaseStorage storage;
    StorageReference storageReference;


    Uri saveUri;
    //GPS
    private GPSTracker gpsTracker ;
    protected PermissionManager permissionnManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_profile);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        chefs = firebaseDatabase.getReference("Chef");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //Init view
        profile_picture = (ImageView) findViewById(R.id.profile_picture);

        editName = (MaterialEditText) findViewById(R.id.updateName);
        editEmail = (MaterialEditText) findViewById(R.id.updateEmail);
        editStoreSummary = (MaterialEditText) findViewById(R.id.updateStoreSummary);
        editCredit = (MaterialEditText) findViewById(R.id.updateCredit);

        choosePic = (FloatingActionButton) findViewById(R.id.choosePic);

        setLocation = (FButton) findViewById(R.id.buttonSetLocation);
        updateProfile = (FButton) findViewById(R.id.buttonUpdateProfile);

        //set default value
        editName.setText(Common.currentChef.getName());
        editEmail.setText(Common.currentChef.getEmail());
        editStoreSummary.setText(Common.currentChef.getStore_Summary());
        if(!Common.currentChef.getProfile_Image().isEmpty()){
            Picasso.with(getBaseContext()).load(Common.currentChef.getProfile_Image()).into(profile_picture);
        }
        //if(Common.currentChef)
        editCredit.setText(Common.currentChef.getCredit());

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();

            }
        });

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionnManager = new PermissionManager() {
                };
                gpsTracker = new GPSTracker(ChefProfileActivity.this);
                if (permissionnManager.checkAndRequestPermissions(ChefProfileActivity.this) && gpsTracker.canGetLocation()) {

                    GeoFire geoFire;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("StoreLocation");
                    geoFire = new GeoFire(ref);


                    geoFire.setLocation(Common.currentChef.getPhone_Number(), new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                System.err.println("There was an error saving the location to GeoFire: " + error);
                                Toast.makeText(ChefProfileActivity.this, "Location Update Failed", Toast.LENGTH_SHORT).show();
                            } else {
                                Common.currentChef.setLocationX(gpsTracker.getLatitude());
                                Common.currentChef.setLocationY(gpsTracker.getLongitude());
                                DatabaseReference chef = firebaseDatabase.getReference("Chef");
                                chef.child(Common.currentChef.getPhone_Number()).setValue(Common.currentChef);
                                System.out.println("Location saved on server successfully!");
                                Toast.makeText(ChefProfileActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                            }
                        }


                    });



                }

            }
        });


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currentChef.setName(editName.getText().toString());
                Common.currentChef.setEmail(editEmail.getText().toString());
                Common.currentChef.setStore_Summary(editStoreSummary.getText().toString());
                Common.currentChef.setCredit(editCredit.getText().toString());

                chefs.child(Common.currentChef.getPhone_Number()).setValue(Common.currentChef);
                Toast.makeText(ChefProfileActivity.this, "The profile is Updated", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(ChefProfileActivity.this, Home.class);
                startActivity(homeIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();
            changeImage();
        }
    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);

    }

    private void changeImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(ChefProfileActivity.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Common.currentChef.setProfile_Image((uri.toString()));
                            //profile_picture.setImageResource();
                            Picasso.with(getBaseContext()).load(Common.currentChef.getProfile_Image()).into(profile_picture);

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(ChefProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(ChefProfileActivity.this, Home.class);
        startActivity(homeIntent);
        finish();


    }
}
