package com.example.plantnet.authentication.fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.example.plantnet.communitymain.CommunityMain;
import com.example.plantnet.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    // Views
    private EditText etName, etUsername, etPassword, etEmail, etPhoneNumber;
    private CheckBox cbTerms;
    private ImageView ivProfilePicture;
    private Button btRegister;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Uri imageUri;

    // Activity result launcher for image picker
    private ActivityResultLauncher<String> launcher;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Initialize views
        etName = view.findViewById(R.id.et_name);
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        etEmail = view.findViewById(R.id.et_email);
        etPhoneNumber = view.findViewById(R.id.et_pnumber);
        cbTerms = view.findViewById(R.id.cb_tnc);
        btRegister = view.findViewById(R.id.bt_register);
        ImageView profilePicture = view.findViewById(R.id.profile_picture);
        ConstraintLayout profilePictureLayout = view.findViewById(R.id.profile_picture_layout);

        // Activity result launcher for image picker
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            imageUri = uri;
                            Glide.with(requireContext()).load(uri).into(profilePicture);
                        } else {
                            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        profilePictureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*"); // Launch the image picker
            }
        });


        // Set click listener for register button
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (validateForm()) {
                    signUp(email, password);
                }
            }
        });

        return view;
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            valid = false;
        }

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            valid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            valid = false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            valid = false;
        }

        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Phone number is required");
            valid = false;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(requireContext(), "Please agree to Terms and Conditions", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Log.d(TAG, "New user ID: " + user.getUid());
                                if (imageUri != null) {
                                    uploadProfilePicture(user);
                                } else {
                                    saveUserInfoToDatabase(user, null);
                                }
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed: " +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadProfilePicture(FirebaseUser user) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference().child("profile_pictures/" + user.getUid() + ".jpg");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(requireActivity(), taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Log.d(TAG, "Profile picture uploaded successfully: " + uri.toString());
                            saveUserInfoToDatabase(user, uri.toString());
                        }))
                .addOnFailureListener(requireActivity(), e -> {
                    Log.w(TAG, "Failed to upload profile picture", e);
                    Toast.makeText(requireContext(), "Failed to upload profile picture",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserInfoToDatabase(FirebaseUser user, String profilePhotoUrl) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = user.getUid(); // Get user ID from FirebaseUser
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim(); // Retrieve this from an EditText if you have one
        String bio = ""; // Default value or retrieve from input
        String occupation = ""; // Default value or retrieve from input
        String instagramUrl = ""; // Default value or retrieve from input
        String email = user.getEmail(); // Or retrieve from input
        String chatUrl = ""; // Default value or retrieve from input
        String birthday = ""; // Default value or retrieve from input
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Create a new User object with userId as the first argument
        User userInfo = new User(userId, name, username, profilePhotoUrl, bio, occupation, instagramUrl, email, chatUrl,birthday, phoneNumber);

        database.child("users").child(userId).setValue(userInfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile saved to database.");
                        updateUI(user);
                    } else {
                        Log.w(TAG, "Error saving user profile to database.", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getActivity(), CommunityMain.class);
            startActivity(intent);
            getActivity().finish(); // Optional: close the login activity
        } else {
            Toast.makeText(getActivity(), "Log In First", Toast.LENGTH_SHORT).show();
        }
    }
}
