package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class EditPub extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference mRef;
    FirebaseDatabase database;

    String pubName;

    ArrayAdapter<CharSequence> hoursSpinnerAdapter;

    Spinner sundayOpen;
    Spinner sundayClose;
    CheckBox chkSunday;

    Spinner mondayOpen;
    Spinner mondayClose;
    CheckBox chkMonday;

    Spinner tuesdayOpen;
    Spinner tuesdayClose;
    CheckBox chkTuesday;

    Spinner wednesdayOpen;
    Spinner wednesdayClose;
    CheckBox chkWednesday;

    Spinner thursdayOpen;
    Spinner thursdayClose;
    CheckBox chkThursday;

    Spinner fridayOpen;
    Spinner fridayClose;
    CheckBox chkFriday;

    Spinner saturdayOpen;
    Spinner saturdayClose;
    CheckBox chkSaturday;

    EditText Phone;
    EditText website;
    Button submitInfo;
    TextView gallery;

    String sunday;
    String monday;
    String tuesday;
    String wednesday;
    String thursday;
    String friday;
    String saturday;
    String phoneString;
    String websiteString;
    HashMap<String, Object> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pub);

        initVariables();
        initViews();
        initSpinners();
        readFromDB();
        setCheckBoxesListeners();

        submitInfo.setOnClickListener(this);
        gallery.setOnClickListener(this);

    }

    private void setCheckBoxesListeners() {
        chkSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sundayOpen.setEnabled(false);
                    sundayClose.setEnabled(false);
                } else {
                    sundayOpen.setEnabled(true);
                    sundayClose.setEnabled(true);
                }
            }
        });

        chkMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mondayOpen.setEnabled(false);
                    mondayClose.setEnabled(false);
                } else {
                    mondayOpen.setEnabled(true);
                    mondayClose.setEnabled(true);
                }
            }
        });

        chkTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tuesdayOpen.setEnabled(false);
                    tuesdayClose.setEnabled(false);
                } else {
                    tuesdayOpen.setEnabled(true);
                    tuesdayClose.setEnabled(true);
                }
            }
        });

        chkWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wednesdayOpen.setEnabled(false);
                    wednesdayClose.setEnabled(false);
                } else {
                    wednesdayOpen.setEnabled(true);
                    wednesdayClose.setEnabled(true);
                }
            }
        });

        chkThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    thursdayOpen.setEnabled(false);
                    thursdayClose.setEnabled(false);
                } else {
                    thursdayOpen.setEnabled(true);
                    thursdayClose.setEnabled(true);
                }
            }
        });

        chkFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fridayOpen.setEnabled(false);
                    fridayClose.setEnabled(false);
                } else {
                    fridayOpen.setEnabled(true);
                    fridayClose.setEnabled(true);
                }
            }
        });

        chkSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saturdayOpen.setEnabled(false);
                    saturdayClose.setEnabled(false);
                } else {
                    saturdayOpen.setEnabled(true);
                    saturdayClose.setEnabled(true);
                }
            }
        });
    }

    private void initSpinners() {
        hoursSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.OpeningHours, android.R.layout.simple_spinner_dropdown_item);
        hoursSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sundayOpen.setAdapter(hoursSpinnerAdapter);
        sundayClose.setAdapter(hoursSpinnerAdapter);
        mondayOpen.setAdapter(hoursSpinnerAdapter);
        mondayClose.setAdapter(hoursSpinnerAdapter);
        tuesdayOpen.setAdapter(hoursSpinnerAdapter);
        tuesdayClose.setAdapter(hoursSpinnerAdapter);
        wednesdayOpen.setAdapter(hoursSpinnerAdapter);
        wednesdayClose.setAdapter(hoursSpinnerAdapter);
        thursdayOpen.setAdapter(hoursSpinnerAdapter);
        thursdayClose.setAdapter(hoursSpinnerAdapter);
        fridayOpen.setAdapter(hoursSpinnerAdapter);
        fridayClose.setAdapter(hoursSpinnerAdapter);
        saturdayOpen.setAdapter(hoursSpinnerAdapter);
        saturdayClose.setAdapter(hoursSpinnerAdapter);
    }

    private void readFromDB() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sunday = snapshot.child("Sunday").getValue().toString();
                monday = snapshot.child("Monday").getValue().toString();
                tuesday = snapshot.child("Tuesday").getValue().toString();
                wednesday = snapshot.child("Wednesday").getValue().toString();
                thursday = snapshot.child("Thursday").getValue().toString();
                friday = snapshot.child("Friday").getValue().toString();
                saturday = snapshot.child("Saturday").getValue().toString();
                phoneString = snapshot.child("Telephone").getValue().toString();
                websiteString = snapshot.child("Website").getValue().toString();

                attachInfoToViews();


            }

            private void attachInfoToViews() {
                int position;

                //יום ראשון
                if (sunday.equals("סגור")) {
                    chkSunday.setChecked(true);
                    sundayClose.setEnabled(false);
                    sundayOpen.setEnabled(false);
                } else {
                    position = hoursSpinnerAdapter.getPosition(sunday.substring(0, 5));
                    sundayClose.setSelection(position);
                    position = hoursSpinnerAdapter.getPosition(sunday.substring(8));
                    sundayOpen.setSelection(position);
                }

                //יום שני
                if (monday.equals("סגור")) {
                    chkMonday.setChecked(true);
                    mondayOpen.setEnabled(false);
                    mondayClose.setEnabled(false);
                } else {
                    position = hoursSpinnerAdapter.getPosition(monday.substring(0, 5));
                    mondayClose.setSelection(position);
                    position = hoursSpinnerAdapter.getPosition(monday.substring(8));
                    mondayOpen.setSelection(position);
                }

                //יום שלישי
                if (tuesday.equals("סגור")) {
                    chkTuesday.setChecked(true);
                    tuesdayOpen.setEnabled(false);
                    tuesdayClose.setEnabled(false);
                } else {
                    position = hoursSpinnerAdapter.getPosition(tuesday.substring(0, 5));
                    tuesdayClose.setSelection(position);
                    position = hoursSpinnerAdapter.getPosition(tuesday.substring(8));
                    tuesdayOpen.setSelection(position);
                }

                //יום רביעי
                if (wednesday.equals("סגור")) {
                    chkWednesday.setChecked(true);
                    wednesdayOpen.setEnabled(false);
                    wednesdayClose.setEnabled(false);
                } else {
                    position = hoursSpinnerAdapter.getPosition(wednesday.substring(0, 5));
                    wednesdayClose.setSelection(position);
                    position = hoursSpinnerAdapter.getPosition(wednesday.substring(8));
                    wednesdayOpen.setSelection(position);
                }

                //יום חמישי
                if (thursday.equals("סגור")) {
                    chkThursday.setChecked(true);
                    thursdayOpen.setEnabled(false);
                    thursdayClose.setEnabled(false);
                } else {
                    position = hoursSpinnerAdapter.getPosition(thursday.substring(0, 5));
                    thursdayClose.setSelection(position);
                    position = hoursSpinnerAdapter.getPosition(thursday.substring(8));
                    thursdayOpen.setSelection(position);
                }

                //יום שישי
                if (friday.equals("סגור")) {
                    chkFriday.setChecked(true);
                    fridayOpen.setEnabled(false);
                    fridayClose.setEnabled(false);
                } else {
                    position = hoursSpinnerAdapter.getPosition(friday.substring(0, 5));
                    fridayClose.setSelection(position);
                    position = hoursSpinnerAdapter.getPosition(thursday.substring(8));
                    fridayOpen.setSelection(position);
                }

                //יום שבת
                if (saturday.equals("סגור")) {
                    chkSaturday.setChecked(true);
                    saturdayOpen.setEnabled(false);
                    saturdayClose.setEnabled(false);
                } else {
                    position = hoursSpinnerAdapter.getPosition(saturday.substring(0, 5));
                    saturdayClose.setSelection(position);
                    position = hoursSpinnerAdapter.getPosition(saturday.substring(8));
                    saturdayOpen.setSelection(position);
                }

                //טלפון
                Phone.setText(phoneString);

                //אתר
                website.setText(websiteString);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initViews() {

        sundayOpen = findViewById(R.id.sunday_open);
        sundayClose = findViewById(R.id.sunday_close);
        chkSunday = findViewById(R.id.sunday_checkBox);

        mondayOpen = findViewById(R.id.monday_open);
        mondayClose = findViewById(R.id.monday_close);
        chkMonday = findViewById(R.id.monday_checkBox);

        tuesdayOpen = findViewById(R.id.tuesday_open);
        tuesdayClose = findViewById(R.id.tuesday_close);
        chkTuesday = findViewById(R.id.tuesday_checkBox);

        wednesdayOpen = findViewById(R.id.wednesday_open);
        wednesdayClose = findViewById(R.id.wednesday_close);
        chkWednesday = findViewById(R.id.wednesday_checkBox);

        thursdayOpen = findViewById(R.id.thursday_open);
        thursdayClose = findViewById(R.id.thursday_close);
        chkThursday = findViewById(R.id.thursday_checkBox);

        fridayOpen = findViewById(R.id.friday_open);
        fridayClose = findViewById(R.id.friday_close);
        chkFriday = findViewById(R.id.friday_checkBox);

        saturdayOpen = findViewById(R.id.saturday_open);
        saturdayClose = findViewById(R.id.saturday_close);
        chkSaturday = findViewById(R.id.saturday_checkBox);

        Phone = findViewById(R.id.pub_management_phoneEditText);
        website = findViewById(R.id.pub_management_websiteEditText);
        submitInfo = findViewById(R.id.pub_management_update_btn);
        gallery = findViewById(R.id.pub_management_gallery_btn);
    }

    private void initVariables() {
        Intent intent = getIntent();
        pubName = intent.getStringExtra("pubName");
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Pubs").child(pubName);
        hashMap = new HashMap<>();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_management_update_btn:
                orderStrings();
                updateFirebase();
                break;
            case R.id.pub_management_gallery_btn:
                Intent intent = new Intent(EditPub.this , GalleryManagement.class);
                intent.putExtra("pubName" , pubName);
                startActivity(intent);
                break;
        }
    }

    private void orderStrings() {

        //יום ראשון
        if (chkSunday.isChecked()) {
            sunday = "סגור";
        } else {
            sunday = sundayOpen.getSelectedItem().toString() + " - " + sundayClose.getSelectedItem().toString();
        }
        hashMap.put("Sunday", sunday);


        //יום שני
        if (chkMonday.isChecked()) {
            monday = "סגור";
        } else {
            monday = mondayOpen.getSelectedItem().toString() + " - " + mondayClose.getSelectedItem().toString();
        }
        hashMap.put("Monday", monday);


        //יום שלישי
        if (chkTuesday.isChecked()) {
            tuesday = "סגור";
        } else {
            tuesday = tuesdayOpen.getSelectedItem().toString() + " - " + tuesdayClose.getSelectedItem().toString();
        }
        hashMap.put("Tuesday", tuesday);


        //יום רביעי
        if (chkWednesday.isChecked()) {
            wednesday = "סגור";
        } else {
            wednesday = wednesdayOpen.getSelectedItem().toString() + " - " + wednesdayClose.getSelectedItem().toString();
        }
        hashMap.put("Wednesday", wednesday);


        //יום חמישי
        if (chkThursday.isChecked()) {
            thursday = "סגור";
        } else {
            thursday = thursdayOpen.getSelectedItem().toString() + " - " + thursdayClose.getSelectedItem().toString();
        }
        hashMap.put("Thursday", thursday);


        //יום שישי
        if (chkFriday.isChecked()) {
            friday = "סגור";
        } else {
            friday = fridayOpen.getSelectedItem().toString() + " - " + fridayClose.getSelectedItem().toString();
        }
        hashMap.put("Friday", friday);


        //יום שבת
        if (chkSaturday.isChecked()) {
            saturday = "סגור";
        } else {
            saturday = saturdayOpen.getSelectedItem().toString() + " - " + saturdayClose.getSelectedItem().toString();
        }
        hashMap.put("Saturday", saturday);

        hashMap.put("Telephone", Phone.getText().toString());
        hashMap.put("Website", website.getText().toString());

    }

    private void updateFirebase() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRef.updateChildren(hashMap);

                Toast.makeText(getApplicationContext(), "המידע עודכן בהצלחה", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}