package com.example.textread;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class Login extends AppCompatActivity {
    EditText email, password;

    // Creating string to hold values.
    String EmailHolder, PasswordHolder;

    Button Login, SignUP, ButtonGoToLoginActivity;
    // Creating Boolean to hold EditText empty true false value
    Boolean EditTextEmptyCheck,IsLoggedIn=false;

    // Creating progress dialog
    ProgressDialog progressDialog;

    // Creating FirebaseAuth object
    FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 1;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Assign ID's
        email = (EditText) findViewById(R.id.editText_email);
        password = (EditText) findViewById(R.id.editText_password);
        //LinearLayout linearlayout=(LinearLayout)findViewById(R.id.changePassword);


        Login = (Button) findViewById(R.id.button_login);
        SignUP = (Button) findViewById(R.id.button_SignUP);
        ButtonGoToLoginActivity = (Button) findViewById(R.id.sign_in_button);
        //session = new Session(this);
        // final TextView changePass=findViewById(R.id.changePass);

        // session=new Session(this);

        progressDialog = new ProgressDialog(Login.this);
        // Assign FirebaseAuth instance to FirebaseAuth object
        firebaseAuth = FirebaseAuth.getInstance();
        // session.setLoggedin(true);
        if (firebaseAuth.getCurrentUser() != null) {

            // Finishing current Login Activity
            finish();

            Intent intent = new Intent(Login.this, ScannerActivity.class);
            startActivity(intent);

        }
        // SharedPreferences mPrefs = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
      /*  if(session.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
            startActivity(intent);
        } else {
            MenuItem menu=(MenuItem) findViewById(R.id.changePass);
            menu.setVisible(true);
        }*/

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // For set user loggedin status
                // IsLoggedIn=true;
                // session.save(null,"",true);

                CheckEditTextIsEmptyOrNot();
                if (EditTextEmptyCheck) {

                    LoginFunction();

                } else {
                    Toast.makeText(Login.this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button signup = findViewById(R.id.button_SignUP);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Opening the Login Activity using Intent
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);

            }
        });
        //to reset the  password
        TextView resetpass = findViewById(R.id.resetpass);
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Finishing current Main Activity.
                finish();

                // Opening the Login Activity using Intent.
                Intent intent = new Intent(Login.this, ResetPass.class);
                startActivity(intent);

            }
        });
        // Adding click listener to ButtonGoToLoginActivity button.
        ButtonGoToLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Finishing current Main Activity.
                finish();

                // Opening the Login Activity using Intent.
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("774826098969-1ap6c3iugur6oa5j1r1kj0igk4tgj7c9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Button signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                signIn();


            }
        });

    }



    public void CheckEditTextIsEmptyOrNot() {

        // Getting value form EditText and fill into Holder string variable
        EmailHolder = email.getText().toString().trim();

        PasswordHolder = password.getText().toString().trim();

        // Checking Both EditText is empty or not
        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {
            // If any of EditText is empty then set value as false
            EditTextEmptyCheck = false;
        } else {
            // If any of EditText is empty then set value as true
            EditTextEmptyCheck = true;
        }

    }

    public void LoginFunction() {

        progressDialog.setMessage("Please Wait");

        // Showing progressDialog.
        progressDialog.show();

        // Calling  signInWithEmailAndPassword buildin  function with firebase object and passing EmailHolder and PasswordHolder inside it
        firebaseAuth.signInWithEmailAndPassword(EmailHolder, PasswordHolder)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // task  Successful
                        if (task.isSuccessful()) {
                            //session.setLoggedIn(getApplicationContext(), true);

                            progressDialog.dismiss();
                            Intent intent = new Intent(Login.this, ScannerActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Email or Password Not found, Please Try Again", Toast.LENGTH_LONG).show();


                        }
                    }
                });

    }

    public void signIn() {

        SharedPreferences sharedPreferences = getSharedPreferences("googleLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("googleLogin", true);
        editor.apply();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);



    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);

                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(), null);
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "Logged in Successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), ScannerActivity.class));


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }

}

