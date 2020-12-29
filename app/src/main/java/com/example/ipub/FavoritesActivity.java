package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity implements View.OnClickListener {

    TextView home, report;
    ListView listView;
    TinyDB tinyDB;
    public ArrayList<Pub> FavoritesList;
    public ArrayList<Object> ObjectsList;
    FoldingCellListAdapter adapter;
    boolean flag;
    long millisecond;
    DatabaseReference ratingRef;
    FirebaseDatabase database;
    RatingBar dialogRatingBar;
    EditText dialogUserName;
    EditText dialogComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initVariables();
        initViews();
        home.setOnClickListener(this);
        report.setOnClickListener(this);
        setTitleCards();
        initListView();
    }

    private void initViews() {
        listView = findViewById(R.id.fav_list_view);
        home = findViewById(R.id.home_button);
        report = findViewById(R.id.report_button);
    }

    private void initVariables() {
        tinyDB = new TinyDB(this);
        FavoritesList = new ArrayList<Pub>();
        ObjectsList = new ArrayList<Object>();
        database = FirebaseDatabase.getInstance();
        ratingRef = database.getReference().child("Pubs");
        ObjectsList.addAll(tinyDB.getListObject("FavoritesList", Pub.class));
        for (Object O : ObjectsList) {
            FavoritesList.add((Pub) O);
        }
        adapter = new FoldingCellListAdapter(this, FavoritesList);

    }

    public void setTitleCards() {
        FavoritesList.sort(new DistanceComparator());
        // get our cards list view
        //final FoldingCellListAdapter adapter = new FoldingCellListAdapter(this, pub_list);
        // set elements to adapter
        adapter.setFullPubList(FavoritesList);
        listView.setAdapter(adapter);

    }

    private void initListView() {

        for (final Pub pub : FavoritesList) {
            pub.setRequestBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makePhoneCall(pub.getTelephone());
                }
            });
        }

        for (final Pub pub : FavoritesList) {
            pub.setBtnGoToWebsite(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebsite(pub.getWebsite());
                }
            });
        }

        for (final Pub pub : FavoritesList) {
            pub.setBtnNavigateTopub(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToPub(pub.getLat(), pub.getLon());
                }
            });

        }

        for (final Pub pub : FavoritesList) {
            pub.setBtnAddToFavorites(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavoritesList.remove(pub);
                    ArrayList<Object> tempObjList = new ArrayList<Object>();
                    for (Pub p : FavoritesList) {
                        tempObjList.add((Object) p);
                    }
                    tinyDB.putListObject("FavoritesList", tempObjList);
                    Toast.makeText(getApplicationContext(), "הפאב הוסר ממועדפים", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();

                }
            });

        }

        for (final Pub pub : FavoritesList) {
            pub.setBtnComments(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FavoritesActivity.this, Ratings.class);
                    intent.putExtra("pub_name", pub.getTitleName());
                    startActivity(intent);
                }
            });
        }

        for (final Pub pub : FavoritesList) {
            pub.setBtnGallery(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FavoritesActivity.this, GalleryActivity.class);
                    intent.putExtra("pub_name", pub.getTitleName());
                    startActivity(intent);
                }
            });
        }

        for (final Pub pub : FavoritesList) {

            pub.setBtnRatePub(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(FavoritesActivity.this);
                    dialog.setContentView(R.layout.rate_dialog_box);

                    TextView pubName = (TextView) dialog.findViewById(R.id.RatingDialogPubName);
                    dialogRatingBar = (RatingBar) dialog.findViewById(R.id.RatingDialogRatingBar);
                    dialogUserName = (EditText) dialog.findViewById(R.id.RatingDialogUserName);
                    dialogComment = (EditText) dialog.findViewById(R.id.RatingDialogComment);
                    Button sendRate = (Button) dialog.findViewById(R.id.RatingDialogBTN);

                    pubName.setText(pub.getName());


                    dialog.show();
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    Window window = dialog.getWindow();
                    width = Integer.valueOf((int) (width * 0.8));
                    height = Integer.valueOf((int) (height * 0.75));
                    window.setLayout(width, height);

                    sendRate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            millisecond = System.currentTimeMillis();
                            ratingRef = database.getReference().child("Pubs").child(pub.getTitleName()).child("Ratings");
                            ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    flag = false;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        long timeStamp = tinyDB.getLong(pub.getTitleName(), 0);
                                        if (String.valueOf(timeStamp).equals(snapshot.getKey())) {
                                            flag = true;
                                        }

                                    }


                                    if (flag == true) {
                                        Toast.makeText(getApplicationContext(), "כבר דירגת את הפאב הנוכחי, לא ניתן לדרג פאב פעמיים", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (dialogRatingBar.getRating() == 0) {
                                            Toast.makeText(getApplicationContext(), "אנא דרג את הפאב", Toast.LENGTH_LONG).show();
                                        } else if (dialogUserName.getText().toString().equals("") || dialogComment.getText().toString().equals("")) {
                                            Toast.makeText(getApplicationContext(), "אנא מלא את כל השדות", Toast.LENGTH_LONG).show();
                                        } else {
                                            ratingRef = database.getReference().child("Pubs").child(pub.getTitleName()).child("Ratings").child(String.valueOf(millisecond));
                                            CommentInfo temp = new CommentInfo(dialogUserName.getText().toString(), dialogComment.getText().toString(), dialogRatingBar.getRating(), millisecond);
                                            ratingRef.setValue(temp);
                                            tinyDB.putLong(pub.getTitleName(), millisecond);

                                            ratingRef = database.getReference().child("Pubs").child(pub.getTitleName()).child("Ratings");
                                            ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    int average = 0;
                                                    int count = 0;
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        average += Integer.parseInt(snapshot.child("rating").getValue().toString());
                                                        count++;
                                                    }
                                                    average /= count;
                                                    ratingRef = database.getReference().child("Pubs").child(pub.getTitleName()).child("RatingAverage");
                                                    ratingRef.setValue(average);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            Toast.makeText(getApplicationContext(), "הביקורת נשלחה בהצלחה!", Toast.LENGTH_LONG).show();
                                            dialog.cancel();

                                        }

                                    }
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });


//************************************************************************************************************************

                        }
                    });


                }
            });

        }

        // set on click event listener to list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });

    }

    private void makePhoneCall(String tel) {
        if (ContextCompat.checkSelfPermission(FavoritesActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FavoritesActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            String dial = "tel:" + tel;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
        }
    }

    private void goToWebsite(String website) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
    }

    private void navigateToPub(double lat, double lon) {
        String uri = "waze://?ll=" + lat + ", " + lon + "&navigate=yes";
        // try to navigate with Waze
        try {
            startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            // if Waze does not install, navigate with Google Maps
        } catch (Exception e) {
            Toast.makeText(this, "אפליקציית וויז אינה מותקנת ולכן הניווט יתבצע באמצעות 'מפות'", Toast.LENGTH_LONG).show();
            Uri navigation = Uri.parse("google.navigation:q=" + lat + "," + lon + "");
            Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
            navigationIntent.setPackage("com.google.android.apps.maps");
            startActivity(navigationIntent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_button:
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                break;
            case R.id.report_button:
                startActivity(new Intent(FavoritesActivity.this, ReportActivity.class));
                break;
        }
    }
}