package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.activities.MainActivity;
import com.example.meetmypets.adapter.MeetingsAdapter;
import com.example.meetmypets.model.MeetingData;
import com.example.meetmypets.model.MeetingToDelete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.firebase.database.DatabaseReference.goOffline;
import static com.google.firebase.database.DatabaseReference.goOnline;

public class MeetingListFragment extends Fragment {
    private MeetingsAdapter meetingsAdapter;
    private DatabaseReference meetingRef;
    private List<Meeting> chosenMeetings;
    private boolean diractionFilter =true, nameFilter =true, userFilter = true;

    public MeetingListFragment(MeetingsAdapter meetingsAdapter){
        this.meetingsAdapter = meetingsAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_meetings_list,container,false);
        Button orderByName, orderByDistance,orderByUsers;
        orderByName = root.findViewById(R.id.sortByName);
        orderByUsers = root.findViewById(R.id.sortByUsers);
        orderByDistance =root.findViewById(R.id.sortByDistance);
        FloatingActionButton floatingActionButton = root.findViewById(R.id.fab);

        //RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.meetingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        orderByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByName(nameFilter);
                nameFilter =! nameFilter;
            }
        });

        orderByUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByNumberOfUsers(userFilter);
                userFilter =! userFilter;
            }
        });

        orderByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByDistance(diractionFilter);
                diractionFilter =!diractionFilter;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "add new meeting", Toast.LENGTH_SHORT).show();

                MainActivity mainActivity = (MainActivity)getActivity();
                NewMeetingFragment newMeetingFragment = new NewMeetingFragment(meetingsAdapter);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) //check if user is loged in
                {
                    mainActivity.navigateToPageFragment(newMeetingFragment);
                } else {
                    mainActivity.navigateToPageFragment(new LoginFragment(newMeetingFragment));
                }
            }
        });


        meetingsAdapter.setListener(new MeetingsAdapter.MyMeetingListener() {
            @Override
            public void onMeetingClicked(int position, View view) {
                Meeting meeting = chosenMeetings.get(position);
                Toast.makeText(getActivity(), chosenMeetings.get(position).getMeetingName(), Toast.LENGTH_SHORT).show();

                MainActivity mainActivity = (MainActivity)getActivity();
                MeetingPageFragment meetingFragment = new MeetingPageFragment(meeting);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) //check if user is loged in
                {
                    mainActivity.navigateToPageFragment(meetingFragment);
                } else {
                    mainActivity.navigateToPageFragment(new LoginFragment(meetingFragment));
                }
            }
        });

        // 3) attache logic class to the View
        recyclerView.setAdapter(meetingsAdapter);
        return root;
    }

    /*
    public void getMeetingList(List<Meeting> meetings){
        getData();
    }

    private void getData() {
        Fragment me = this;
        if (meetingRef == null) meetingRef = FirebaseDatabase.getInstance().getReference().child("Allmeetings");
        meetingRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            chosenMeetings = new ArrayList<Meeting>();
                            for (final DataSnapshot meetingObject : dataSnapshot.getChildren()) {
                                try {
                                    MeetingData meetingData = meetingObject.getValue(MeetingData.class);
                                    Meeting meeting = new Meeting(
                                            meetingData.meetingName,
                                             meetingData.meetingDescription,
                                             new ArrayList<String>(meetingData.meetingUsers.keySet()),
                                            0,
                                            new LatLng(meetingData.meetingLatLng.latitude, meetingData.meetingLatLng.longitude));
                                    chosenMeetings.add(meeting);
                                }
                                catch (Exception e){
                                    Log.e("read-meeting", "Failed to deserialize meeting data", e);
                                }
                            }

                            Collections.reverse(chosenMeetings);

                            me.getActivity().runOnUiThread(() -> {
                                meetingsAdapter.refreshMeetingsList(chosenMeetings);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("firebase", "onCancelled", databaseError.toException());
                    }
                });

    }
    */
}
