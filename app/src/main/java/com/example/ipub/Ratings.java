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
import android.widget.ListView;
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


    String pub_name;
    FirebaseDatabase database;
    DatabaseReference mRef;
    TinyDB tinyDB;
    CommentItemListAdapter adapter;
    ArrayList<CommentInfo> commentsList = new ArrayList<CommentInfo>();
    ListView commentsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);


        pub_name = getIntent().getStringExtra("pub_name");
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings");
        tinyDB = new TinyDB(this);
        adapter = new CommentItemListAdapter(this , R.layout.comment_item ,commentsList);
        commentsListView = findViewById(R.id.comments_list_view);

        readCommentsFromDB();
    }

    private void readCommentsFromDB() {

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CommentInfo temp = dataSnapshot.getValue(CommentInfo.class);
                    commentsList.add(temp);
                }
                adapter.SetList(commentsList);
                commentsListView.setAdapter(adapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}