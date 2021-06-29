
package com.example.meetmypets.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MeetingsMapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {
    private List<Meeting> mapFragmentMeetings;
    private GoogleMap map;
    private MapView mMapView;
    private SupportMapFragment mapFragment;
    private boolean isMapReady,isMapLoaded =false, isCameraMoved =false;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mapFragment = SupportMapFragment.newInstance();
       // mapFragment.getMapAsync(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;

                // For showing a move to my location button
                //map.set MyLocationEnabled(true);

                // For dropping a marker at a point on the Map
//                refreshMarkers();
//                map.setOnInfoWindowClickListener(this);
                LatLng sydney = new LatLng(31.77009916536245, 34.62246552296086);
                map.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        return rootView;
    }

    public void setMapFragmentMeetings(List<Meeting> mapFragmentMeetings) {
        this.mapFragmentMeetings = mapFragmentMeetings;
    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(mapFragment., container, false);
//
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//        isMapReady=true;
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.77009916536245, 34.62246552296086), 14));
//        //enableMyLocation();
//        refreshMarkers();
//        googleMap.setOnInfoWindowClickListener(this);
//        // info window.
//        //map.setInfoWindowAdapter(new MainActivity.CustomInfoWindowAdapter());
//
////        internal val DEFAULT_TLV_LATLNG: LatLng = LatLng(32.09040223978312, 34.782786585677016)azrieli
//        //internal val DEFAULT_CENTER_LATLNG: LatLng = LatLng(31.298816, 34.880428)7.5zoom
//
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, 15));
//    }
    @SuppressLint("MissingPermission")//Permission check invoked at MainActivity
    private void enableMyLocation() {
        // [START maps_check_location_permission]

        if (map != null) {
            map.setMyLocationEnabled(true);
        }

        // [END maps_check_location_permission]
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
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
                createMarker(meeting,100,  R.drawable.icons8_dog_walking_50 , latLng);
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
//            Toast.makeText(this, marker.getTitle() +" Info window clicked",
//                    Toast.LENGTH_SHORT).show();
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
}
