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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            List<String> users = new ArrayList<>();
                            for (String userId : meetingData.meetingUsers.keySet())
                            {
                                users.add(meetingData.meetingUsers.get(userId).name);
                            }

                            Meeting meeting = new Meeting(
                                    meetingObject.getKey(),
                                    meetingData.meetingName,
                                    meetingData.meetingDescription,
                                    new ArrayList<String>(meetingData.meetingUsers.keySet()),
                                    users,
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

    public void createNewMeeting(String name, String description, LatLng locationLatlng, String userId, String username){
        String id = meetingsRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(id+"/meetingName", name);
        childUpdates.put(id+"/meetingDescription", description);
        childUpdates.put(id+"/meetingLatLng", locationLatlng);
        childUpdates.put(id+"/meetingCreationTime", ServerValue.TIMESTAMP);
        childUpdates.put(id+"/meetingUsers/"+userId+"/name", username);
        meetingsRef.updateChildren(childUpdates);
    }

    public void addMeetingSubscriber(String meetingId, String userId, String userName){
        meetingsRef.child(meetingId)
                .child("meetingUsers")
                .child(userId)
                .child("name")
                .setValue(userName);

    }

    public void removeMeetingSubscriber(String meetingId, String userId){
        meetingsRef.child(meetingId)
                .child("meetingUsers")
                .child(userId)
                .child("name")
                .removeValue();
    }
}
