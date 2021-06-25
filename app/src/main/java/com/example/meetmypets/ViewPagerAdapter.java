package com.example.meetmypets;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.meetmypets.fragments.MeetingListFragment;
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
import com.google.android.gms.tasks.Task;

public class ViewPagerAdapter extends FragmentStateAdapter implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    MeetingListFragment meetingListFragment;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isMapReady;
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity)
        {
            super(fragmentActivity);
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    meetingListFragment = new MeetingListFragment();;
                    return meetingListFragment;
                default: {
                    if(mapFragment == null) {
                        mapFragment = SupportMapFragment.newInstance();
                        mapFragment.getActivity();
                        mapFragment.getMapAsync(this);
                    }
                    return mapFragment;


                }

            }
        }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        enableMyLocation();
        isMapReady=true;
    }

        @Override
        public int getItemCount() {return 2; }

//map functions

    @SuppressLint("MissingPermission")//Permission check invoked at MainActivity
    private void enableMyLocation() {
        // [START maps_check_location_permission]

            if (map != null) {
                map.setMyLocationEnabled(true);
            }

        // [END maps_check_location_permission]
    }




    public void applyCurrentLocation(Location location,boolean isMapReady) {
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

