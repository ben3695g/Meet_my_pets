package com.example.meetmypets.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.meetmypets.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    FirebaseAuth mAuth;
    Button btnLogin;
    SharedPreferences sp;
    Context context;


    public LoginFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        mAuth = FirebaseAuth.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        TextView tvRegister = view.findViewById(R.id.tvRegister);
        TextView tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        tvRegister.setOnClickListener(v -> moveToRegister());

        tvForgotPassword.setOnClickListener(v -> {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                etEmail.setError(getString(R.string.email_not_valid));
                return;
            }
            String email = etEmail.getText().toString().trim();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, R.string.password_recovery_email_sent, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnLogin.setOnClickListener(v -> {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                etEmail.setError(getString(R.string.email_not_valid));
                return;
            }
            if (etPassword.getText().toString().isEmpty()) {
                etPassword.setError(getString(R.string.password_is_empty));
                return;
            }
            if (etPassword.getText().toString().length() < 6) {
                etPassword.setError(getString(R.string.password_is_too_short));
                return;
            }
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, task -> {
                if (task.isSuccessful()) {
                    Log.d("auth", "signInWithCredential:success");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(user.getUid()).child("Details").child("LastSeen")
                                .setValue(ServerValue.TIMESTAMP);
                        moveToMain();
                    }
                } else {
                    Log.d("auth", "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(context, R.string.no_such_user_please_register, Toast.LENGTH_SHORT).show();
                        //TODO move to register
                        moveToRegister();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                        Toast.makeText(context, R.string.wrong_code_entered, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void moveToMain() {
        SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setItemActiveIndex(0);
        getParentFragmentManager().beginTransaction().replace(R.id.flFragment,
                new FeedFragment(), "FeedFragment").commit();
    }

    private void moveToRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        sp.edit().putString("email", email).putString("password",password).apply();
        getParentFragmentManager().beginTransaction().replace(R.id.flFragment,
                new RegisterFragment(), "RegisterFragment")
                .addToBackStack("RegisterFragment").commit();
    }

}
