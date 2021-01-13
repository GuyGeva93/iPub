package com.example.ipub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class AddPubRequest extends AppCompatActivity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener {

    EditText pubName, pubAddress, pubCity, email;
    CheckBox isAdmin;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pub_request);

        initViews();
        btnSend.setOnClickListener(this);
        isAdmin.setOnCheckedChangeListener(this);

    }

    private void initViews() {
        pubName = findViewById(R.id.addPubRequest_pubName);
        pubAddress = findViewById(R.id.addPubRequest_pubAddress);
        pubCity = findViewById(R.id.addPubRequest_pubCity);
        email = findViewById(R.id.addPubRequest_email);
        isAdmin = findViewById(R.id.addPubRequest_checkBox);
        btnSend = findViewById(R.id.addPubRequest_sendBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.addPubRequest_sendBtn:
                if(pubName.getText().toString().equals("") || pubAddress.getText().toString().equals("") ||
                pubCity.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext() , "אנא מלא את כל השדות" , Toast.LENGTH_SHORT).show();
                }
                else{
                    sendEmail();
                }
                break;

        }

    }

    private void sendEmail() {

        String Memail = "ipub2020@gmail.com";
        String Msubject = "בקשה להוספת פאב חדש";
        String pub_name = pubName.getText().toString();
        String pub_address = pubAddress.getText().toString();
        String pub_city = pubCity.getText().toString();
        String admin_email = email.getText().toString();
        String Mmessage;
        Mmessage = pub_name + "\n\n" + pub_address + "\n\n" + pub_city + "\n\n" + admin_email;

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, Memail, Msubject, Mmessage);

        javaMailAPI.execute();

        Toast.makeText(getApplicationContext(), "הבקשה נשלחה בהצלחה", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddPubRequest.this, MainActivity.class));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            email.setVisibility(buttonView.VISIBLE);
        }
        else{
            email.setVisibility(buttonView.INVISIBLE);

        }
    }
}