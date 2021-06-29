package com.example.meetmypets.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.Map;

public class MeetingData {
    public String meetingName;
    public String meetingDescription;
    public MeetingLocation meetingLatLng;
    public long meetingCreationTime;
    public Map<String,Object> meetingUsers;
}
