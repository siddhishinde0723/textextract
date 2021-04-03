package com.example.textread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nullable;

public class Extraction extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    TextView textView;
    ImageView add,gallery;
    boolean getLoginStatus, isGetLoginStatus;
    SharedPreferences sharedPreferences, sharedPreferences1;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    FirebaseAuth firebaseAuth;
    static final int CAPTURE_IMAGE_REQUEST = 1,SELECT_FILE=0;
    private Uri picUri;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extraction);
        firebaseAuth = FirebaseAuth.getInstance();
        //paste=findViewById(R.id.paste);
        user = firebaseAuth.getCurrentUser();
        gallery=findViewById(R.id.gallery_display);
        drawerLayout = findViewById(R.id.drawer_layout);
        textView=findViewById(R.id.textView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_menu);
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

        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImg();
            }
        });

    }
    private void SelectImg(){
        final CharSequence[] items = {"Scan","Camera","Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Extraction.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (items[i].equals("Scan")) {
                    Intent intent = new Intent(Extraction.this, ScannerActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);

            }else if(items[i].equals("Gallery")){

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Select File"), SELECT_FILE);

                }else if(items[i].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode==CAPTURE_IMAGE_REQUEST){
// CropImage.activity(picUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
                picUri = data.getData();

                Bundle bundle = data.getExtras();
//from bundle, extract the image
                Bitmap bitmap = (Bitmap) bundle.get("data");
//set image in imageview
                gallery.setImageBitmap(bitmap);
                FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
//2. Get an instance of FirebaseVision
                FirebaseVision firebaseVision = FirebaseVision.getInstance();
//3. Create an instance of FirebaseVisionTextRecognizer
                FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
//4. Create a task to process the image
                Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
//5. if task is success
                task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {

                        String s = firebaseVisionText.getText();
                        saveToGallery();
                        Intent intent = new Intent(Extraction.this, Display.class);
                        intent.putExtra("", s);
                        startActivity(intent);


                    }
                });

//6. if task is failure
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }if(requestCode==SELECT_FILE) {
// CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
                Uri selectimg = data.getData();
                gallery.setImageURI(selectimg);

                try {
                    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromFilePath(this,selectimg);
                    FirebaseVision firebaseVision = FirebaseVision.getInstance();
                    FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
                    Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
                    task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {

                            String s = firebaseVisionText.getText();
                            Intent intent = new Intent(Extraction.this, Display.class);
                            intent.putExtra("", s);
                            startActivity(intent);


                        }
                    });

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }






    }

    private void saveToGallery() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) gallery.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/Data");
        dir.mkdirs();

        String filename = String.format("%d.jpg",System.currentTimeMillis());
        File outFile = new File(dir,filename);
        try{
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        try{
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            outputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.camera) {
            Intent intent = new Intent(Extraction.this, Extraction.class);
            Toast.makeText(this, "Scan Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(Extraction.this, Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (!getLoginStatus || !isGetLoginStatus) {
            if (id == R.id.changePass) {
                Intent intent = new Intent(Extraction.this, Change_Password.class);
                Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
        if (id == R.id.translate) {
            Intent intent = new Intent(Extraction.this, Translate.class);
            Toast.makeText(this, "Translate Text", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        if (id == R.id.logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            SharedPreferences.Editor editor2 = sharedPreferences1.edit();
            editor2.clear();
            editor2.apply();

            //session.setLoggedin(true);
            finish();
            firebaseAuth.signOut();
            Intent intent = new Intent(Extraction.this, Login.class);
            Toast.makeText(Extraction.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
            startActivity(intent);

        }

        return false;
    }


}