package com.example.meetmypets.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetmypets.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import me.ibrahimsn.lib.SmoothBottomBar;

/**
 * Created by Gal Reshef on 6/25/2021.
 */

public class RegisterFragment extends Fragment {
    Button btnRegister;
    EditText etName, etEmail, etPassword, etPassword2;
    FirebaseAuth mAuth;
    SharedPreferences sp;
    DatabaseReference  usersRef;
    public RegisterFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String email = sp.getString("email", "");
        String password = sp.getString("password", "");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etPassword2 = view.findViewById(R.id.etPassword2);
        btnRegister = view.findViewById(R.id.btnRegister);

        etEmail.setText(email);
        etPassword.setText(password);

        btnRegister.setOnClickListener(v -> {
            if (checkData()) {
                login();
            }
        });




    }

    private boolean checkData() {

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError(getString(R.string.email_not_valid));
            return false ;
        }

        if (etPassword.getText().toString().trim().isEmpty()) {
            etPassword.setError(getString(R.string.password_is_empty));
            return false;
        }
        if (etPassword.getText().toString().trim().length() < 6) {
            etPassword.setError(getString(R.string.password_is_too_short));
            return false;
        }
        if (!etPassword.getText().toString().trim().equals(etPassword2.getText().toString().trim())) {
            etPassword2.setError(getString(R.string.password_not_equal));
            return false;
        }
        if (etName.getText().toString().isEmpty()) {
            etName.setError(getString(R.string.name_is_empty));
            return false;
        }

        return true;
    }

    public void login() {
        String email, password, name;
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        name = etName.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("auth", "signInWithCredential:success");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    usersRef.child(user.getUid()).child("Details").child("LastSeen")
                            .setValue(ServerValue.TIMESTAMP);
                    moveToMain();
                }
            } else {
                Log.d("auth", "signInWithCredential:failure", task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    //TODO move to register
                    register(email, password , name);

                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(requireContext(),
                            R.string.already_registerd_wrong_code, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("auth", "user created successfully");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    usersRef.child(user.getUid()).child("Details").child("Name").
                            setValue(name);
                    usersRef.child(user.getUid()).child("Details").child("Email")
                            .setValue(etEmail.getText().toString());
                    usersRef.child(user.getUid()).child("Details").child("RegistrationDate")
                            .setValue(ServerValue.TIMESTAMP);
                    usersRef.child(user.getUid()).child("Details").child("LastSeen")
                            .setValue(ServerValue.TIMESTAMP);
                    // Eli - Add here whatever data you need

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Name", etName.getText().toString());
                    editor.putString("Email", etEmail.getText().toString());
                    editor.apply();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(etName.getText().toString())
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("firebase", "User profile updated.");
                                    moveToMain();
                                }
                            });
                }
            }
        });
    }


    private void moveToMain() {
        SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setItemActiveIndex(0);
        getParentFragmentManager().beginTransaction().replace(R.id.flFragment,
                new FeedFragment(), "FeedFragment").commit();
    }
}
