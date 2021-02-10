package com.example.ipub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminRequest extends AppCompatActivity implements View.OnClickListener {

    EditText admin_request_name;
    EditText admin_request_pub_name;
    EditText admin_request_email;
    EditText admin_request_phone;
    Button admin_request_send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_request);
        initViews();
        admin_request_send.setOnClickListener(this);

    }

    // Initialize activity views
    private void initViews() {
        admin_request_name = findViewById(R.id.admin_request_name);
        admin_request_pub_name = findViewById(R.id.admin_request_pub_name);
        admin_request_email = findViewById(R.id.admin_request_email);
        admin_request_phone = findViewById(R.id.admin_request_phone);
        admin_request_send = findViewById(R.id.admin_request_send_btn);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.admin_request_send_btn:
                if (admin_request_name.getText().toString().equals("") || admin_request_pub_name.getText().toString().equals("") ||
                        admin_request_email.getText().toString().equals("") || admin_request_phone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                }

                // Phone validation
                else if (admin_request_phone.getText().toString().length() != 10) {
                    Toast.makeText(getApplicationContext(), "אנא הזן מספר נייד חוקי", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendEmail();
                }
                break;
        }

    }

    private void sendEmail() {
        String Memail = "ipub2020@gmail.com";
        String Msubject = "בקשה לניהול פאב";
        String MfullName = admin_request_name.getText().toString();
        String MpubName = admin_request_pub_name.getText().toString();
        String MadminEmail = admin_request_email.getText().toString();
        String Mphone = admin_request_phone.getText().toString();
        String Mmessage;
        Mmessage = MfullName + "\n\n" + MpubName + "\n\n" + MadminEmail + "\n\n" + Mphone;

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, Memail, Msubject, Mmessage);

        javaMailAPI.execute();

        Toast.makeText(getApplicationContext(), "הבקשה נשלחה בהצלחה", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AdminRequest.this, MainActivity.class));

    }
}
