package com.example.meetmypets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetmypets.R;
import com.example.meetmypets.model.MeetingToDelete;

import java.util.List;

public class MeetingsAdapterToDelete extends RecyclerView.Adapter<MeetingsAdapterToDelete.MyMeetingsViewHolder> {

    private Context context;
    private final List<MeetingToDelete> meetingList;
    private myMeetingsListener listener;

    public interface myMeetingsListener {
        void onMeetingsListener(int position);
    }
    public void setListener(myMeetingsListener listener) {
        this.listener = listener;
    }


    public MeetingsAdapterToDelete(Context context, List<MeetingToDelete> meetingList) {
        this.meetingList = meetingList;
        this.context = context;
    }

    public class MyMeetingsViewHolder  extends RecyclerView.ViewHolder {
        //TODO eli - fill the items like textView ImageView
        final TextView tvMeetingName;

        MyMeetingsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeetingName = itemView.findViewById(R.id.tvMeetingName);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onMeetingsListener(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public MyMeetingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting,parent,false);
        return new MyMeetingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMeetingsViewHolder holder, int position) {
        MeetingToDelete meeting = meetingList.get(position);
        holder.tvMeetingName.setText(meeting.getMeetingName());

    }

    @Override
    public int getItemCount() { return meetingList.size(); }


}
