package com.example.textread;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView name,name1,email1,email,text,change;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID;
    ImageView profile,profile1;
    DrawerLayout drawerLayout;
    StorageReference storageReference;
    GoogleSignInOptions gso;
    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;
    LinearLayout linearLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    Session session;
    Login status;

    @SuppressLint({"SetTextI18n", "CheckResult", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        status=new Login();
        name = findViewById(R.id.name);
        text = findViewById(R.id.text);
        // change=findViewById(R.id.change);
        profile1 = findViewById(R.id.profile1);
        email = findViewById(R.id.email);
        name1 = findViewById(R.id.name1);
        email1 = findViewById(R.id.email1);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        profile = findViewById(R.id.profile);
        linearLayout = findViewById(R.id.click);
//        userID = auth.getCurrentUser().getUid();
   //     user = auth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();
        drawerLayout = findViewById(R.id.drawer_layout);

     /*   GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Profile.this);
        if(acct!=null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profile);
            name1.setText(user.getDisplayName());
            email1.setText(user.getEmail());
            profile1.setVisibility(View.INVISIBLE);

        }
*/  actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);




/*        StorageReference profileRef = storageReference.child("users/" + auth.getCurrentUser().getUid() + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profile1);

            }
        });
*/
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile1.setVisibility(View.VISIBLE);
            }
        });
        profile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });
/*
        DocumentReference documentReference1 = fStore.collection("users").document(userID);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                name.setText(documentSnapshot.getString("Name"));
                email.setText(documentSnapshot.getString("Email"));

            }
        });
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                name1.setText(user.getDisplayName());
                email1.setText(user.getEmail());
            }
        });
*/
    }

    protected void onActivityResult(int requestCode,int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //  profile.setImageURI(imageUri);
                uploadImage(imageUri);




            }
        }
    }

    private void uploadImage(Uri imageUri) {
        //upload image to firebase
        final StorageReference fileRef = storageReference.child("users/"+auth.getCurrentUser().getUid()+"profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri).into(profile);

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this,"Fail",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ClickMenu(View view){
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void  openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public void ClickLogo(View view){
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            //when drawer is open close it
            drawerLayout.closeDrawer(GravityCompat.START);

        }
    }/*
    public void ClickHome(View view){
        //recreate activity
        redirectActivity(this,ScannerActivity.class);

    }
    public void ClickProfile(View view){
        //recreate activity
        recreate();
    }

    public void ClickChangePass(View view){
        //recreate activity
        AlertDialog.Builder builder = new AlertDialog.Builder(
                Profile.this);
        builder.setTitle("Chnage Password");
        builder.setMessage("Have you done Google Logged in");


        builder.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(Profile.this, "Cannot change password if login is done using Google ID", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Profile.this, Profile.class);
                        startActivity(intent);
                    }
                });
        builder.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(Profile.this, Change_Password.class);
                        startActivity(intent);

                    }
                });


        builder.show();





}

    public static void redirectActivity(Activity activity, Class aClass) {
        //initialized intent
        Intent intent = new Intent(activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);

    }*/
    public void ClickLogout(View view) {
        //recreate activity
        logout();
    }
    public  void logout(){
      //  session.setLoggedin(false);
        auth.signOut();
        finish();

        Intent intent = new Intent(Profile.this, Login.class);
        Toast.makeText(Profile.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.camera) {
            Intent intent = new Intent(Profile.this,ScannerActivity.class);
            Toast.makeText(this, "Scan Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(Profile.this,Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.changePass) {
            Intent intent = new Intent(Profile.this,Change_Password.class);
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }



        return false;
    } public boolean onCreateOptionsMenu(Menu menu) {
        if(status.status == true){
            getMenuInflater().inflate(R.menu.nav_gmenu,menu);
        }
        getMenuInflater().inflate(R.menu.nav_menu,menu);
        /*session=new Session();
        if(session.loggedin()){
            MenuItem items = menu.findItem(R.id.changePass);
            items.setVisible(false);
        }
*/
        return true;
    }


}


