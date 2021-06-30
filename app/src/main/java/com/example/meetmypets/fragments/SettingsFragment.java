package com.example.meetmypets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetmypets.R;
import com.example.meetmypets.activities.MainActivity;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LoginFragment loginFragment =new LoginFragment();
//                MainActivity mainActivity = (MainActivity)getActivity();
//                mainActivity.navigateToPageFragment(new LoginFragment(LoginFragment));
                getParentFragmentManager().beginTransaction().replace(R.id.flFragment,
                        new LoginFragment(null), "LoginFragment")
                        .addToBackStack("LoginFragment").commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}