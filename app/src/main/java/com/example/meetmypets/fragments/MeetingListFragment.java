package com.example.meetmypets.fragments;

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

import com.example.meetmypets.model.Meeting;
import com.example.meetmypets.R;
import com.example.meetmypets.activities.MainActivity;
import com.example.meetmypets.adapter.MeetingsAdapter;
import com.example.meetmypets.model.MeetingToDelete;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.List;

import static com.google.firebase.database.DatabaseReference.goOffline;
import static com.google.firebase.database.DatabaseReference.goOnline;

public class MeetingListFragment extends Fragment {

   // private ListActionCallback callback;
   FirebaseUser user;
    private MeetingsAdapter meetingsAdapter;
    private List<Meeting> chosenMeetings;
    private boolean diractionFilter =true, nameFilter =true, userFilter = true;

    public MeetingListFragment(){
        meetingsAdapter = new MeetingsAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMeetingList(chosenMeetings);

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
       // FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);

//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setApiKey("<AIzaSyCqwBDIgYd42oXxjuqXEHX7408cwn2Qfxg>")
//                .setDatabaseUrl("https://meet-my-pets-default-rtdb.europe-west1.firebasedatabase.app/")
//                .setApplicationId("<meet-my-pets>")
//                .build();
//        FirebaseApp app = FirebaseApp.initializeApp(getContext(), options);



//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference = database.getReference();;
//        String id = reference.push().getKey();
//        MeetingToDelete meeting = new MeetingToDelete();
//        meeting.setId(id);
//        meeting.setMeetingName("first name");
//        reference.child(id).setValue(meeting);


        orderByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByName(nameFilter);
                nameFilter =! nameFilter;
            }
        });

        orderByUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByNumberOfUsers(userFilter);
                userFilter =! userFilter;
            }
        });

        orderByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingsAdapter.orderByDistance(diractionFilter);
                diractionFilter =!diractionFilter;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "add new meeting", Toast.LENGTH_SHORT).show();

                MainActivity mainActivity = (MainActivity)getActivity();
                NewMeetingFragment newMeetingFragment = new NewMeetingFragment(meetingsAdapter);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) //check if user is loged in
                {
                    mainActivity.navigateToPageFragment(newMeetingFragment);
                } else {
                    mainActivity.navigateToPageFragment(new LoginFragment(newMeetingFragment));
                }
            }
        });


        meetingsAdapter.setListener(new MeetingsAdapter.MyMeetingListener() {
            @Override
            public void onMeetingClicked(int position, View view) {
                Meeting meeting = chosenMeetings.get(position);
                Toast.makeText(getActivity(), chosenMeetings.get(position).getMeetingName(), Toast.LENGTH_SHORT).show();

                MainActivity mainActivity = (MainActivity)getActivity();
                MeetingPageFragment meetingFragment = new MeetingPageFragment(meeting);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) //check if user is loged in
                {
                    mainActivity.navigateToPageFragment(meetingFragment);
                } else {
                    mainActivity.navigateToPageFragment(new LoginFragment(meetingFragment));
                }
            }
        });

        // 3) attache logic class to the View
        recyclerView.setAdapter(meetingsAdapter);
        return root;
    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof ListActionCallback) {
//            callback = (ListActionCallback) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement SampleCallback");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        callback = null;
//    }

    public void getMeetingList(List<Meeting> meetings){
        chosenMeetings = meetings;
        meetingsAdapter.refreshMeetingsList(chosenMeetings);
        //meetingsAdapter.readData();
    }
}
