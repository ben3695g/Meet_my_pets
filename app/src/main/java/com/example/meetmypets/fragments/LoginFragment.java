package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetmypets.R;

import me.ibrahimsn.lib.SmoothBottomBar;

public class LoginFragment extends Fragment {

    public LoginFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.GONE);


    }
}