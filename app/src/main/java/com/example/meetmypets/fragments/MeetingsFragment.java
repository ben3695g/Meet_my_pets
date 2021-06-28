package com.example.meetmypets.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetmypets.R;
import com.example.meetmypets.adapter.MeetingsAdapterToDelete;
import com.example.meetmypets.model.MeetingToDelete;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MeetingsFragment extends Fragment {
    private List<MeetingToDelete> meetingsList = new ArrayList<>();
    private RecyclerView rvMeetings;
    private MeetingsAdapterToDelete adapter = null;
    public MeetingsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meetings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.VISIBLE);

        rvMeetings.findViewById(R.id.rvMeetings);
        getData();

        rvMeetings.setHasFixedSize(true);
        rvMeetings.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MeetingsAdapterToDelete(requireContext(), meetingsList);
        rvMeetings.setAdapter(adapter);

        adapter.setListener(position -> {
            Gson gson = new Gson();
            String json = gson.toJson(meetingsList.get(position));
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
            sp.edit().putString("meetingSelected", json).apply();
            getParentFragmentManager().beginTransaction().replace(R.id.flFragment,
                    new MeetingsFragment(), "RegisterFragment");
        });
    }

    private void getData() {
        FirebaseDatabase.getInstance().getReference().child("Meetings")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (final DataSnapshot object : dataSnapshot.getChildren()) {
                                MeetingToDelete meeting = object.getValue(MeetingToDelete.class);
                                meetingsList.add(meeting);
                            }
                            Collections.reverse(meetingsList);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("firebase", "onCancelled", databaseError.toException());
                    }
                });

    }
}