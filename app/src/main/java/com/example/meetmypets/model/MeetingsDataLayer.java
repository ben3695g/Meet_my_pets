package com.example.meetmypets.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeetingsDataLayer {
    private DatabaseReference meetingsRef;

    public interface MeetingsDataListener {
        void onMeetingDataChanged(List<Meeting> meetings);
    }

    private DatabaseReference getMeetingsRef() {
        if (meetingsRef == null)
            meetingsRef = FirebaseDatabase.getInstance().getReference().child("Allmeetings");
        return meetingsRef;
    }

    public void registerForData(MeetingsDataListener listener) {
        meetingsRef = getMeetingsRef();
        meetingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Meeting> meetings = new ArrayList<Meeting>();
                    for (final DataSnapshot meetingObject : dataSnapshot.getChildren()) {
                        try {
                            MeetingData meetingData = meetingObject.getValue(MeetingData.class);
                            Meeting meeting = new Meeting(
                                    meetingData.meetingName,
                                    meetingData.meetingDescription,
                                    new ArrayList<String>(meetingData.meetingUsers.keySet()),
                                    0,
                                    new LatLng(meetingData.meetingLatLng.latitude, meetingData.meetingLatLng.longitude));
                            meetings.add(meeting);
                        } catch (Exception e) {
                            Log.e("read-meeting", "Failed to deserialize meeting data", e);
                        }
                    }

                    Collections.reverse(meetings);

                    listener.onMeetingDataChanged(meetings);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "onCancelled", databaseError.toException());
            }
        });

    }
}
