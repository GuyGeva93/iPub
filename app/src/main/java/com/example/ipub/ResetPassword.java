package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {
    Button reset_password_send;
    EditText reset_password_email;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initViews();
        reset_password_send.setOnClickListener(this);

    }

    private void initViews() {
        reset_password_email = findViewById(R.id.reset_password_email);
        reset_password_send = findViewById(R.id.reset_password_send);
    }

    @Override
    public void onClick(View view) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(reset_password_email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(ResetPassword.this, AdminLogin.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "כתובת המייל לא קיימת במערכת", Toast.LENGTH_LONG).show();
            }
        });
    }
}