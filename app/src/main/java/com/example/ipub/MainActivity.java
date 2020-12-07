package com.example.ipub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Layout;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener  {

    private int LOCATION_PERMISSION_CODE = 1;
    private static final int REQUEST_CALL = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    public double user_lat;
    public double user_lon;
    public ArrayList<Pub> pub_list;
    public ArrayList<Pub> fullPubList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initVariables();
        initSpinners();
        getDataFromDB();

        //onClickEvents
        report.setOnClickListener(this);
        favorites.setOnClickListener(this);

        //Spinner Item Selected Events
        kosherSpinner.setOnItemSelectedListener(this);
        areaSpinner.setOnItemSelectedListener(this);


    }

    private void initVariables() {
        pub_list = new ArrayList<Pub>();
        tinyDB = new TinyDB(this);
        FavoritesList = new ArrayList<Pub>();
        ObjectsList = new ArrayList<Object>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fullPubList = new ArrayList<Pub>();
        adapter = new FoldingCellListAdapter(this, pub_list);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Pubs");
        ObjectsList.addAll(tinyDB.getListObject("FavoritesList", Pub.class));
        for (Object O : ObjectsList) {
            FavoritesList.add((Pub) O);
        }

    }

    private void initViews() {
        areaSpinner = findViewById(R.id.area_spinner);
        theListView = findViewById(R.id.list_view);
        report = findViewById(R.id.report_button);
        favorites = findViewById(R.id.favorites_button);
    }

    private void initSpinners() {
        kosherSpinner = findViewById(R.id.kosher_Spinner);
        ArrayAdapter<CharSequence> kosherSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.KosherArray, android.R.layout.simple_spinner_dropdown_item);
        kosherSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kosherSpinner.setAdapter(kosherSpinnerAdapter);

        ArrayAdapter<CharSequence> areaSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.areaArray, android.R.layout.simple_spinner_dropdown_item);
        areaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaSpinnerAdapter);
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
            pub.setBtnRatePub(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.rate_dialog_box);
                    TextView pubName = (TextView) dialog.findViewById(R.id.RatingDialogPubName);
                    RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.RatingDialogRatingBar);
                    EditText userName = (EditText) dialog.findViewById(R.id.RatingDialogUserName);
                    EditText comment = (EditText) dialog.findViewById(R.id.RatingDialogComment);
                    Button sendRate = (Button) dialog.findViewById(R.id.RatingDialogBTN);

                    pubName.setText(pub.getName());


                    dialog.show();
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    Window window = dialog.getWindow();
                    width = Integer.valueOf((int)(width*0.8));
                    height = Integer.valueOf((int)(height*0.75));
                    window.setLayout(width ,height);

                    sendRate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext() , pub.getName() , Toast.LENGTH_SHORT).show();
                        }
                    });

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
            pub.setBtnComments(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Ratings.class);
                    intent.putExtra("pub_name", pub.getTitleName());
                    startActivity(intent);
                }
            });
        }

    }

    // call to pub from iPub directly
    private void makePhoneCall(String tel) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + tel;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
        }
    }

    // navigate to pub's website
    private void goToWebsite(String website) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
    }

    // Waze navigation (or Google Maps)
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

    // retriving data from Firebase
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
                            snapshot.child("Area").getValue().toString(), Float.parseFloat(snapshot.child("RatingAverage").getValue().toString()));

                    pub_list.add(temp);
                    fullPubList.add(temp);

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
        adapter.setFullPubList(pub_list);
        theListView.setAdapter(adapter);
    }

    // next three methods to calculate the distance between user and pubs
    public double getKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
    }

    //    private double deg2rad(double deg) {
    //        return (deg * Math.PI / 180.0);
    //    }
    //
    //    private double rad2deg(double rad) {
    //        return (rad * 180.0 / Math.PI);
    //    }

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


    // onClick events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favorites_button:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                break;
            case R.id.report_button:
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
                break;
        }
    }

    //onItemSelected events
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.kosher_Spinner:
                if(l == 0){
                    if(pub_list.size()>0){
                        adapter.setFullPubList(fullPubList);
                        String s = "כשרות-" + areaSpinner.getSelectedItem().toString();
                        adapter.getSpinnerFilter().filter(s);
                    }

                }
                else if(l == 1){
                    adapter.setFullPubList(fullPubList);
                    String s = "כשר-" + areaSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                }
                else{
                    adapter.setFullPubList(fullPubList);
                    String s = "לא כשר-" + areaSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter( s);

                }
                break;

            case R.id.area_spinner:
                if(l == 0){
                    if(pub_list.size()>0) {
                        adapter.setFullPubList(fullPubList);
                        String s = kosherSpinner.getSelectedItem().toString() + "-איזור";
                        adapter.getSpinnerFilter().filter(s);
                    }

                }
                else if(l == 1){
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-צפון";
                    adapter.getSpinnerFilter().filter(s);
                }
                else if(l == 2){
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-השרון";
                    adapter.getSpinnerFilter().filter(s);
                }
                else if(l == 3){
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-מרכז";
                    adapter.getSpinnerFilter().filter(s);
                }
                else{
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-דרום";
                    adapter.getSpinnerFilter().filter(s);
                }

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }



}


