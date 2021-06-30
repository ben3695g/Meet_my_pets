package com.example.meetmypets.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetmypets.adapter.MeetingsAdapter;
import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.fragments.MeetingsMapFragment;
import com.example.meetmypets.fragments.MeetingListFragment;
import com.example.meetmypets.fragments.SettingsFragment;
import com.example.meetmypets.fragments.Splash;
import com.example.meetmypets.model.MeetingsDataLayer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private MeetingListFragment listFragment;
    private MeetingsMapFragment mapFragment2;
    private SettingsFragment settingFragment;
    private SupportMapFragment mapFragment;
    private MeetingsAdapter meetingsAdapter;
    private SmoothBottomBar smoothBottomBar;
    private GoogleMap map;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private boolean userIsLoggedIn, isMapReady, isMapLoaded =false, isCameraMoved =false;
    private  List<Meeting> mapFragmentMeetings;
    private int previousCase = 0;


    interface UserLocationListener{
        void onUserLocationFound(LatLng location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        int resultFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int resultCoarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (resultFineLocation != PackageManager.PERMISSION_GRANTED && resultCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        mapFragment2 = new MeetingsMapFragment();
        meetingsAdapter = new MeetingsAdapter();
        MeetingsDataLayer dataLayer = new MeetingsDataLayer();

        dataLayer.registerForData(new MeetingsDataLayer.MeetingsDataListener() {
            @Override
            public void onMeetingDataChanged(List<Meeting> meetings) {
                runOnUiThread(() -> {
                    meetingsAdapter.refreshMeetingsList(meetings);
                    mapFragment2.refreshMeetingList(meetings);
                });
            }
        });

        hookupLocation(new UserLocationListener() {
            @Override
            public void onUserLocationFound(LatLng location) {
                ApplyLocation(location);
            }
        });

        smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.INVISIBLE);
        listFragment = new MeetingListFragment(meetingsAdapter, dataLayer);

        settingFragment = new SettingsFragment();

        mapFragment = SupportMapFragment.newInstance();

        navigateToPageFragment(new Splash(listFragment));
        initTabFragment(listFragment);

        smoothBottomBar.setOnItemSelectedListener(position -> {
            switch (position) {
                case 0:
                    switchToTabFragment(listFragment);
                    break;
                case 1:
                    isCameraMoved =false;
                    switchToTabFragment(mapFragment2);

                    if (!isMapLoaded){


                        isMapLoaded =true;
                    }
                    break;
                case 2:
                    switchToTabFragment(settingFragment);
                    break;
            }
            return false;
        });

    }

    private void ApplyLocation(LatLng location){
        List<Meeting> updatedMeetings = meetingsAdapter.updateDistance(location);
        mapFragment2.refreshMeetingList(updatedMeetings);
        mapFragment2.applyCurrentLocation(location);
    }

    @SuppressLint("MissingPermission")//Permission check invoked at MainActivity
    public void hookupLocation(UserLocationListener userLocationListener) {
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
                    if (chosenLocation == null || chosenLocation.getAccuracy() > location.getAccuracy()) {
                        chosenLocation = location;
                    }
                }
                userLocation = chosenLocation;

                if (userLocation!= null){
                    try {
                        LatLng point = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                        userLocationListener.onUserLocationFound(point);
                    }
                    catch (Exception e){
                        // ignoring exception for off screen map scenarios
                    }
                }
            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location loc) {
                userLocation = loc;
                LatLng point = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                userLocationListener.onUserLocationFound(point);
            }
        });
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
                    // permission denied
                    Toast.makeText(this, "Location access is denied - some functionality will not be available",
                            Toast.LENGTH_LONG).show();
                    ApplyLocation(new LatLng(32.09040223978312, 34.782786585677016));
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void switchToTabFragment(Fragment targetFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int futureCase = smoothBottomBar.getItemActiveIndex();
        if(previousCase>futureCase) {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }else{
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        previousCase =futureCase;
        ft.replace(R.id.flFragment, targetFragment, targetFragment.getClass().getName());;
        ft.commit();
    }

    void initTabFragment(Fragment targetFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, targetFragment, targetFragment.getClass().getName());;
        ft.commit();
    }

     public void navigateToTabFragment(Fragment fragmentToRemove) {
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         ft.remove(fragmentToRemove);
         ft.commit();
         runOnUiThread(() -> {
             smoothBottomBar.setVisibility(View.VISIBLE);
             smoothBottomBar.setItemActiveIndex(0);
         });
     }

     public void navigateToPageFragment(Fragment fragmentToNavigate) {
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         ft.replace(R.id.mainLayout, fragmentToNavigate);
         ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
         ft.addToBackStack(fragmentToNavigate.getClass().getName());
         ft.commit();
     }
}