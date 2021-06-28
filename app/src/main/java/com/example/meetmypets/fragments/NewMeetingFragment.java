package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.meetmypets.R;
import com.example.meetmypets.adapter.MeetingsAdapter;

public class NewMeetingFragment extends Fragment {

    private MeetingsAdapter recyclerViewAdapter;

    public NewMeetingFragment(MeetingsAdapter recyclerViewAdapter) {
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_meeting, container, false);
        return root;
    }
}
