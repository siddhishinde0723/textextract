package com.example.textread;



import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class  MainActivity extends AppCompatActivity{

    EditText email, password,name ;
    Button reg,login;
    String userID;
    // Creating string to hold email and password .
    String EmailHolder, PasswordHolder,NameHolder ;
    ProgressDialog progressDialog;

    // Creating FirebaseAuth object.
    FirebaseAuth firebaseAuth ;
    FirebaseFirestore firestore;
    DbHelper db;
    private static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DbHelper(this);
        reg = findViewById(R.id.Button_SignUp);
        login = (Button)findViewById(R.id.Button_LoginActivity);
        email = (EditText)findViewById(R.id.User_email);
        password = (EditText)findViewById(R.id.User_password);
        name = findViewById(R.id.User_name);
        //reg.setOnClickListener(this);
       // login.setOnClickListener(this);



        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString();
                String Email = email.getText().toString();
                String pass = password.getText().toString();
                if(Email.isEmpty() && pass.isEmpty()){
                    displayToast("Username/password field empty");
                }else{
                    db.addUser(Name,Email,pass);
                    displayToast("User registered");
                    finish();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);

                startActivity(intent);

            }
        });

    }



    private void register(){

    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}