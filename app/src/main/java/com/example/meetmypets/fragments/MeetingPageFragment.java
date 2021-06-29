package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;

public class MeetingPageFragment extends Fragment {
    private Meeting meeting;
    public MeetingPageFragment(Meeting meeting){
        this.meeting = meeting;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_meeting_page, container, false);
        TextView description = root.findViewById(R.id.meetingPageDescription);
        description.setMovementMethod(new ScrollingMovementMethod());
        TextView name =root.findViewById(R.id.meetingPageName);
        Button subscription, mute,goToMap;
        subscription=root.findViewById(R.id.subscriptionToMeetingBtn);
        mute=root.findViewById(R.id.muteNotificationsBtn);
        goToMap=root.findViewById(R.id.showOnMapBtn);
        name.setText(meeting.getMeetingName());
        description.setText(meeting.getMeetingDescription());


        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }
}
