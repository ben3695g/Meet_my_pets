package com.example.meetmypets.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetmypets.R;
import com.example.meetmypets.adapter.MessagesAdapter;
import com.example.meetmypets.model.Message;
import com.example.meetmypets.model.Meeting;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CurrentMeeting extends Fragment {
    FirebaseUser user;
    private MessagesAdapter adapter = null;
    RecyclerView rvMessages;
    SharedPreferences sp;
    Meeting meeting;
    List<Message> messageList = new ArrayList<>();
    DatabaseReference meetingRef = null;
    FloatingActionButton fabSend;
    EditText etMessageText;

    public CurrentMeeting() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_meeting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String json = sp.getString("meetingSelected", "");
        if (json.isEmpty()) {
            getParentFragmentManager().popBackStack();
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        meetingRef = FirebaseDatabase.getInstance().getReference()
                .child("Meetings")
                .child("meet-aaa")
                //.child(meeting.getId())
                .child("Messages");

        Gson gson = new Gson();
        Type type = new TypeToken<Meeting>() {
        }.getType();
        meeting = gson.fromJson(json, type);
        rvMessages=view.findViewById(R.id.rvMessages);
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        getMessages();

        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MessagesAdapter(requireContext(), messageList);
        rvMessages.setAdapter(adapter);



        fabSend = view.findViewById(R.id.fabSend);

        fabSend.setOnClickListener(v -> {
            String text = etMessageText.getText().toString().trim();
            ImageView ivFab = view.findViewById(R.id.ivFab);
            Bitmap img = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);
            ImageViewAnimatedChange(ivFab, img);
            if (!text.isEmpty()) {
                sendMessage(text);
            } else {
                Toast.makeText(requireContext(), R.string.message_cant_be_empty, Toast.LENGTH_SHORT).show();
            }
            etMessageText.setText("");
        });
    }
    public void getMessages() {
        meetingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    messageList.clear();
                    for (DataSnapshot messageItem : snapshot.getChildren()) {
                        if (messageItem.exists()) {
                            if (messageItem.hasChild("time") &&
                                    messageItem.hasChild("senderUid") &&
                                    messageItem.hasChild("message")) {
                                Message message = messageItem.getValue(Message.class);
                                if (user != null) {
                                    if (user.getUid().equals(message.getSenderUid())) {
                                        message.setType(0);
                                    } else {
                                        message.setType(1);
                                    }
                                }
                                messageList.add(message);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    private void sendMessage(String text) {
        Message newMessage = new Message();

        newMessage.setType(-1);
        newMessage.setMessage(text);
        newMessage.setSenderUid(user.getUid());
        newMessage.setTime(ServerValue.TIMESTAMP);
        meetingRef.push().setValue(newMessage);
        meetingRef = FirebaseDatabase.getInstance().getReference()
                .child("Meetings").child(meeting.getId()).child("Messages");
        meetingRef.child("LastMessage").setValue(text);
    }



    private void ImageViewAnimatedChange(final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

}