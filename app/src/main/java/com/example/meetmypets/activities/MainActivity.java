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
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetmypets.adapter.MeetingsAdapter;
import com.example.meetmypets.fragments.MeetingsFragment;
import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.fragments.MeetingsMapFragment;
import com.example.meetmypets.fragments.MeetingListFragment;
import com.example.meetmypets.fragments.SettingsFragment;
import com.example.meetmypets.fragments.Splash;
import com.example.meetmypets.model.MeetingToDelete;
import com.example.meetmypets.model.MeetingsDataLayer;
import com.example.meetmypets.model.Message;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.ArrayList;
import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,GoogleMap.OnInfoWindowClickListener,  OnMapReadyCallback {

    private MeetingListFragment listFragment;
    private MeetingsMapFragment mapFragment2;
    private SettingsFragment settingFragment;
    private SupportMapFragment mapFragment;
    private SmoothBottomBar smoothBottomBar;
    private GoogleMap map;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private boolean userIsLoggedIn, isMapReady, isMapLoaded =false, isCameraMoved =false;
    private  List<Meeting> mapFragmentMeetings;
    private int previousCase = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        int resultFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int resultCoarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (resultFineLocation != PackageManager.PERMISSION_GRANTED && resultCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            //todo if permission is denied
            //Dexter d;
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        mapFragment2 = new MeetingsMapFragment();
        MeetingsAdapter meetingsAdapter = new MeetingsAdapter();
        MeetingsDataLayer dataLayer = new MeetingsDataLayer();

        dataLayer.registerForData(new MeetingsDataLayer.MeetingsDataListener() {
            @Override
            public void onMeetingDataChanged(List<Meeting> meetings) {
                runOnUiThread(() -> {
                    meetingsAdapter.refreshMeetingsList(meetings);
                    mapFragment2.updateMeetingList(meetings);
                });
            }
        });

        smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.INVISIBLE);
        listFragment = new MeetingListFragment(meetingsAdapter);

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
                    //mapFragment.getMapAsync(this);
                    if (!isMapLoaded){

//                        hookupLocation();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        isMapReady=true;
        enableMyLocation();
        refreshMarkers();
        googleMap.setOnInfoWindowClickListener(this);
        // info window.
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
//        internal val DEFAULT_TLV_LATLNG: LatLng = LatLng(32.09040223978312, 34.782786585677016)azrieli
        //internal val DEFAULT_CENTER_LATLNG: LatLng = LatLng(31.298816, 34.880428)7.5zoom

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, 15));
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
                    if (chosenLocation == null || chosenLocation.getAccuracy() > location.getAccuracy()) {
                        chosenLocation = location;
                    }
                }

                applyCurrentLocation(chosenLocation);
                userLocation = chosenLocation;

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
                userLocation = loc;
                // Got last known location. In some rare situations this can be null.
                applyCurrentLocation(loc);

            }
        });
    }
    public void applyCurrentLocation(Location location) {
        if (location != null && isMapReady) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

            if (!isCameraMoved) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                isCameraMoved = true;
            }
        }
    }
    private void refreshMarkers() {
        if (map != null && mapFragmentMeetings != null) {
            for (Meeting meeting :  mapFragmentMeetings) {
                LatLng latLng = meeting.getMeetingLocation();
                createMarker(meeting,100,  R.drawable.icons8_dog_walking_50, latLng);
            }

        }
    }

    private BitmapDescriptor createIcon(int iconId, int iconSize) {
        int height = iconSize;
        int width = iconSize;

//        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(iconId);
//        Drawable chipIcon= ResourcesCompat.getDrawable(this.getResources(),iconId,null);
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(iconId,null);

        Bitmap b = bitmapdraw.getBitmap();

        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }


    private void createMarker(Meeting meeting, int iconSize,int iconId,LatLng point) {
        BitmapDescriptor icon = createIcon(iconId, iconSize);
        Marker marker = map.addMarker(new MarkerOptions().position(point).title( " meeting name: "+meeting.getMeetingName())
                .snippet("users: "+meeting.getSubscribedUserIds().size()+"\n"+"distance:"+meeting.getDistance()).icon(icon));
        marker.showInfoWindow();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle() +" Info window clicked",
                Toast.LENGTH_SHORT).show();
    }


    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }
        private RadioGroup mOptions;
        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (mOptions.getCheckedRadioButtonId() != R.id.customInfoContents) {
                // This means that the default info contents will be used.
                return null;
            }
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
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