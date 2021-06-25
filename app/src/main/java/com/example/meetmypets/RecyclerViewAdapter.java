package com.example.meetmypets;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MeetingViewHolder> {
    private List<Meeting> meetings;
    private MyMeetingListener listener;

    public interface MyMeetingListener {
        void onMeetingClicked(int position,View view);
    }

    public RecyclerViewAdapter(List<Meeting> meetings) {
        this.meetings = meetings;
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
        //Location meetingLocation;
        TextView meetingUsers;
        TextView meetingDistance;
       // ImageView meetingImage;

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
        holder.meetingName.setText(meeting.meetingName);
        holder.meetingUsers.setText("users:" + meeting.subscribedUserIds.size());
      //  holder.meetingImage.setImageBitmap(meeting.meetingImage);//holder.meetingImage.setImageResource(meeting.get
        holder.meetingDistance.setText(meeting.distance + "m");

    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    public void orderByName(){
        this.meetings = meetings.stream().sorted((x,y)-> x.getMeetingName().compareTo(y.getMeetingName())).collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public void orderByDistance(){
        this.meetings = meetings.stream().sorted((x,y)-> Integer.compare(x.getDistance(),y.getDistance())).collect(Collectors.toList());
        notifyDataSetChanged();
    }
}
