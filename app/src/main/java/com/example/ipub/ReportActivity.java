package com.example.ipub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReportActivity extends AppCompatActivity {

    TextView home , favorites;
    EditText name , subject , message;
    Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        home = findViewById(R.id.home_button);
        favorites = findViewById(R.id.favorites_button);
        name = findViewById(R.id.report_name);
        subject = findViewById(R.id.report_subject);
        message = findViewById(R.id.report_message);
        send_btn = findViewById(R.id.report_send_btn);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("") || subject.getText().toString().equals("") || message.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext() ,"בבקשה מלא את כל השדות" , Toast.LENGTH_SHORT).show();
                }
                else{
                    sendEmail();
                }

            }
        });




        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReportActivity.this , MainActivity.class);
                startActivity(i);
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReportActivity.this , FavoritesActivity.class);
                startActivity(i);
            }
        });
    }

    private void sendEmail(){
        String Memail = "ipub2020@gmail.com";
        String MfullName = name.getText().toString();
        String Msubject = subject.getText().toString();
        String Mmessage = message.getText().toString();
        Mmessage = MfullName + "\n\n" + Mmessage;

        JavaMailAPI javaMailAPI = new JavaMailAPI(this , Memail , Msubject , Mmessage);

        javaMailAPI.execute();

        Toast.makeText(getApplicationContext() ,"ההודעה נשלחה בהצלחה" ,Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ReportActivity.this , MainActivity.class);
        startActivity(i);
    }

}