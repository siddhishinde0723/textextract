package com.example.textread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private static final int CAMERA_REQUEST_CODE=200,STORAGE_REQUEST_CODE=400,IMAGE_PICK_GALLERY_CODE=1000,IMAGE_PICK_CAMERA_CODE=1001;
    String cameraPermission[],storagePermission[];
    Uri image_uri;

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
        cameraPermission=new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);


        ActivityCompat.requestPermissions(Extraction.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(Extraction.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    101);
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
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }

            }else if(items[i].equals("Gallery")){

                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickGallery();
                    }

                }else if(items[i].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }
    private void pickGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);



    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){

            case CAMERA_REQUEST_CODE:

                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:

                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                gallery.setImageURI(resultUri);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) gallery.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItems = items.valueAt(i);
                        sb.append(myItems.getValue());
                        sb.append("\n");

                    }

                    textView.setText(sb.toString());
                    String s = textView.getText().toString();
                    Intent intent = new Intent(Extraction.this, Display.class);
                    intent.putExtra("", s);
                    startActivity(intent);

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();


            }
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