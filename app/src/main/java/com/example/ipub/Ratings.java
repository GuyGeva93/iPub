package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Ratings extends AppCompatActivity {

    EditText txt_rate_name;
    EditText txt_rate;
    Button btn_send_rate;
    String pub_name;
    FirebaseDatabase database;
    DatabaseReference mRef;
    RatingBar ratingBar;
    int count = 0;
    int average = 0;
    long millisecond;
    boolean flag = false;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        txt_rate_name = findViewById(R.id.txt_rate_name);
        txt_rate = findViewById(R.id.txt_rate);
        btn_send_rate = findViewById(R.id.btn_send_rate);
        pub_name = getIntent().getStringExtra("pub_name");
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings");
        ratingBar = findViewById(R.id.rating_bar);
        tinyDB = new TinyDB(this);


        btn_send_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                millisecond = System.currentTimeMillis();
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            if (Long.parseLong(snapshot.getValue().toString()) == tinyDB.getLong(pub_name, 0)) {
                                flag = true;
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (flag == true) {
                    Toast.makeText(getApplicationContext(), "כבר דירגת את הפאב הנוכחי, לא ניתן לדרג פאב פעמיים", Toast.LENGTH_LONG).show();
                } else {
                    if (ratingBar.getRating() == 0) {
                        Toast.makeText(getApplicationContext(), "אנא דרג את הפאב", Toast.LENGTH_LONG).show();
                    } else if (txt_rate_name.getText().toString().equals("") || txt_rate.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "אנא מלא את כל השדות", Toast.LENGTH_LONG).show();
                    } else {
                        mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings").child(String.valueOf(millisecond));
                        tinyDB.putLong(pub_name, millisecond);
                        mRef.child("Name").setValue(txt_rate_name.getText().toString());
                        mRef.child("Comment").setValue(txt_rate.getText().toString());
                        mRef.child("StarsRate").setValue(ratingBar.getRating());
                        mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings");
                        mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    average += Integer.parseInt(snapshot.child("StarsRate").getValue().toString());
                                    count++;
                                }
                                average /= count;
                                mRef = database.getReference().child("Pubs").child(pub_name).child("RatingAverage");
                                mRef.setValue(average);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(getApplicationContext(), "הביקורת נשלחה בהצלחה!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Ratings.this, MainActivity.class));
                    }
                }
            }
        });
    }
}