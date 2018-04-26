package com.example.android.baitbite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.baitbite.Common.Common;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karan.churi.PermissionManager.PermissionManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class CustomerProfileActivity extends AppCompatActivity {

    MaterialEditText editName, editEmail;
    ImageView profile_picture;
    FloatingActionButton choosePic;
    FButton  updateProfile;


    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference customers;

    FirebaseStorage storage;
    StorageReference storageReference;


    Uri saveUri;
    //GPS




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        customers = firebaseDatabase.getReference("Customer");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //Init view
        profile_picture = (ImageView) findViewById(R.id.profile_picture);

        editName = (MaterialEditText) findViewById(R.id.updateName);
        editEmail = (MaterialEditText) findViewById(R.id.updateEmail);

        choosePic = (FloatingActionButton) findViewById(R.id.choosePic);


        updateProfile = (FButton) findViewById(R.id.buttonUpdateProfile);

        //set default value
        editName.setText(Common.currentCustomer.getName());
        editEmail.setText(Common.currentCustomer.getEmail());
        if(!Common.currentCustomer.getProfile_Image().isEmpty()){
            Picasso.with(getBaseContext()).load(Common.currentCustomer.getProfile_Image()).into(profile_picture);
        }

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();

            }
        });



        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currentCustomer.setName(editName.getText().toString());
                Common.currentCustomer.setEmail(editEmail.getText().toString());

                customers.child(Common.currentCustomer.getPhone()).setValue(Common.currentCustomer);
                Toast.makeText(CustomerProfileActivity.this, "The profile is Updated", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(CustomerProfileActivity.this, MapsActivity.class);
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
                    Toast.makeText(CustomerProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Common.currentCustomer.setProfile_Image((uri.toString()));
                            //profile_picture.setImageResource();
                            Picasso.with(getBaseContext()).load(Common.currentCustomer.getProfile_Image()).into(profile_picture);

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(CustomerProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
