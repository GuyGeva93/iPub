package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagementPubsList extends AppCompatActivity {

    DatabaseReference mRef;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    List<String> pubsList;
    String userId;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    String pubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_pubs_list);

        initVariables();
        initViews();
        readFromDB();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pubName = listView.getItemAtPosition(position).toString();
                Intent i = new Intent(ManagementPubsList.this , EditPub.class);
                i.putExtra("pubName" ,pubName);
                startActivity(i);

            }
        });

    }

    private void setList() {
        arrayAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, pubsList );
        listView.setAdapter(arrayAdapter);
    }

    private void initViews() {
        listView = findViewById(R.id.management_pubs_list);
    }

    private void readFromDB() {

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    pubName = dataSnapshot.child("managers").getValue().toString();
                    if(pubName.contains(userId)){
                        pubsList.add(dataSnapshot.child("TitleName").getValue().toString());
                    }
                }
                setList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initVariables() {
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Pubs");
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        pubsList = new ArrayList<>();
    }


}