package com.example.textread;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    EditText email, password;

    // Creating string to hold values.
    String EmailHolder, PasswordHolder;

    Button Login, SignUP, ButtonGoToLoginActivity;
    // Creating Boolean to hold EditText empty true false value
    Boolean EditTextEmptyCheck,IsLoggedIn;

    // Creating progress dialog
    ProgressDialog progressDialog;

    // Creating FirebaseAuth object
    FirebaseAuth firebaseAuth;
    private DbHelper db;
    private SQLiteOpenHelper openHelper;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 1;
    private Session session;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DbHelper(this);
      //  db = openHelper.getReadableDatabase();
        session = new Session(this);
        Login = (Button)findViewById(R.id.button_login);
        SignUP = (Button)findViewById(R.id.button_SignUP);
        email = (EditText)findViewById(R.id.editText_email);
        password = (EditText)findViewById(R.id.editText_password);

      /*  if(session.loggedin()){
            startActivity(new Intent(Login.this,ScannerActivity.class));
            finish();
        }*/
        SignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);

                startActivity(intent);
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               login();
            }
        });
    }




    private void login(){
        String Email = email.getText().toString();
        String pass = password.getText().toString();

        if(db.getUser(Email,pass)){
            session.setLoggedin(true);

            startActivity(new Intent(Login.this, ScannerActivity.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Wrong email/password",Toast.LENGTH_SHORT).show();
        }
    }
}


