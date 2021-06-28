package com.example.meetmypets.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meetmypets.R;
import com.example.meetmypets.activities.MainActivity;

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
        Fragment me = this;
        Thread splash_screen = new Thread() {
            public void run() {
                 try {
                    sleep(1500);
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.navigateToTabFragment(me);
                }
            }
        };
        splash_screen.start();
    }
}
