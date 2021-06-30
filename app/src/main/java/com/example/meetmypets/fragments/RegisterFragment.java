package com.example.meetmypets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.meetmypets.R;
import com.example.meetmypets.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RegisterFragment extends Fragment {
    Button btnRegister;
    EditText etName, etEmail, etPassword, etPassword2, etPetName;
    ImageView ivUser,ivPet;
    FirebaseAuth mAuth;
    SharedPreferences sp;
    DatabaseReference  usersRef;
    Uri userImageUri = null;
    Uri petImageUri = null;
    int USER_IMAGE_REQ_CODE = 999;
    int PET_IMAGE_REQ_CODE = 888;
    boolean userImage=true;
    private Fragment fragmentToGo;

    public RegisterFragment(Fragment fragmentToGo) {
        this.fragmentToGo = fragmentToGo;
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
        etPetName = view.findViewById(R.id.etPetName);
        ivUser = view.findViewById(R.id.ivUser);
        ivPet = view.findViewById(R.id.ivPet);


        etEmail.setText(email);
        etPassword.setText(password);

        btnRegister.setOnClickListener(v -> {
            if (checkData()) {
                register();
            }
        });

        ivUser.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(intent,USER_IMAGE_REQ_CODE);

        });

        ivPet.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(intent,PET_IMAGE_REQ_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==USER_IMAGE_REQ_CODE && resultCode== Activity.RESULT_OK) {
            if (data!=null){
                userImageUri=data.getData();
                CropImage.activity(userImageUri)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireContext(),this);
            }
        }
        if (requestCode==PET_IMAGE_REQ_CODE && resultCode== Activity.RESULT_OK) {
            if (data!=null){
                petImageUri=data.getData();
                CropImage.activity(petImageUri)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireContext(),this);

            }
        }
        if ( resultCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (result!=null){
                userImageUri=data.getData();
                if(userImage) {
                    userImageUri = data.getData();
                    Glide.with(this)
                            .load(userImageUri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivUser);
                    userImageUri = data.getData();
                } else {
                    petImageUri=data.getData();
                    Glide.with(this)
                            .load(petImageUri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivPet);
                }
            }
        }
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
        if(userImageUri==null)
        {
            Toast.makeText(requireContext(), "Must select image for user!", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(petImageUri==null)
        {
            Toast.makeText(requireContext(), "Must select image for pet!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void register() {
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
                    moveToNextOrMain();
                }
            } else {
                Log.d("auth", "signInWithCredential:failure", task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    //TODO move to register
                    doRegister(email, password , name);

                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(requireContext(),
                            R.string.already_registerd_wrong_code, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doRegister(String email, String password, String name) {
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
                                    moveToNextOrMain();
                                }
                            });
                }
            }
        });
    }

    private void moveToNextOrMain() {
        MainActivity mainActivity = (MainActivity)getActivity();
        if (fragmentToGo != null) mainActivity.navigateToPageFragment(fragmentToGo);
        else mainActivity.navigateToTabFragment(this);
    }
}