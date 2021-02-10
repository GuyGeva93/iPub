package com.example.ipub;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

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
    DatabaseReference ratingRef;
    FirebaseDatabase database;
    TextView report, favorites;
    Spinner kosherSpinner;
    Spinner areaSpinner;
    Spinner starSpinner;
    TinyDB tinyDB;
    long millisecond;
    boolean flag;
    EditText dialogComment;
    EditText dialogUserName;
    RatingBar dialogRatingBar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mToggle;
    NavigationView navigationView;
    FirebaseAuth mAuth;



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
        starSpinner.setOnItemSelectedListener(this);

        navigationView.setNavigationItemSelectedListener(this);
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
        ratingRef = database.getReference().child("Pubs");
        ObjectsList.addAll(tinyDB.getListObject("FavoritesList", Pub.class));

        for (Object O : ObjectsList) {
            FavoritesList.add((Pub) O);
        }

        navigationView.bringToFront();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        kosherSpinner = findViewById(R.id.kosher_Spinner);
        areaSpinner = findViewById(R.id.area_spinner);
        starSpinner = findViewById(R.id.star_spinner);
        theListView = findViewById(R.id.list_view);
        report = findViewById(R.id.report_button);
        favorites = findViewById(R.id.favorites_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mToggle = new ActionBarDrawerToggle(this , drawerLayout , R.string.open ,R.string.close );
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Initialize spinners for sorting the pubs by kosher, area and rating
    private void initSpinners() {
        ArrayAdapter<CharSequence> kosherSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.KosherArray, android.R.layout.simple_spinner_dropdown_item);
        kosherSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kosherSpinner.setAdapter(kosherSpinnerAdapter);

        ArrayAdapter<CharSequence> areaSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.areaArray, android.R.layout.simple_spinner_dropdown_item);
        areaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaSpinnerAdapter);

        ArrayAdapter<CharSequence> starSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.StarsArray, android.R.layout.simple_spinner_dropdown_item);
        starSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        starSpinner.setAdapter(starSpinnerAdapter);

    }

    // Initialize main view
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

            // Dialog window to rate a pub
            pub.setBtnRatePub(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.rate_dialog_box);

                    TextView pubName = (TextView) dialog.findViewById(R.id.RatingDialogPubName);
                    dialogRatingBar = (RatingBar) dialog.findViewById(R.id.RatingDialogRatingBar);
                    dialogUserName = (EditText) dialog.findViewById(R.id.RatingDialogUserName);
                    dialogComment = (EditText) dialog.findViewById(R.id.RatingDialogComment);
                    Button sendRate = (Button) dialog.findViewById(R.id.RatingDialogBTN);

                    pubName.setText(pub.getName());

                    dialog.show();
                    Display display = getWindowManager().getDefaultDisplay();
                    // Setting the dialog window size
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    Window window = dialog.getWindow();
                    width = Integer.valueOf((int) (width * 0.8));
                    height = Integer.valueOf((int) (height * 0.75));
                    window.setLayout(width, height);

                    // When clicking rate, we are taking the timestamp for future comment delete by the user who created the comment.
                    // Also taking all the information such as name, rate and full comment
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

                        }
                    });


                }
            });

        }

        // When "add to favirites" clicked, checking if the pub is already exist in user's favorites list.
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

                    // If it's doesn't exist, adding to favorites.
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

        // set on click listener to start comments activity
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

        // Setting on click listener to start gallery activity.
        for (final Pub pub : pub_list) {
            pub.setBtnGallery(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
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
                pub_list.clear();
                fullPubList.clear();
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

    // calculating the distance between user and pubs
    public double getKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1));
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
                Toast.makeText(this, "אנא הפעל את מיקום המכשיר", Toast.LENGTH_LONG).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        switch (adapterView.getId()) {
            case R.id.kosher_Spinner:
                if (l == 0) {
                    adapter.setFullPubList(fullPubList);
                    String s = "כשרות-" + areaSpinner.getSelectedItem().toString() + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                } else if (l == 1) {
                    adapter.setFullPubList(fullPubList);
                    String s = "כשר-" + areaSpinner.getSelectedItem().toString() + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                } else {
                    adapter.setFullPubList(fullPubList);
                    String s = "לא כשר-" + areaSpinner.getSelectedItem().toString() + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                }
                break;

            case R.id.area_spinner:
                if (l == 0) {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-איזור" + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                } else if (l == 1) {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-צפון" + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                } else if (l == 2) {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-השרון" + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                } else if (l == 3) {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-מרכז" + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);

                } else {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-דרום" + "+" + starSpinner.getSelectedItem().toString();
                    adapter.getSpinnerFilter().filter(s);
                }
                break;

            case R.id.star_spinner:
                if (l == 0) {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-" + areaSpinner.getSelectedItem().toString() + "+דירוג";
                    adapter.getSpinnerFilter().filter(s);
                } else if (l == 1) {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-" + areaSpinner.getSelectedItem().toString() + "+3";
                    adapter.getSpinnerFilter().filter(s);
                } else if (l == 2) {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-" + areaSpinner.getSelectedItem().toString() + "+4";
                    adapter.getSpinnerFilter().filter(s);
                } else {
                    adapter.setFullPubList(fullPubList);
                    String s = kosherSpinner.getSelectedItem().toString() + "-" + areaSpinner.getSelectedItem().toString() + "+5";
                    adapter.getSpinnerFilter().filter(s);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        // If manager is logged in, show update pub and signout items.
        if(mAuth.getCurrentUser() != null){
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_update_pub);
            menuItem.setVisible(true);
            MenuItem menuItem1 = navigationView.getMenu().findItem(R.id.nav_signout);
            menuItem1.setVisible(true);
            MenuItem menuItem2 = navigationView.getMenu().findItem(R.id.nav_admin_login);
            menuItem2.setVisible(false);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_admin_login:
                startActivity(new Intent(MainActivity.this , AdminLogin.class));
                break;
            case R.id.nav_signout:
                mAuth.signOut();
                MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_update_pub);
                menuItem.setVisible(false);
                MenuItem menuItem1 = navigationView.getMenu().findItem(R.id.nav_signout);
                menuItem1.setVisible(false);
                MenuItem menuItem2 = navigationView.getMenu().findItem(R.id.nav_admin_login);
                menuItem2.setVisible(true);
                break;
            case R.id.nav_update_pub:
                startActivity(new Intent(MainActivity.this , ManagementPubsList.class));
                break;
            case R.id.nav_ask_admin:
                startActivity(new Intent(MainActivity.this, AdminRequest.class));
                break;
            case R.id.nav_report_new_pub:
                startActivity(new Intent(MainActivity.this, AddPubRequest.class));
                break;
        }

        return true;
    }
}


