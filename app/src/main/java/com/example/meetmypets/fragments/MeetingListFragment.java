package com.example.meetmypets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetmypets.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.adapter.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MeetingListFragment extends Fragment {

    private ListActionCallback callback;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Meeting> chosenMeetings;
    private boolean diractionFilter =true, nameFilter =true, userFilter = true;

    public MeetingListFragment(){
        recyclerViewAdapter = new RecyclerViewAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public interface ListActionCallback {
        void onButtonMeetingClicked(boolean bool);
        void onButtonNewMeetingClicked(boolean bool);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_meetings_list,container,false);
        Button orderByName, orderByDistance,orderByUsers;
        orderByName = root.findViewById(R.id.sortByName);
        orderByUsers = root.findViewById(R.id.sortByUsers);
        orderByDistance =root.findViewById(R.id.sortByDistance);
        FloatingActionButton floatingActionButton = root.findViewById(R.id.fab);
        //RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.meetingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //static users id list initialization


        orderByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter.orderByName(nameFilter);
                nameFilter =! nameFilter;
            }
        });

        orderByUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter.orderByNumberOfUsers(userFilter);
                userFilter =! userFilter;
            }
        });

        orderByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAdapter.orderByDistance(diractionFilter);
                diractionFilter =!diractionFilter;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "add new meeting", Toast.LENGTH_SHORT).show();
                callback.onButtonNewMeetingClicked(true);
            }
        });


        recyclerViewAdapter.setListener(new RecyclerViewAdapter.MyMeetingListener() {
            @Override
            public void onMeetingClicked(int position, View view) {
                Toast.makeText(getActivity(), chosenMeetings.get(position).getMeetingName(), Toast.LENGTH_SHORT).show();
                callback.onButtonMeetingClicked(false);
            }
        });

        // 3) attache logic class to the View
        recyclerView.setAdapter(recyclerViewAdapter);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListActionCallback) {
            callback = (ListActionCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SampleCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public void getMeetingList(List<Meeting> meetings){
        chosenMeetings = meetings;
        recyclerViewAdapter.refreshMeetingsList(chosenMeetings);
    }
}
