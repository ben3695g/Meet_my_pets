
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meetmypets.adapter.MeetingsAdapter;
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
    private boolean isMapReady,isMapLoaded =false, isCameraMoved =false,isLocationEnabled;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    public void setLocationEnabled(boolean locationEnabled) {
        isLocationEnabled = locationEnabled;
    }

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

        GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = this;
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;
                isMapReady= true;
                // For showing a move to my location button
                //map.set MyLocationEnabled(true);

                // For dropping a marker at a point on the Map
//                refreshMarkers();
                map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                map.setOnInfoWindowClickListener(onInfoWindowClickListener);
               // LatLng ashdod = new LatLng(31.77009916536245, 34.62246552296086);
                //change after ------------------
                LatLng israel = new LatLng(31.298816, 34.880428);
                float zoom = 7.5f;
                CameraPosition cameraPositionafter = new CameraPosition.Builder().target(israel).zoom(zoom).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionafter));
                refreshMarkers();
                //-------------------------------



               // CameraPosition cameraPosition = new CameraPosition.Builder().target(ashdod).zoom(14).build();
               // map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                isMapReady= true;
            }
        });
        return rootView;
    }


    @SuppressLint("MissingPermission")//Permission check invoked at MainActivity
    private void enableMyLocation() {

        if (map != null) {
            map.setMyLocationEnabled(true);
        }

        // [END maps_check_location_permission]
    }


    public void applyCurrentLocation(LatLng point) {
        if (point != null && isMapReady) {
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


        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(iconId,null);

        Bitmap b = bitmapdraw.getBitmap();

        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    public void refreshMeetingList(List<Meeting> mapFragmentMeetings){
        this.mapFragmentMeetings = mapFragmentMeetings;
        refreshMarkers();
    }

    private void createMarker(Meeting meeting, int iconSize,int iconId,LatLng point) {
        BitmapDescriptor icon = createIcon(iconId, iconSize);
        Marker marker = map.addMarker(new MarkerOptions().position(point).title( " meeting name: "+meeting.getMeetingName())
                .snippet("users: "+meeting.getSubscribedUserIds().size()+"\n"+"distance:"+meeting.getFormattedDistance()).icon(icon));
        marker.showInfoWindow();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
           Toast.makeText(getContext(), marker.getTitle() +" Info window clicked",
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
}
