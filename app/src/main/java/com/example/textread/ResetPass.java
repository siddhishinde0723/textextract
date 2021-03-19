package com.example.textread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPass extends AppCompatActivity {


    private EditText edtEmail;
    private Button btnResetPassword,btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        edtEmail = (EditText) findViewById(R.id.edt_reset_email);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        mAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //build in method  which will send the link to reset the password to your email id entered
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPass.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ResetPass.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ResetPass.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

}