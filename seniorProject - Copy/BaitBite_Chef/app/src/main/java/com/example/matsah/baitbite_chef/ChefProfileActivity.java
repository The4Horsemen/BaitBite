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
import com.example.matsah.baitbite_chef.Model.Dish;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class ChefProfileActivity extends AppCompatActivity {

    MaterialEditText editName, editEmail, editStoreSummary;
    ImageView profile_picture;
    FloatingActionButton choosePic;
    FButton setLocation, updateProfile;


    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference chefs;

    FirebaseStorage storage;
    StorageReference storageReference;


    Uri saveUri;


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

        choosePic = (FloatingActionButton) findViewById(R.id.choosePic);

        setLocation = (FButton) findViewById(R.id.buttonSetLocation);
        updateProfile = (FButton) findViewById(R.id.buttonUpdateProfile);

        //set default value
        editName.setText(Common.currentChef.getName());
        editEmail.setText(Common.currentChef.getEmail());
        editStoreSummary.setText(Common.currentChef.getStore_Summary());

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImage();
                changeImage();
            }
        });

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aseel
            }
        });


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            saveUri = data.getData();
            //buttonSelect.setText("Image Selected !");
            Toast.makeText(ChefProfileActivity.this,"Image Selected !" , Toast.LENGTH_SHORT).show();
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
}
