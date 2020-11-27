package com.example.ipub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    TextView home, report;
    ListView listView;
    TinyDB tinyDB;
    public ArrayList<Pub> FavoritesList;
    public ArrayList<Object> ObjectsList;
    FoldingCellListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        listView = findViewById(R.id.fav_list_view);
        home = findViewById(R.id.home_button);
        report = findViewById(R.id.report_button);
        tinyDB = new TinyDB(this);
        FavoritesList = new ArrayList<Pub>();
        ObjectsList = new ArrayList<Object>();

        ObjectsList.addAll(tinyDB.getListObject("FavoritesList", Pub.class));
        for (Object O : ObjectsList) {
            FavoritesList.add((Pub) O);
        }

        adapter = new FoldingCellListAdapter(this, FavoritesList);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavoritesActivity.this, ReportActivity.class);
                startActivity(i);
            }
        });

        setTitleCards();
        initListView();
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
            pub.setBtnRating(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FavoritesActivity.this, Ratings.class);
                    intent.putExtra("pub_name", pub.getTitleName());
                    startActivity(intent);
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
}