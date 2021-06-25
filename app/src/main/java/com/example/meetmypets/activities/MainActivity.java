package com.example.meetmypets.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.example.meetmypets.R;
import com.example.meetmypets.ViewPagerAdapter;
import com.example.meetmypets.fragments.CreateMeetingFragment;
import com.example.meetmypets.fragments.MeetingListFragment;
import com.example.meetmypets.fragments.SettingsFragment;
import com.example.meetmypets.fragments.Splash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, MeetingListFragment.ListActionCallback, OnMapReadyCallback {

    private MeetingListFragment listFragment;
    private  ViewPagerAdapter pagerAdapter;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;
    private boolean userIsLoggedIn, isMapReady;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int resultFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int resultCoarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (resultFineLocation != PackageManager.PERMISSION_GRANTED && resultCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            //todo if permission is denied
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        pagerAdapter = new ViewPagerAdapter(this);
        final SmoothBottomBar smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.INVISIBLE);
        listFragment = new MeetingListFragment();
        mapFragment = SupportMapFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new Splash(listFragment),"splash").commit();

        smoothBottomBar.setOnItemSelectedListener(position -> {
            switch (position) {
                case 0:
                    handleFragment(listFragment,"Meetings");
                    break;
                case 1:
                    handleFragment(mapFragment, "Map");
                    mapFragment.getMapAsync(this);
                    hookupLocation();
                    break;
                case 2:
                    handleFragment(new SettingsFragment(), "Settings");
                    break;
            }
            return false;
        });

    }


    void handleFragment(Fragment fragment, String fragmentName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, fragment, fragmentName);
        ft.commit();
    }
    @Override
    public void onButtonMeetingClicked(boolean isNewMeeting) {

    }

    @Override
    public void onButtonNewMeetingClicked(boolean isNewMeeting) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        isMapReady=true;
        enableMyLocation();
    }
    @SuppressLint("MissingPermission")//Permission check invoked at MainActivity
    private void enableMyLocation() {
        // [START maps_check_location_permission]

        if (map != null) {
            map.setMyLocationEnabled(true);
        }

        // [END maps_check_location_permission]
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //   showMap();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint("MissingPermission")//Permission check invoked at MainActivity
    private void hookupLocation() {
        LocationRequest locationRequest;
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(5000);
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location chosenLocation = null;
                for (Location location : locationResult.getLocations()) {
                    if (chosenLocation == null || chosenLocation.getAccuracy() > location.getAccuracy())
                        chosenLocation = location;
                }
                location = chosenLocation;
                applyCurrentLocation(chosenLocation);
            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        setLastUserLocation();

    }
    @SuppressLint("MissingPermission")//Permission check invoked at MainActivity

    private void setLastUserLocation() {

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location loc) {
                location = loc;
                // Got last known location. In some rare situations this can be null.
                applyCurrentLocation(loc);
            }
        });
    }
    public void applyCurrentLocation(Location location) {
        if (location != null && isMapReady) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

//            if (!isParent && parent != null){
//                dbRef.child(PARENTS).child(parent).child(KIDS).child(user).child("location").setValue(point);
//            }

            // createMarker(point, R.drawable.parenticon, user, 100);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        }
    }
}