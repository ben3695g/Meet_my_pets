package com.example.meetmypets.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meetmypets.R;

import me.ibrahimsn.lib.SmoothBottomBar;

public class Splash extends Fragment {

    private  Fragment firstFragment;

    public Splash(Fragment firstFragment) {
        this.firstFragment = firstFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Thread splash_screen = new Thread() {
            public void run() {
                Activity activity = getActivity();
                SmoothBottomBar smoothBottomBar = activity.findViewById(R.id.bottomBar);

                try {
                    sleep(1500);

                } catch(Exception e) {
                    e.printStackTrace();

                } finally {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    Fragment fragment = getParentFragmentManager().findFragmentByTag("splash");
                    ft.remove(fragment).replace(R.id.flFragment, firstFragment, "Meetings");
                    ft.commit();
                    activity.runOnUiThread(() -> smoothBottomBar.setVisibility(View.VISIBLE));
                }
            }
        };
        splash_screen.start();
    }
}
