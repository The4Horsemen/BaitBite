package com.example.matsah.baitbite_chef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.GPS.GPSTracker;
import com.example.matsah.baitbite_chef.Model.Chef;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.concurrent.TimeUnit;

public class SignUP_WithVerify extends AppCompatActivity {
    private GPSTracker gpsTracker ;
    MaterialEditText editPhone, editName, verificationCode;
    protected PermissionManager permissionnManager;


    /*added by Ibra*/
    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    /**/

    FirebaseDatabase firebaseDatabase;
    DatabaseReference table_chef;

    Button buttonSignUp, buttonVerify;
    Chef chef;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__with_verify);


        editPhone = (MaterialEditText) findViewById(R.id.editPhone);
        editName = (MaterialEditText) findViewById(R.id.editName);
        verificationCode = (MaterialEditText) findViewById(R.id.verificationCode);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonVerify = (Button) findViewById(R.id.buttonVerify);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        table_chef = firebaseDatabase.getReference("Chef");

        /*Added by Ibra*/
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);


                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(SignUP_WithVerify.this, "Sign up onVerificationFailed !", Toast.LENGTH_LONG).show();


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                Toast.makeText(SignUP_WithVerify.this, "verification code is sent to your mobile ",Toast.LENGTH_LONG).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };



        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String code = verificationCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(SignUP_WithVerify.this, "please enter the verification code.",Toast.LENGTH_LONG).show();
                    //verification_code.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);

            }});



        /**/



        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editPhone.getText().toString().matches("")){
                    Toast.makeText(SignUP_WithVerify.this, "please enter the phone number",Toast.LENGTH_LONG).show();
                    return;
                }

                phone = "+966"+editPhone.getText().toString().substring(1);

                if(!isValidPhoneNo(phone)){
                    Toast.makeText(SignUP_WithVerify.this, "please enter a valid phone number",Toast.LENGTH_LONG).show();
                    return;
                }

                permissionnManager = new PermissionManager() {
                };
                gpsTracker = new GPSTracker(SignUP_WithVerify.this);
                if (permissionnManager.checkAndRequestPermissions(SignUP_WithVerify.this) && gpsTracker.canGetLocation()) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUP_WithVerify.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();
                    table_chef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {



                            //check if the phone number already exist
                            if (dataSnapshot.child(phone).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUP_WithVerify.this, "The phone number is already registered by a chef", Toast.LENGTH_LONG).show();
                            } else {
                                /*Ibra*/
                                startPhoneNumberVerification(phone);

                                buttonSignUp.setVisibility(View.INVISIBLE);
                                editName.setVisibility(View.INVISIBLE);
                                editPhone.setVisibility(View.INVISIBLE);
                                buttonVerify.setVisibility(View.VISIBLE);
                                verificationCode.setVisibility(View.VISIBLE);
                                /**/
                                mDialog.dismiss();


                                //finish();
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
    /*Ibra*/
    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Toast.makeText(SignUP_WithVerify.this, "the code is verified successfully",Toast.LENGTH_LONG).show();

                            chef = new Chef("", gpsTracker.getLatitude(), gpsTracker.getLongitude(), editName.getText().toString(), phone);
                            chef.setProfile_Image("");
                            table_chef.child(phone).setValue(chef);
                            Toast.makeText(SignUP_WithVerify.this, "Sign up successfully !", Toast.LENGTH_LONG).show();

                            CreateStoreLocation(chef);

                            Intent homeIntent = new Intent(SignUP_WithVerify.this, Home.class);
                            Common.currentChef = chef;
                            startActivity(homeIntent);
                            finish();


                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignUP_WithVerify.this, "the verification code is incorrect",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    /**/


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

    public static boolean isValidPhoneNo(CharSequence iPhoneNo) {
        return !TextUtils.isEmpty(iPhoneNo) &&
                Patterns.PHONE.matcher(iPhoneNo).matches();
    }


}
