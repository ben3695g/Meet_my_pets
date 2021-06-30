package com.example.meetmypets.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MeetingViewHolder> {
    private List<Meeting> meetings = new ArrayList<>();
    private MyMeetingListener listener;

    public interface MyMeetingListener {
        void onMeetingClicked(int position,View view);
    }

    public MeetingsAdapter() {

    }

    public void setListener(MyMeetingListener listener) {
        this.listener = listener;
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        TextView meetingName;
        TextView meetingUsers;
        TextView meetingDistance;

        public MeetingViewHolder(View view) {
            super(view);
            relativeLayout =view.findViewById(R.id.cellLayout);
            meetingName = view.findViewById(R.id.cellText);
            meetingUsers = view.findViewById(R.id.cellUsers);
            meetingDistance = view.findViewById(R.id.cellDistance);

          //  meetingImage = view.findViewById(R.id.cellImage);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onMeetingClicked(getBindingAdapterPosition(),v);
                }
            });
        }
    }

    public Meeting getMeeting(int position){
        return meetings.get(position);
    }

    public List<Meeting> updateDistance(LatLng userLocation){

        for (Meeting meeting : meetings){
            LatLng loc = meeting.getMeetingLocation();
            if (loc != null){
                meeting.setDistance(calculateDistance(userLocation.latitude, userLocation.longitude, loc.latitude, loc.longitude));
            }
        }

        notifyDataSetChanged();
        return meetings;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344; // in Kilometers
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell, parent,false);
        MeetingViewHolder meetingViewHolder = new MeetingViewHolder((view));
        return  meetingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        Meeting meeting = meetings.get(position);
        holder.meetingName.setText(meeting.getMeetingName());
        holder.meetingUsers.setText("users:" + meeting.getSubscribedUserIds().size());
        double distance = meeting.getDistance();
        if (distance> 0.001)
        {
            holder.meetingDistance.setText(meeting.getFormattedDistance());
        }
    }

    @Override
    public int getItemCount() {
        return meetings != null ? meetings.size() : 0;
    }

    public void orderByName(boolean toggleDirectionName){
        if(toggleDirectionName) {
            if (meetings == null) return;
            this.meetings = meetings.stream().sorted((x, y) -> x.getMeetingName().compareTo(y.getMeetingName())).collect(Collectors.toList());
        }else{
            this.meetings = meetings.stream().sorted((y, x) -> x.getMeetingName().compareTo(y.getMeetingName())).collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    public void orderByDistance(boolean toggleDirectionDistance){
        if(toggleDirectionDistance){
        if (meetings == null) return;
            this.meetings = meetings.stream().sorted((x, y)-> Double.compare(x.getDistance(),y.getDistance())).collect(Collectors.toList());
        }else{
            this.meetings = meetings.stream().sorted((y, x)-> Double.compare(x.getDistance(),y.getDistance())).collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }
    public void orderByNumberOfUsers(boolean toggleDirectionUsers){
        if(toggleDirectionUsers) {
            if (meetings == null) return;
            this.meetings = meetings.stream().sorted((x, y)-> Integer.compare(x.getSubscribedUserIds().size(),y.getSubscribedUserIds().size())).collect(Collectors.toList());
        }else{
            this.meetings = meetings.stream().sorted((y, x)-> Integer.compare(x.getSubscribedUserIds().size(),y.getSubscribedUserIds().size())).collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    public void refreshMeetingsList(List<Meeting> meetings){
        this.meetings =meetings;
        notifyDataSetChanged();
    }
}
