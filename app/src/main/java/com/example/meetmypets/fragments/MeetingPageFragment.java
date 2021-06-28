package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        TextView textView = root.findViewById(R.id.meetingPageDescription);
        textView.setMovementMethod(new ScrollingMovementMethod());


        return root;
    }
}
