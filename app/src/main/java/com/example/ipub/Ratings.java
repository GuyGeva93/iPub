package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Ratings extends AppCompatActivity {

    String pub_name;
    FirebaseDatabase database;
    DatabaseReference mRef;
    TinyDB tinyDB;
    CommentItemListAdapter adapter;
    ArrayList<CommentInfo> commentsList = new ArrayList<CommentInfo>();
    ListView commentsListView;
    DatabaseReference ratingRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        initVariables();
        initViews();
        readCommentsFromDB();
    }

    private void initViews() {
        commentsListView = findViewById(R.id.comments_list_view);
    }

    private void initVariables() {
        pub_name = getIntent().getStringExtra("pub_name");
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings");
        tinyDB = new TinyDB(this);
        adapter = new CommentItemListAdapter(this, R.layout.comment_item, commentsList);
        adapter.setPub_name(pub_name);

    }

    private void deleteComment(final long timeStamp) {
        mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRef.child(timeStamp + "").removeValue();
                recalculateRating();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mRef = database.getReference().child("Pubs").child(pub_name).child("Ratings");

    }

    private void recalculateRating() {
        ratingRef = database.getReference().child("Pubs").child(pub_name).child("Ratings");
        ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int average = 0;
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    average += Integer.parseInt(snapshot.child("rating").getValue().toString());
                    count++;
                }
                if (count > 0) {
                    average /= count;
                }
                ratingRef = database.getReference().child("Pubs").child(pub_name).child("RatingAverage");
                ratingRef.setValue(average);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readCommentsFromDB() {

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CommentInfo temp = dataSnapshot.getValue(CommentInfo.class);
                    commentsList.add(temp);
                }
                setComments();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setComments() {

        for (final CommentInfo c : commentsList) {
            c.setBtnDeleteComment(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "התגובה נמחקה בהצלחה", Toast.LENGTH_LONG).show();
                    deleteComment(c.getTimeStamp());

                }
            });
        }

        adapter.SetList(commentsList);
        commentsListView.setAdapter(adapter);
    }


}