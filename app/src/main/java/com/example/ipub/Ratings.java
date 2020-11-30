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

import java.util.ArrayList;

public class Ratings extends AppCompatActivity {

    EditText txt_rate_name;
    EditText txt_rate;
    Button btn_send_rate;
    String pub_name;
    FirebaseDatabase database;
    DatabaseReference mRef;
    DatabaseReference mRef_commentLine;
    RatingBar ratingBar;
    int stars = 0;
    public ArrayList<CommentInfo> commentInfos;
    int count;


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
        mRef_commentLine = database.getReference().child("Pubs").child(pub_name).child("Ratings");
        ratingBar = findViewById(R.id.rating_bar);
        commentInfos = new ArrayList<CommentInfo>();

        btn_send_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(getApplicationContext(), "אנא דרג את הפאב", Toast.LENGTH_LONG).show();
                } else if (txt_rate_name.getText().toString().equals("") || txt_rate.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "אנא מלא את כל השדות", Toast.LENGTH_LONG).show();
                } else {
                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                count = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                                }
                            }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings").child(String.valueOf(count+1));
                    mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings").child(txt_rate_name.getText().toString());
                    mRef.child("Comment").setValue(txt_rate.getText().toString());
                    mRef.child("StarsRate").setValue(ratingBar.getRating());

                    Toast.makeText(getApplicationContext(), "הביקורת נשלחה בהצלחה!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Ratings.this, MainActivity.class));
                }
            }
        });

        mRef_commentLine.addValueEventListener(new ValueEventListener() {
            String name;
            String comment;
            float rating;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //name = snapshot.;
                    comment = snapshot.child("Comment").getValue().toString();
                    rating = Float.parseFloat(snapshot.child("StarsRate").getValue().toString());
                    commentInfos.add(new CommentInfo(name, comment, rating));
                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}