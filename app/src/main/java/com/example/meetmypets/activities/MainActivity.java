package com.example.meetmypets.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.meetmypets.R;
import com.example.meetmypets.fragments.FeedFragment;
import com.example.meetmypets.fragments.CreateMeetingFragment;
import com.example.meetmypets.fragments.SettingsFragment;
import com.example.meetmypets.fragments.Splash;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SmoothBottomBar smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.INVISIBLE);

        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new Splash(),"splash").commit();

        smoothBottomBar.setOnItemSelectedListener(position -> {
            switch (position) {
                case 0:
                    handleFragment(new FeedFragment(),"MainFeed");
                    break;
                case 1:
                        handleFragment(new CreateMeetingFragment(), "Meetings");
                        break;
                case 2:
                    handleFragment(new SettingsFragment(), "Settings");
                    break;
            }
            return false;
        });


    }

    void handleFragment(Fragment fragment, String fragmentName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, fragment, fragmentName);
        ft.commit();
    }
}