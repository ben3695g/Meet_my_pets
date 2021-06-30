package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.activities.MainActivity;
import com.example.meetmypets.adapter.MeetingsAdapter;
import com.example.meetmypets.model.MeetingsDataLayer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MeetingListFragment extends Fragment {
    private MeetingsAdapter meetingsAdapter;
    private MeetingsDataLayer dataLayer;
    private boolean directionFilter = true, nameFilter = true, userFilter = true;

    public MeetingListFragment(MeetingsAdapter meetingsAdapter, MeetingsDataLayer dataLayer) {
        this.meetingsAdapter = meetingsAdapter;
        this.dataLayer = dataLayer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_meetings_list, container, false);
        Button orderByName, orderByDistance, orderByUsers;
        orderByName = root.findViewById(R.id.sortByName);
        orderByUsers = root.findViewById(R.id.sortByUsers);
        orderByDistance = root.findViewById(R.id.sortByDistance);
        FloatingActionButton floatingActionButton = root.findViewById(R.id.fab);

        //RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.meetingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        orderByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByName(nameFilter);
                nameFilter = !nameFilter;
            }
        });

        orderByUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByNumberOfUsers(userFilter);
                userFilter = !userFilter;
            }
        });

        orderByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByDistance(directionFilter);
                directionFilter = !directionFilter;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "add new meeting", Toast.LENGTH_SHORT).show();

                MainActivity mainActivity = (MainActivity) getActivity();
                NewMeetingFragment newMeetingFragment = new NewMeetingFragment(meetingsAdapter, dataLayer);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) //check if user is loged in
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
                Meeting meeting = meetingsAdapter.getMeeting(position);
                Toast.makeText(getActivity(), meeting.getMeetingName(), Toast.LENGTH_SHORT).show();

                MainActivity mainActivity = (MainActivity) getActivity();
                MeetingPageFragment meetingFragment = new MeetingPageFragment(meeting, dataLayer);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) //check if user is loged in
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
}