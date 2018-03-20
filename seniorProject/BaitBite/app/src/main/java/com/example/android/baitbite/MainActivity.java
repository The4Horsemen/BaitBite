package com.example.android.baitbite;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Button SignIn & SignUp in Main page
    Button buttonSignUp, buttonSignIn;
    TextView textSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        textSlogan = (TextView) findViewById(R.id.textSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        textSlogan.setTypeface(face);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);

            }
        });

    }
}
