package com.example.meetmypets.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetmypets.R;
import com.example.meetmypets.activities.MainActivity;
import com.example.meetmypets.adapter.MeetingsAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class NewMeetingFragment extends Fragment implements GoogleMap.OnMapClickListener,GoogleMap.OnMarkerClickListener {
    private DatabaseReference meetingRef;
    private MeetingsAdapter recyclerViewAdapter;
    private FirebaseAuth mAuth;
    private MeetingsMapFragment newMeetingMapFragment;
    private LatLng locationLatlng;
    private GoogleMap map;
    private TextView locationLatLng;
    private EditText meetingName, meetingDescription;

    public NewMeetingFragment(MeetingsAdapter recyclerViewAdapter) {
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.meetingRef = FirebaseDatabase.getInstance().getReference().child("Allmeetings");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_meeting, container, false);
        MapView mapView = root.findViewById(R.id.newMeetingMapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;
                LatLng isreal = new LatLng(31.298816, 34.880428);
                float zoom = 7.5f;
                CameraPosition cameraPosition = new CameraPosition.Builder().target(isreal).zoom(zoom).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
                {
                    @Override
                    public void onMapClick(LatLng arg0)
                    {
                        MainActivity mainActivity = (MainActivity)getActivity();
                        hideKeyboard(mainActivity);
                        map.clear();
                        map.addMarker(new MarkerOptions().position(arg0).title("Meeting location"));
                        locationLatlng = arg0;
                        locationLatLng.setText("("+arg0.latitude+","+ arg0.longitude+")");
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(arg0).zoom(14).build();
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
            }
        });


        meetingName = root.findViewById(R.id.newMeetingName);
        meetingDescription = root.findViewById(R.id.newMeetingDescription);
        Button createMeeting, pickLocation;
        createMeeting = root.findViewById(R.id.createMeetingBtn);
        locationLatLng = root.findViewById(R.id.locationLatlng);

        createMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationLatLng.getText().toString() == "" || meetingName.getText().toString() == "" || meetingDescription.getText().toString() == "" ){
                    Toast.makeText(getContext(), " please input all parameters",
                            Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Toast.makeText(getContext(), "  all parameters exist: creating meeting",
                            Toast.LENGTH_SHORT).show();
                    String name,description,username;
                    name=  meetingName.getText().toString();
                    description= meetingDescription.getText().toString();
                    LatLng l = locationLatlng;
                    mAuth = FirebaseAuth.getInstance();
                    username=  mAuth.getCurrentUser().getDisplayName();


                    String id = meetingRef.push().getKey();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(id+"/meetingName", name);
                    childUpdates.put(id+"/meetingDescription", description);
                    childUpdates.put(id+"/meetingLatLng", locationLatlng);
                    childUpdates.put(id+"/meetingCreationTime", ServerValue.TIMESTAMP);
                    childUpdates.put(id+"/meetingUsers/"+mAuth.getUid()+"/name", username);
                    meetingRef.updateChildren(childUpdates);
//                    meetingRef.child(id).child("meetingName").setValue(name);
//                    meetingRef.child(id).child("meetingDescription").setValue(description);
//                    meetingRef.child(id).child("meetingLatLng").setValue(locationLatlng);
//                    meetingRef.child(id).child("meetingCreationTime").setValue(ServerValue.TIMESTAMP);
//                    meetingRef.child(id).child("meetingUsers").child(mAuth.getUid()).child("name").setValue(username);

                    moveToMain();
                }
            }
        });


        //meetingRef.child(id).child("meetingName").setValue(name);
        return root;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        map.addMarker(new MarkerOptions().position(latLng).title("Meeting location"));
        locationLatlng = latLng;
        locationLatLng.setText(locationLatlng.toString());

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        return false;
    }

    private void moveToMain() {
        MainActivity mainActivity = (MainActivity)getActivity();
        hideKeyboard(mainActivity);
        mainActivity.navigateToTabFragment(this);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
