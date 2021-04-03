package com.example.textread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Translate extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    SharedPreferences sharedPreferences,sharedPreferences1;
    EditText data,number;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    Spinner chlanguage;
    FirebaseTranslator translator;
    // Session session;
    StorageReference storageReference;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    DrawerLayout drawerLayout;
    boolean getLoginStatus,isGetLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        drawerLayout = findViewById(R.id.drawer_layout);

        data = findViewById(R.id.data);
        data.setTextIsSelectable(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //userID = Objects.requireNonNull(auth.getCurrentUser()).getUid();


        number = findViewById(R.id.number);
        chlanguage=findViewById(R.id.language);
        ActivityCompat.requestPermissions(Translate.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = getSharedPreferences("googleLogin", Context.MODE_PRIVATE);
        getLoginStatus = sharedPreferences.getBoolean("googleLogin", false);
//        if (getLoginStatus) {
//            navigationView.getMenu().removeItem(R.id.changePass);
//        }

        sharedPreferences1 = getSharedPreferences("facebookLogin", Context.MODE_PRIVATE);
        isGetLoginStatus = sharedPreferences1.getBoolean("facebookLogin", false);

        if (getLoginStatus || isGetLoginStatus) {
            navigationView.getMenu().removeItem(R.id.changePass);
        }

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Translate.this);
                builder.setTitle("Send");
                builder.setMessage("Send Data");

                builder.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(), "Cancel is clicked", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("WhatsApp",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String text = data.getText().toString();
                                String toNumber = number.getText().toString();


                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="  + "&text=" + text));
                                startActivity(intent);

                            }
                        });
                builder.setPositiveButton("Message",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String text1 = data.getText().toString();
                                String phone_Num = number.getText().toString();

                                try {
                                    SmsManager sms = SmsManager.getDefault(); // using android SmsManager
                                    sms.sendTextMessage(phone_Num, null, text1, null, null);
                                    Toast.makeText(Translate.this, "Sms Send", Toast.LENGTH_SHORT).show();// adding number and text
                                } catch (Exception e) {
                                    Toast.makeText(Translate.this, "Sms not Send", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });


                builder.show();
            }
        });
        chlanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectLang = parent.getItemAtPosition(position).toString();
                switch(selectLang){
                    case "Marathi":
// assigning div item list defined in XML to the div Spinner
                        FirebaseTranslatorOptions options =
                                new FirebaseTranslatorOptions.Builder()
// below line we are specifying our source language.
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
// in below line we are displaying our target language.
                                        .setTargetLanguage(FirebaseTranslateLanguage.MR)
// after that we are building our options.
                                        .build();
                        translator= FirebaseNaturalLanguage.getInstance().getTranslator(options);
                        String string = data.getText().toString();
                        downloadModal(string);
                        break;

                    case "Hindi":
                        FirebaseTranslatorOptions options1 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                        .build();
                        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options1);
                        String string1 = data.getText().toString();
                        downloadModal(string1);
                        break;
                    case "Urdu":
                        FirebaseTranslatorOptions options2 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.UR)
                                        .build();
                        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options2);
                        String string2 = data.getText().toString();
                        downloadModal(string2);
                        break;
                    case "French":
                        FirebaseTranslatorOptions options3 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                        .build();
                        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options3);
                        String string3 = data.getText().toString();
                        downloadModal(string3);
                        break;
                    case "Japanese":
                        FirebaseTranslatorOptions options4 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                        .build();
                        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options4);
                        String string4 = data.getText().toString();
                        downloadModal(string4);
                        break;
                    case "German":
                        FirebaseTranslatorOptions options5 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                        .build();
                        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options5);
                        String string5 = data.getText().toString();
                        downloadModal(string5);
                        break;
                    case "Spanish":
                        FirebaseTranslatorOptions options6 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.ES)
                                        .build();
                        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options6);
                        String string6 = data.getText().toString();
                        downloadModal(string6);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void ClickMenu(View view) {
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public void ClickLogo(View view) {
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //when drawer is open close it
            drawerLayout.closeDrawer(GravityCompat.START);

        }
    }
    public static void redirectActivity(Activity activity, Class aClass) {
        //initialized intent
        Intent intent = new Intent(activity, aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);

    }

    /*
        public void ClickHome(View view) {
            //recreate activity
            redirectActivity(this, ScannerActivity.class);

        }
        public void ClickChangePass(View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Display.this);
            builder.setTitle("Change Password");
            builder.setMessage("Google Logged in");


            builder.setNegativeButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Toast.makeText(Display.this, "Cannot change password if login is done using Google ID", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Display.this, Profile.class);
                            startActivity(intent);
                        }
                    });
            builder.setPositiveButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Intent intent = new Intent(Display.this, Change_Password.class);
                            startActivity(intent);

                        }
                    });


            builder.show();
        }

        public void ClickProfile(View view) {
            //recreate 0
            redirectActivity(this, Profile.class);
        }



        public void ClickLogout(View view) {
            //recreate activity
            logout();
        }
        public  void logout(){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            //session.setLoggedin(true);

            auth.signOut();
            finish();
            Intent intent = new Intent(Display.this, Login.class);
            Toast.makeText(Display.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
            startActivity(intent);

        }
    */
    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }
    private void downloadModal(final String input) {
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Translate.this,"Modal is been downloading",Toast.LENGTH_SHORT).show();
                translateLanguage(input);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Translate.this,"Failed to download",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateLanguage(String input) {
        translator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                data.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Translate.this,"Fail to tranlate",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.camera) {
            Intent intent = new Intent(Translate.this, ScannerActivity.class);
            Toast.makeText(this, "Scan Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(Translate.this, Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        //  if(!getLoginStatus){
        if (id == R.id.changePass) {
            Intent intent = new Intent(Translate.this, Change_Password.class);
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.translate) {
            Intent intent = new Intent(Translate.this, Translate.class);
            Toast.makeText(this, "Translate Text", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        if (id == R.id.logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            //session.setLoggedin(true);
            SharedPreferences.Editor editor2 = sharedPreferences1.edit();
            editor2.clear();
            editor2.apply();

            auth.signOut();
            finish();
            Intent intent = new Intent(Translate.this, Login.class);
            Toast.makeText(Translate.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }


        return false;
    }
}