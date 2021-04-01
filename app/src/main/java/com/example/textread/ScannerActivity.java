package com.example.textread;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
/*
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
*/
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class ScannerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SurfaceView camareview;
    CameraSource cameraSource;
    TextView textView, text;
    FirebaseAuth firebaseAuth;
    final int RequestCameraPermissionID = 1001;
    ClipboardManager clipboardManager;
    Button paste;
    String userID;
    ImageView profile, profileimg;
    DrawerLayout drawerLayout;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    FirebaseUser user;

    Timer timer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    boolean getLoginStatus, isGetLoginStatus;
    SharedPreferences sharedPreferences, sharedPreferences1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(camareview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @SuppressLint("ServiceCast")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        //  session=new Session(this);
        //Assign Id
        camareview = (SurfaceView) findViewById(R.id.surface_view);
        textView = findViewById(R.id.textdata);
        firebaseAuth = FirebaseAuth.getInstance();
        //paste=findViewById(R.id.paste);
        user = firebaseAuth.getCurrentUser();
        text = findViewById(R.id.text);
        profileimg = findViewById(R.id.profileimg);
        fStore = FirebaseFirestore.getInstance();
        profile = findViewById(R.id.profile);
        //userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        drawerLayout = findViewById(R.id.drawer_layout);


        //timer
        timer = new Timer();

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


        /* StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profile);
            }
        });
        StorageReference profileRef1 = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileimg);
            }
        });*/


    /*   DocumentReference documentReference1 = fStore.collection("users").document(userID);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                text.setText(documentSnapshot.getString("Name"));
            }
        });*/

/*        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });*/

        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("ScannerActivity", "Detector dependencies are not yet avaliable");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            camareview.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ScannerActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(camareview.getHolder());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                    cameraSource.stop();


                }
            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();

                    if (items.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); ++i) {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                    textView.setText(stringBuilder.toString());
                                    //Toast.makeText(getApplicationContext(),"Ok ready to capture 1...2...3...Click",Toast.LENGTH_LONG).show();

                                }

                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        cameraSource.stop();

                                        String s = textView.getText().toString();
                                        //cameraSource.stop();
                                        Intent intent = new Intent(ScannerActivity.this, Display.class);
                                        intent.putExtra("", s);
                                        startActivity(intent);
                                        finish();
                                        //cameraSource.stop();
                                    }
                                }, 3500);


                                //   Toast.makeText(getApplicationContext(), "Ok ready to capture", Toast.LENGTH_SHORT).show();
                                //cameraSource.stop();
                                // String s = textView.getText().toString();
                                // cameraSource.stop();
                                // Intent intent = new Intent(ScannerActivity.this, Display.class);
                                //intent.putExtra("", s);
                                //startActivity(intent);
                                //finish();
                            }

                        });
                    }

                }

            });

/*
            final TextView textView=findViewById(R.id.textdata);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = textView.getText().toString();
                    Intent intent = new Intent(ScannerActivity.this, Display.class);
                    intent.putExtra("", s);
                    startActivity(intent);
                    finish();
                }
            });
*/

   /*
        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doProcess(v);
                textView.setText("");
            }
        });/*
        final Button paste=findViewById(R.id.paste);
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (manager != null && manager.getPrimaryClip() != null && manager.getPrimaryClip().getItemCount() > 0) {
                    paste.setText(manager.getPrimaryClip().getItemAt(0).getText().toString());
                }
               // textView.setText(item.getText().toString());
                Intent intent = new Intent(ScannerActivity.this, Display.class);
                intent.putExtra("", (Parcelable) textView);
                startActivity(intent);
            }
        });*/

        }/*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void doProcess(View view) {
        //open the camera  create an Intent object
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        //from bundle, extract the image
        Bitmap bitmap = (Bitmap) bundle.get("data");
        //set image in imageview
        imageView.setImageBitmap(bitmap);
        //process the image
        //create a FirebaseVisionImage object from a Bitmap object
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        //Get an instance of FirebaseVision
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        // Create an instance of FirebaseVisionTextRecognizer
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
        // Create a task to process the image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        //if task is success
        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String s = firebaseVisionText.getText();
                Intent intent = new Intent(ScannerActivity.this, Display.class);
                intent.putExtra("", s);
                startActivity(intent);
            }
        });
        // if task is failure
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNul9l Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }*/

    }

    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
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
        final StorageReference fileRef = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "profile.jpg");
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
                Toast.makeText(ScannerActivity.this, "Fail", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.camera) {
            Intent intent = new Intent(ScannerActivity.this, ScannerActivity.class);
            Toast.makeText(this, "Scan Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(ScannerActivity.this, Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (!getLoginStatus || !isGetLoginStatus) {
            if (id == R.id.changePass) {
                Intent intent = new Intent(ScannerActivity.this, Change_Password.class);
                Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
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
            Intent intent = new Intent(ScannerActivity.this, Login.class);
            Toast.makeText(ScannerActivity.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
            startActivity(intent);

        }

        return false;
    }

}
