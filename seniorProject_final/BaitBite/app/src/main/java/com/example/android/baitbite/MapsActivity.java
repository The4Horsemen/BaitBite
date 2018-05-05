package com.example.android.baitbite;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.baitbite.Common.Common;
import com.example.android.baitbite.Database.Database;
import com.example.android.baitbite.Model.Chef;
import com.example.android.baitbite.Service.ListenOrder;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    //Firebase database obj
    private DatabaseReference refDatabase;
    protected PermissionManager permissionnManager;
    //Google maps related objects
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClientObj;
    private LocationRequest mLocationRequest;
    private LocationManager manager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    //Current Location
    private Double x;
    private Double y;

    TextView textView_customerName;

    boolean IsLoaded = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionnManager = new PermissionManager() {
        };
        if(permissionnManager.checkAndRequestPermissions(MapsActivity.this)) {



            // we check if the google service is installed on the device
            if (googleServicesAvailable()) {
                setContentView(R.layout.activity_maps);

                Toolbar toolbar = (Toolbar) findViewById(R.id.mapToolbar);
                //setSupportActionBar(toolbar);
                toolbar.setTitle("Map");

                //TODO: Remove the comments to enable "Special Order" Button
                //NOTE: Don't forget to remove the comments from the XML: app_bar_map.xml

//                FButton fab = (FButton) findViewById(R.id.buttonSpecialOrder);
//                fab.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(MapsActivity.this, "TODO: Special order", Toast.LENGTH_SHORT).show();
//                        Intent homeIntent = new Intent(MapsActivity.this, HomeActivity.class);
//                        startActivity(homeIntent);
//                    }
//                });

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_mapLayout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_mapView);
                navigationView.setNavigationItemSelectedListener(this);

                //Set the customer name
                View headerView = navigationView.getHeaderView(0);
                textView_customerName = (TextView) headerView.findViewById(R.id.textView_mapCustomerName);
                textView_customerName.setText(Common.currentCustomer.getName());

            } else {
                // no support
            }

            // we check if the GPS is enabled in the device
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //buildAlertMessageNoGps();
                displayLocationSettingsRequest(this);// For prompting user to enable the GPS
            }
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            // initialize the map
            initMap();
        }

        //Register Service
        Intent service = new Intent(MapsActivity.this, ListenOrder.class);
        startService(service);

    }
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    // Checking the google service availability
    public boolean googleServicesAvailable() {

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();

        } else {
            Toast.makeText(this, "Cant Connect to play services", Toast.LENGTH_LONG).show();
        }

        return false;

    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void initMap() {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true); // this for showing the locate me icon on the map ( top right corner)
        Start();
        /*  if we need to do something when the marker is clicked
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Chef tmp = (Chef) marker.getTag();


               //Pass the chef object or the phone number or etc

                Log.d("clicked","am clicked");
                Toast.makeText(MapsActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            //TODO: chefdishlistActivity


                return false;
            }
        });
        */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){

            @Override
            public void onInfoWindowClick(Marker marker) {
                Chef tempChef = (Chef) marker.getTag();

                //Get ChefID & send it to DishList Activity
                Intent chefDishList = new Intent(MapsActivity.this, ChefDishListActivity.class);
                //Get the key of ChefID
                chefDishList.putExtra("chefID", tempChef.getPhone_Number());
                startActivity(chefDishList);
            }
        });

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(26.31330705, 50.14426388);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    // Operations
    private void addStoresToMap(){
        Log.d("TAG","X  "+x + "Y  "+y);
        GeoFire geoFire;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("StoreLocation");
        //final DatabaseReference Chefref = FirebaseDatabase.getInstance().getReference("Chef");
        geoFire = new GeoFire(ref);

        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(x,  y), 7);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                FirebaseDatabase.getInstance().getReference("Chef").child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Chef user = dataSnapshot.getValue(Chef.class);
                                if(user.getAvailability() > 0){
                                if (!user.getProfile_Image().isEmpty()) {
                                    Picasso.with(MapsActivity.this)
                                            .load(user.getProfile_Image()).fetch();
                                }

                                else{
                                    Picasso.with(MapsActivity.this)
                                            .load("https://png.icons8.com/ios/50/000000/baguette.png").fetch();
                                }
                                SetMarker(user);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
                geoQuery.removeAllListeners();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

        /*
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(x,  y), 7);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                FirebaseDatabase.getInstance().getReference("Chef").child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Chef user = dataSnapshot.getValue(Chef.class);
                                //if(user.getAvailability() > 0){
                                if (!user.getProfile_Image().isEmpty()) {
                                    Picasso.with(MapsActivity.this)
                                            .load(user.getProfile_Image()).fetch();
                                }else{
                                    Picasso.with(MapsActivity.this)
                                            .load("https://png.icons8.com/ios/50/000000/baguette.png").fetch();
                                }
                                SetMarker(user);

                                //}

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
                geoQuery.removeAllListeners();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
         */



    }

    private void SetMarker(Chef user){
        System.out.println(user.getName());
        LatLng newLocation = new LatLng(user.getLocationX(),user.getLocationY());
        Marker M = mMap.addMarker(new MarkerOptions()
                .position(newLocation)
        );
        M.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_2));
        M.setTag(user);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                System.out.println("Marker is set" );
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                final AtomicBoolean loaded = new AtomicBoolean();
                    TextView Name = (TextView) v.findViewById((R.id.tv_locality));
                    TextView phone = (TextView) v.findViewById((R.id.tv_locality2));
                    ImageView img = (ImageView) v.findViewById(R.id.imageView1);
                    Chef Tmp = (Chef) marker.getTag();
                    Name.setText(Tmp.getName());
                    phone.setText(Tmp.getStore_Summary());
                    if (!Tmp.getProfile_Image().isEmpty()) {


                        //Picasso.with(MapsActivity.this).load(Tmp.getProfile_Image()).into(img);
                        Picasso.with(MapsActivity.this)
                                .load(Tmp.getProfile_Image())
                                .into(img,new Callback.EmptyCallback() {
                            @Override public void onSuccess() {
                                loaded.set(true);
                            }
                        });
                    } else {
                        Picasso.with(MapsActivity.this).load("https://png.icons8.com/ios/50/000000/baguette.png").into(img,new Callback.EmptyCallback() {
                            @Override public void onSuccess() {
                                loaded.set(true);
                            }
                        });

                    }
                if (loaded.get()) {
                    return v;
                }
                    return null;
                }



        });




    }

    // Operations
    private void goToLocation(double lat, double lng) {
        LatLng CurrentLocation = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(CurrentLocation);
        mMap.moveCamera(update);

    }

    // Operations
    private void goToLocationZoom(double lat, double lng, float zoom) {
        x = lat;
        y = lng;
        LatLng CurrentLocation = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CurrentLocation, zoom);
        mMap.moveCamera(update);

    }

    public void Start() {
        mLocationRequest = LocationRequest.create();


        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationCallback = new LocationCallback(){
            //getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                goToLocationZoom(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude(), 15);

                onLocationChanged(locationResult.getLastLocation());
            }
        };


    }

    public void onLocationChanged(Location location) {
        x = location.getLatitude();
        y = location.getLongitude();
        addStoresToMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClientObj != null && mFusedLocationClient != null) {
            requestLocationUpdates();
        } else {
            buildGoogleApiClient();
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClientObj = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClientObj.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }

    public void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }





    // Empty overridden methods
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
//            MapFragment mapFragment = new MapFragment();
//            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace();

        } else if (id == R.id.nav_cart) {
            Toast.makeText(MapsActivity.this, "Your cart is empty !", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_orders) {
            Intent orderStatusIntent = new Intent(MapsActivity.this, OrderStatusActivity.class);
            startActivity(orderStatusIntent);

        } else if (id == R.id.nav_sign_out) {
            //Delete Remembered Customer
            Paper.book().destroy();

            //Delete cart
            new Database(getBaseContext()).cleanCart();

            //Signout
            Intent signInIntent = new Intent(MapsActivity.this, SignInActivity.class);
            signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signInIntent);

        } else if (id == R.id.nav_profile) {
            Intent profileIntent = new Intent(MapsActivity.this, CustomerProfileActivity.class);
            startActivity(profileIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_mapLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A simple {@link Fragment} subclass.
     */
    public static class MapFragment extends Fragment implements OnMapReadyCallback {

        GoogleMap googleMap;

        public MapFragment() {
            // Required empty public constructor
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {

        }
    }
}

