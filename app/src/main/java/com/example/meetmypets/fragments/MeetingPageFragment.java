package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.meetmypets.activities.MainActivity;
import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.model.MeetingsDataLayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MeetingPageFragment extends Fragment {
    private Meeting meeting;
    private MeetingsDataLayer dataLayer;
    private String userId;
    private String userName;

    public MeetingPageFragment(Meeting meeting, MeetingsDataLayer dataLayer){
        this.meeting = meeting;
        this.dataLayer = dataLayer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_meeting_page, container, false);
        ListView userListView = root.findViewById(R.id.meetingUsersList);
        userListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, meeting.getSubscribedUsers()));
        TextView description = root.findViewById(R.id.meetingPageDescription);
        description.setMovementMethod(new ScrollingMovementMethod());
        TextView name =root.findViewById(R.id.meetingPageName);
        Button subscription, mute,goToMap;
        subscription=root.findViewById(R.id.subscriptionToMeetingBtn);
        name.setText(meeting.getMeetingName());
        description.setText(meeting.getMeetingDescription());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId =user.getUid();
        userName = user.getDisplayName();
        boolean subscribed = meeting.getSubscribedUserIds().contains(userId);
        subscription.setText(subscribed ? "Unsubscribe" : "Subscribe");

        Fragment me = this;
        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (subscribed){
                    if (meeting.getSubscribedUserIds().size() > 1)
                        dataLayer.removeMeetingSubscriber(meeting.getMeetingId(), userId);
                }
                else  {
                    dataLayer.addMeetingSubscriber(meeting.getMeetingId(), userId, userName);
                }
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.navigateToTabFragment(me);
            }
        });

        //    mute=root.findViewById(R.id.muteNotificationsBtn);
        //    goToMap=root.findViewById(R.id.showOnMapBtn);
//        mute.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        goToMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return root;
    }
}
