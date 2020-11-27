package com.example.ipub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MainActivity extends AppCompatActivity {

    private int LOCATION_PERMISSION_CODE = 1;
    private static final int REQUEST_CALL = 1;
    // initializing FusedLocationProviderClient object
    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    public double user_lat;
    public double user_lon;
    public ArrayList<Pub> pub_list;
    public ArrayList<Pub> fullPubList;
    public ArrayList<Pub> sortKosherPubList;
    public ArrayList<Pub> sortNotKosherPubList;
    public ArrayList<Pub> sortCenterPubList;
    public ArrayList<Pub> FavoritesList;
    ArrayList<Object> ObjectsList;
    FoldingCellListAdapter adapter;
    ListView theListView;
    DatabaseReference myRef;
    FirebaseDatabase database;
    TextView report, favorites;
    Spinner kosherSpinner;
    Spinner areaSpinner;
    TinyDB tinyDB;
    boolean kosher_flag;
    boolean not_kosher_flag;
    boolean north_flag;
    boolean hasharon_flag;
    boolean center_flag;
    boolean south_flag;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tinyDB = new TinyDB(this);

        FavoritesList = new ArrayList<Pub>();
        ObjectsList = new ArrayList<Object>();

        ObjectsList.addAll(tinyDB.getListObject("FavoritesList", Pub.class));

        for (Object O : ObjectsList) {
            FavoritesList.add((Pub) O);
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        pub_list = new ArrayList<Pub>();
        fullPubList = new ArrayList<Pub>();
        sortKosherPubList = new ArrayList<Pub>();
        sortNotKosherPubList = new ArrayList<Pub>();
        sortCenterPubList = new ArrayList<Pub>();
        adapter = new FoldingCellListAdapter(this, pub_list);
        theListView = findViewById(R.id.list_view);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Pubs");
        report = findViewById(R.id.report_button);
        favorites = findViewById(R.id.favorites_button);
        kosher_flag = not_kosher_flag = north_flag = hasharon_flag = center_flag = south_flag = false;


        kosherSpinner = findViewById(R.id.kosher_Spinner);
        ArrayAdapter<CharSequence> kosherSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.KosherArray, android.R.layout.simple_spinner_dropdown_item);
        kosherSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kosherSpinner.setAdapter(kosherSpinnerAdapter);

        areaSpinner = findViewById(R.id.area_spinner);
        ArrayAdapter<CharSequence> areaSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.areaArray, android.R.layout.simple_spinner_dropdown_item);
        areaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaSpinnerAdapter);

        getDataFromDB();

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(i);
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(i);
            }
        });

        kosherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id == 0) {
                    kosher_flag = not_kosher_flag = false;
                    if ((!north_flag) && (!south_flag) && (!hasharon_flag) && (!center_flag)) {
                        adapter = new FoldingCellListAdapter(MainActivity.this, pub_list);
                        theListView.setAdapter(adapter);
                    } else if (north_flag) {
                        ArrayList<Pub> north_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("North")) {
                                north_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, north_list);
                        theListView.setAdapter(adapter);
                    } else if (hasharon_flag) {
                        ArrayList<Pub> hasharon_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("Hasharon")) {
                                hasharon_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, hasharon_list);
                        theListView.setAdapter(adapter);
                    } else if (center_flag) {
                        ArrayList<Pub> center_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("Center")) {
                                center_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, center_list);
                        theListView.setAdapter(adapter);
                    } else {
                        ArrayList<Pub> south_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("South")) {
                                south_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, south_list);
                        theListView.setAdapter(adapter);
                    }

                } else if (id == 1) {
                    kosher_flag = true;
                    not_kosher_flag = false;
                    if ((!north_flag) && (!south_flag) && (!hasharon_flag) && (!center_flag)) {
                        adapter = new FoldingCellListAdapter(MainActivity.this, sortKosherPubList);
                        theListView.setAdapter(adapter);
                    } else if (north_flag) {
                        ArrayList<Pub> kosherNorthList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("North")) {
                                kosherNorthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherNorthList);
                        theListView.setAdapter(adapter);
                    } else if (hasharon_flag) {
                        ArrayList<Pub> kosherHasharonList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("Hasharon")) {
                                kosherHasharonList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherHasharonList);
                        theListView.setAdapter(adapter);
                    } else if (center_flag) {
                        ArrayList<Pub> kosherCenterList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("Center")) {
                                kosherCenterList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherCenterList);
                        theListView.setAdapter(adapter);
                    } else {
                        ArrayList<Pub> kosherSouthList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("South")) {
                                kosherSouthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherSouthList);
                        theListView.setAdapter(adapter);
                    }
                } else {
                    not_kosher_flag = true;
                    kosher_flag = false;
                    if ((!north_flag) && (!south_flag) && (!hasharon_flag) && (!center_flag)) {
                        adapter = new FoldingCellListAdapter(MainActivity.this, sortNotKosherPubList);
                        theListView.setAdapter(adapter);
                    } else if (north_flag) {
                        ArrayList<Pub> notKosherNorthList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("North")) {
                                notKosherNorthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherNorthList);
                        theListView.setAdapter(adapter);
                    } else if (hasharon_flag) {
                        ArrayList<Pub> notKosherHasharonList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("Hasharon")) {
                                notKosherHasharonList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherHasharonList);
                        theListView.setAdapter(adapter);
                    } else if (center_flag) {
                        ArrayList<Pub> notKosherCenterList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("Center")) {
                                notKosherCenterList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherCenterList);
                        theListView.setAdapter(adapter);
                    } else {
                        kosher_flag = false;
                        not_kosher_flag = true;
                        ArrayList<Pub> notKosherSouthList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("South")) {
                                notKosherSouthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherSouthList);
                        theListView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long id2) {
                if (id2 == 0) {
                    north_flag = hasharon_flag = center_flag = south_flag = false;
                    if ((!kosher_flag) && (!not_kosher_flag)) {
                        adapter = new FoldingCellListAdapter(MainActivity.this, pub_list);
                        theListView.setAdapter(adapter);
                    } else if ((kosher_flag)) {
                        adapter = new FoldingCellListAdapter(MainActivity.this, sortKosherPubList);
                        theListView.setAdapter(adapter);
                    } else {
                        adapter = new FoldingCellListAdapter(MainActivity.this, sortNotKosherPubList);
                        theListView.setAdapter(adapter);
                    }

                } else if (id2 == 1) {
                    north_flag = true;
                    hasharon_flag = center_flag = south_flag = false;
                    if ((!kosher_flag) && (!not_kosher_flag)) {
                        ArrayList<Pub> north_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("North")) {
                                north_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, north_list);
                        theListView.setAdapter(adapter);
                    } else if ((kosher_flag)) {
                        ArrayList<Pub> kosherNorthList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("North")) {
                                kosherNorthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherNorthList);
                        theListView.setAdapter(adapter);
                    } else {
                        ArrayList<Pub> notKosherNorthList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("North")) {
                                notKosherNorthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherNorthList);
                        theListView.setAdapter(adapter);
                    }

                } else if (id2 == 2) {
                    hasharon_flag = true;
                    north_flag = center_flag = south_flag = false;
                    if ((!kosher_flag) && (!not_kosher_flag)) {
                        ArrayList<Pub> hasharon_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("Hasharon")) {
                                hasharon_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, hasharon_list);
                        theListView.setAdapter(adapter);
                    } else if ((kosher_flag)) {
                        ArrayList<Pub> kosherHasharonList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("Hasharon")) {
                                kosherHasharonList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherHasharonList);
                        theListView.setAdapter(adapter);
                    } else {
                        ArrayList<Pub> notKosherHasharonList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("Hasharon")) {
                                notKosherHasharonList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherHasharonList);
                        theListView.setAdapter(adapter);
                    }
                } else if (id2 == 3) {
                    center_flag = true;
                    north_flag = hasharon_flag = south_flag = false;
                    if ((!kosher_flag) && (!not_kosher_flag)) {
                        ArrayList<Pub> center_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("Center")) {
                                center_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, center_list);
                        theListView.setAdapter(adapter);
                    } else if (kosher_flag) {
                        ArrayList<Pub> kosherCenterList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("Center")) {
                                kosherCenterList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherCenterList);
                        theListView.setAdapter(adapter);
                    } else {
                        ArrayList<Pub> notKosherCenterList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("Center")) {
                                notKosherCenterList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherCenterList);
                        theListView.setAdapter(adapter);
                    }

                } else {
                    south_flag = true;
                    north_flag = hasharon_flag = center_flag = false;
                    if ((!kosher_flag) && (!not_kosher_flag)) {
                        ArrayList<Pub> south_list = new ArrayList<Pub>();
                        for (Pub p : pub_list) {
                            if (p.getArea().equals("South")) {
                                south_list.add(p);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, south_list);
                        theListView.setAdapter(adapter);
                    } else if (kosher_flag) {
                        ArrayList<Pub> kosherSouthList = new ArrayList<Pub>();
                        for (Pub pub : sortKosherPubList) {
                            if (pub.getArea().equals("South")) {
                                kosherSouthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, kosherSouthList);
                        theListView.setAdapter(adapter);
                    } else {
                        ArrayList<Pub> notKosherSouthList = new ArrayList<Pub>();
                        for (Pub pub : sortNotKosherPubList) {
                            if (pub.getArea().equals("South")) {
                                notKosherSouthList.add(pub);
                            }
                        }
                        adapter = new FoldingCellListAdapter(MainActivity.this, notKosherSouthList);
                        theListView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void initListView() {

        // method to get the location
        getLastLocation();

        for (final Pub pub : pub_list) {
            pub.setRequestBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makePhoneCall(pub.getTelephone());
                }
            });
        }

        for (final Pub pub : pub_list) {
            pub.setBtnGoToWebsite(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebsite(pub.getWebsite());
                }
            });
        }

        for (final Pub pub : pub_list) {
            pub.setBtnNavigateTopub(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToPub(pub.getLat(), pub.getLon());
                }
            });

        }

        for (final Pub pub : pub_list) {
            pub.setBtnAddToFavorites(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int flag = 0;
                    for (Pub FavPub : FavoritesList) {
                        if (pub.getName().equals(FavPub.getName())) {
                            flag = 1;
                            break;
                        }
                    }

                    if (flag == 0) {
                        FavoritesList.add(pub);
                        ArrayList<Object> TempObjList = new ArrayList<Object>();
                        for (Pub FavPub : FavoritesList) {
                            TempObjList.add((Object) FavPub);
                        }
                        tinyDB.putListObject("FavoritesList", TempObjList);
                        Toast.makeText(getApplicationContext(), "הפאב נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "הפאב כבר קיים במועדפים", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

        // set on click event listener to list view
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });

        for (final Pub pub : pub_list) {
            pub.setBtnRating(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Ratings.class);
                    intent.putExtra("pub_name", pub.getTitleName());
                    startActivity(intent);
                }
            });
        }

    }


    private void makePhoneCall(String tel) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
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

    private void getDataFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // reading from FireBase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pub temp = new Pub(snapshot.child("Name").getValue().toString(), snapshot.child("TitleName").getValue().toString(),
                            snapshot.child("Address").getValue().toString(),
                            Double.parseDouble(snapshot.child("Longitude").getValue().toString()),
                            Double.parseDouble(snapshot.child("Latitude").getValue().toString()),
                            snapshot.child("Kosher").getValue().toString(), snapshot.child("Telephone").getValue().toString(),
                            snapshot.child("Website").getValue().toString(), snapshot.child("Sunday").getValue().toString(),
                            snapshot.child("Monday").getValue().toString(), snapshot.child("Tuesday").getValue().toString(),
                            snapshot.child("Wednesday").getValue().toString(), snapshot.child("Thursday").getValue().toString(),
                            snapshot.child("Friday").getValue().toString(), snapshot.child("Saturday").getValue().toString(),
                            snapshot.child("Area").getValue().toString());
                    pub_list.add(temp);
                    fullPubList.add(temp);
                    if (temp.getKosher().equals("כשר"))
                        sortKosherPubList.add(temp);
                    if (temp.getKosher().equals("לא כשר"))
                        sortNotKosherPubList.add(temp);

                }
                if (pub_list.size() != 0) {
                    initListView();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to read from DB", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setTitleCards() {
        pub_list.sort(new DistanceComparator());
        sortKosherPubList.sort(new DistanceComparator());
        sortNotKosherPubList.sort(new DistanceComparator());

        // get our cards list view
        //final FoldingCellListAdapter adapter = new FoldingCellListAdapter(this, pub_list);
        // set elements to adapter
        adapter.setFullPubList(pub_list);
        theListView.setAdapter(adapter);
    }

    public double getKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last location from FusedLocationClient object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    user_lat = location.getLatitude(); // get user current lat
                                    user_lon = location.getLongitude(); // get user current lon
                                    for (Pub pub : pub_list) {
                                        pub.setDistance(getKilometers(pub.getLat(), pub.getLon(), user_lat, user_lon));
                                    }
                                    // setting the cards display after receiving DataBase information
                                    setTitleCards();
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            user_lat = mLastLocation.getLatitude();
            user_lon = mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        // If we want background location
        // on Android 10.0 and higher:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ipub_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setFullPubList(fullPubList);
                adapter.getFilter().filter(newText);
                pub_list.sort(new DistanceComparator());
                fullPubList.sort(new DistanceComparator());
                return true;
            }
        });
        return true;
    }


}


