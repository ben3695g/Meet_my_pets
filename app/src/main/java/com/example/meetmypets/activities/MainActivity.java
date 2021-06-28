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

import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.fragments.MeetingsMapFragment;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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

        int resultFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int resultCoarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (resultFineLocation != PackageManager.PERMISSION_GRANTED && resultCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            //todo if permission is denied
            //Dexter d;
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.INVISIBLE);
        listFragment = new MeetingListFragment();
        mapFragment2 = new MeetingsMapFragment();
        settingFragment = new SettingsFragment();
        // temporary simulation of async call for DB
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getMeetingsListFromFireBase();

            }
        }, 3000);


        mapFragment = SupportMapFragment.newInstance();
//todo why is \/ line 103

        navigateToPageFragment(new Splash(listFragment));
        initTabFragment(listFragment);

        smoothBottomBar.setOnItemSelectedListener(position -> {
            switch (position) {
                case 0:
                    switchToTabFragment(listFragment);
                    break;
                case 1:
                    isCameraMoved =false;
                    switchToTabFragment(mapFragment);
                    mapFragment.getMapAsync(this);
                    if (!isMapLoaded){

                        hookupLocation();
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

//    void handleNonSmoothBottomBarFragment(String fragment) {
//        FragmentTransaction ft , ftt;
//                ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
//        if(true)//check if user is loged in
//        {
//            if (fragment == "MeetingPageFragment") {
//                ft.replace(R.id.mainLayout, new MeetingPageFragment(), "MeetingPageFragment").addToBackStack("MeetingPageFragment");
//               // ftt = getSupportFragmentManager().beginTransaction();
//               // ftt.replace(R.id.chatFragment, new CurrentMeeting(), "CurrentMeeting");;
//
//                } else {
//                ft.replace(R.id.mainLayout, new NewMeetingFragment(), "NewMeetingFragment").addToBackStack("NewMeetingFragment");
//            }
//        }else{
//            ft.replace(R.id.mainLayout, new LoginFragment(), "LoginFragment").addToBackStack("LoginFragment");
//        }
//        ft.commit();
//        //ftt.commit();
   // }
//    @Override
//    public void onButtonMeetingClicked(boolean isNewMeeting) {
//        handleNonSmoothBottomBarFragment("MeetingPageFragment");
//        FragmentTransaction ft , ftt;
//        ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
//        ft.replace(R.id.mainLayout, new MeetingPageFragment(), "MeetingPageFragment").addToBackStack("MeetingPageFragment");
//        ft.commit();
//        ftt = getSupportFragmentManager().beginTransaction();
//        ftt.replace(R.id.chatFragment, new CurrentMeeting(), "CurrentMeeting");;
//        ftt.commit();

    //}

//    @Override
//    public void onButtonNewMeetingClicked(boolean isNewMeeting) {
//        handleNonSmoothBottomBarFragment("NewMeetingFragment");
//    }

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
                createMarker(meeting,100,  R.drawable.icons8_dog_park_50 , latLng);
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

    public void getMeetingsListFromFireBase(){
         List<String> users = new ArrayList<String>();
         users.add("a");
         users.add("aa");
         List<String> userss = new ArrayList<String>();
            users.add("a");
            users.add("aa");
            users.add("a");
            users.add("aa");
         LatLng point1 = new LatLng(31.776507546255935, 34.62478162343042);
         LatLng point2 = new LatLng(31.775506291568767, 34.6270703705988);
         LatLng point3 = new LatLng(31.775144063934704, 34.63015265240772);
         LatLng point4 = new LatLng(31.775790297015536, 34.62912524059108);
         LatLng point5 = new LatLng(31.772750968373543, 34.63018607123547);
         LatLng point6 = new LatLng(31.769555361922798, 34.624096677629794);
         LatLng point7 = new LatLng(31.780400396831684, 34.62671999065109);
         LatLng point8 = new LatLng(31.772459825243235, 34.623077619318515);
         LatLng point9 = new LatLng(31.78410689193512, 34.63183210435391);
         LatLng point10 = new LatLng(31.78288560817526, 34.62245156086419);
         LatLng point11 = new LatLng(31.783684597813586, 34.62356374127263);
         LatLng point12 = new LatLng(31.768694958076537, 34.62949570367687);
         LatLng point13 = new LatLng(31.77628097704576, 34.642746431905756);
         LatLng point14 = new LatLng(31.77851285073171, 34.63689206480455);
         LatLng point15 = new LatLng(31.77438521863641, 34.6299922798979);
         LatLng point16 = new LatLng(31.773634667633004, 34.63496384552723);
         LatLng point17 = new LatLng(31.7684601205635, 34.626252256015185);
         LatLng point18 = new LatLng(31.77009916536245, 34.62246552296086);



         //new Location((31.776507546255935, 34.62478162343042));
         List<Meeting> meetings = new ArrayList<>();
         meetings.add(new Meeting("a1", users,1500,point1));
         meetings.add(new Meeting("b2", users,200,point2));
         meetings.add(new Meeting("c3", users,10,point3));
         meetings.add(new Meeting("d4", users,10111,point4));
         meetings.add(new Meeting("e5", users,1990,point5));
         meetings.add(new Meeting("f6", users,30,point6));
         meetings.add(new Meeting("a7", users,1500,point7));
         meetings.add(new Meeting("b8", users,200,point8));
         meetings.add(new Meeting("c9", users,10,point9));
         meetings.add(new Meeting("d10", users,10111,point10));
         meetings.add(new Meeting("e11", users,1990,point11));
         meetings.add(new Meeting("f12", users,30,point12));
         meetings.add(new Meeting("a13", users,1500,point13));
         meetings.add(new Meeting("b14", users,200,point14));
         meetings.add(new Meeting("c15", users,10,point15));
         meetings.add(new Meeting("d16", userss,10111,point16));
         meetings.add(new Meeting("e117", userss,1990,point17));
         meetings.add(new Meeting("f18", userss,30,point18));
         mapFragmentMeetings =meetings;
         listFragment.getMeetingList(meetings);

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