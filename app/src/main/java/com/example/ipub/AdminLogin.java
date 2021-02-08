package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLogin extends AppCompatActivity implements View.OnClickListener {

    EditText userEmail, userPass;
    FirebaseAuth mAuth;
    TextView admin_forgot_password;
    Button admin_btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        initViews();
        admin_btn_login.setOnClickListener(this);
        admin_forgot_password.setOnClickListener(this);
    }

    private void initViews() {
        userEmail = findViewById(R.id.admin_email);
        userPass = findViewById(R.id.admin_password);
        mAuth = FirebaseAuth.getInstance();
        admin_forgot_password = findViewById(R.id.admin_forgot_password);
        admin_btn_login = findViewById(R.id.admin_btn_login);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.admin_btn_login:
                login();
                break;
            case R.id.admin_forgot_password:
                startActivity(new Intent(AdminLogin.this, ResetPassword.class));
                break;
        }
    }

    private void login() {
        mAuth.signInWithEmailAndPassword(userEmail.getText().toString(), userPass.getText().toString())
                .addOnCompleteListener(AdminLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(AdminLogin.this, MainActivity.class));
                            Toast.makeText(getApplicationContext(), "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}