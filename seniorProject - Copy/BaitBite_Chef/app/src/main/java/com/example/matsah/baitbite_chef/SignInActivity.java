package com.example.matsah.baitbite_chef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.android.baitbite.Common.Common;
//import com.example.android.baitbite.Model.Customer;
import com.example.matsah.baitbite_chef.Common.Common;
import com.example.matsah.baitbite_chef.GPS.GPSTracker;
import com.example.matsah.baitbite_chef.Model.Chef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.rengwuxian.materialedittext.MaterialEditText;

/*added by Ibra*/
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
/**/

public class SignInActivity extends AppCompatActivity {
    //added by aseel
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

    Chef chef;
    /**/




    EditText editPhone,  verification_code;
    TextView textSignup ;

    String phone;

    //Remember me
    com.rey.material.widget.CheckBox checkBoxRememberMe;

    //Button SignInActivity in SignInActivity page
    Button buttonSignIn, buttonVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        editPhone = (MaterialEditText) findViewById(R.id.editPhone);
        verification_code = (MaterialEditText) findViewById(R.id.verification_code);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonVerify = (Button) findViewById(R.id.verify);

        textSignup =  (TextView) findViewById(R.id.textSignup);

        //Remeber me
        checkBoxRememberMe = (com.rey.material.widget.CheckBox) findViewById(R.id.checkBox_rememberMe);

        //Init Paper
        Paper.init(this);

        //Init Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_chef = firebaseDatabase.getReference("Chef");

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

                Toast.makeText(SignInActivity.this, "verification code is sent to your mobile ",Toast.LENGTH_LONG).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };

        /**/

        textSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // permissionnManager = new PermissionManager() {
               // };
               // GPSTracker gpsTracker = new GPSTracker(SignInActivity.this);
               // if(permissionnManager.checkAndRequestPermissions(SignInActivity.this) && gpsTracker.canGetLocation()) {
                    Intent signUp = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(signUp);
               // }

            }
        });



        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String code = verification_code.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(SignInActivity.this, "please enter the verification code.",Toast.LENGTH_LONG).show();
                    //verification_code.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
            }});

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(SignInActivity.this, editPhone.getText().toString().substring(1),Toast.LENGTH_LONG).show();
                phone = "+966"+editPhone.getText().toString().substring(1);

                if(checkBoxRememberMe.isChecked()) {
                    //Save Chef
                    Paper.book().write(Common.CHEF_KEY, phone);
                }

                if(phone.matches("")){
                    Toast.makeText(SignInActivity.this, "please enter the phone number",Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();

                table_chef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Check chef existence in Database
                        if(dataSnapshot.child(phone).exists()){

                            startPhoneNumberVerification(phone);

                            buttonSignIn.setVisibility(View.INVISIBLE);
                            buttonVerify.setVisibility(View.VISIBLE);
                            editPhone.setVisibility(View.INVISIBLE);
                            verification_code.setVisibility(View.VISIBLE);
                            textSignup.setVisibility(View.INVISIBLE);
                            checkBoxRememberMe.setVisibility(View.INVISIBLE);

                            //Get chef info
                            mDialog.dismiss();
                            chef = dataSnapshot.child(phone).getValue(Chef.class);
                            chef.setPhone_Number(phone);


                        }else{
                            mDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "chef not exist, Sign Up please!",Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        //Check remember me
        String customer = Paper.book().read(Common.CHEF_KEY);
        if(customer != null){
            if(!customer.isEmpty()){
                signIn(customer);
            }
        }

    }

    private void signIn(final String phone) {
        //Init Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_customer = firebaseDatabase.getReference("Chef");

        if (Common.isConnectedToInternet(getBaseContext())) {

            if (phone.matches("")) {
                Toast.makeText(SignInActivity.this, "please enter the phone number", Toast.LENGTH_LONG).show();
                return;
            }

            final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
            mDialog.setMessage("Please wait...");
            mDialog.show();

            table_customer.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //Check Chef existence in Database
                    if (dataSnapshot.child(phone).exists()) {

                        //Get Chef info
                        mDialog.dismiss();
                        chef = dataSnapshot.child(phone).getValue(Chef.class);
                        //Set Phone number of the chef
                        chef.setPhone_Number(phone);

                        Intent homeIntent = new Intent(SignInActivity.this, Home.class);
                        Common.currentChef = chef;
                        startActivity(homeIntent);
                        finish();


                    } else {
                        mDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Chef not exist, Sign Up please!", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(SignInActivity.this, "Please check your intenet connection !!!", Toast.LENGTH_LONG).show();
            return;
        }
    }

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


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Toast.makeText(SignInActivity.this, "the code is verified successfully",Toast.LENGTH_LONG).show();


                            Intent homeIntent = new Intent(SignInActivity.this, Home.class);
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
                                Toast.makeText(SignInActivity.this, "the verification code is incorrect",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }






}
