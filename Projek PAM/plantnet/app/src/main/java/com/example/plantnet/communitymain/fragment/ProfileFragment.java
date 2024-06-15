package com.example.plantnet.communitymain.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.example.plantnet.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ImageView profilePhotoImageView;
    private TextView display_name;
    private EditText usernameEditText;
    private EditText bioEditText;
    private EditText occupationEditText;
    private EditText birthdayEditText;
    private EditText instagramEditText;
    private MaterialButton editProfileCardView, finish_edit;

    private DatabaseReference usersReference;
    private FirebaseUser currentUser;
    private User currentUserData;

    private boolean isEditMode = false;
    private boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profilePhotoImageView = view.findViewById(R.id.detail_photo);
        display_name = view.findViewById(R.id.display_name);
        usernameEditText = view.findViewById(R.id.detail_username);
        bioEditText = view.findViewById(R.id.detail_bio);
        occupationEditText = view.findViewById(R.id.detail_occupation);
        birthdayEditText = view.findViewById(R.id.detail_birthday);
        instagramEditText = view.findViewById(R.id.detail_instagram);
        editProfileCardView = view.findViewById(R.id.edit_profile);
        finish_edit = view.findViewById(R.id.finish_edit);

        // Firebase references
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Fetch user data
        if (currentUser != null) {
            fetchUserData(currentUser.getUid());
        }

        // Handle edit profile button click
        editProfileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditMode) {
                    // Enter edit mode
                    enterEditMode();
                } else {
                    // Exit edit mode
                    exitEditMode();
                }
            }
        });

        // Handle finish edit button click
        finish_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        isFirstLoad = false; // Set isFirstLoad to false after initialization

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            // Make sure edit mode is not entered automatically on resume
            exitEditMode();
        }
    }

    private void fetchUserData(String userId) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserData = snapshot.getValue(User.class);
                if (currentUserData != null) {
                    // Set user data to views
                    display_name.setText(currentUserData.getUserName());
                    usernameEditText.setText(currentUserData.getUserName());
                    bioEditText.setText(currentUserData.getBio());
                    occupationEditText.setText(currentUserData.getOccupation());
                    birthdayEditText.setText(currentUserData.getBirthday());
                    instagramEditText.setText(currentUserData.getInstagramUrl());

                    // Load profile photo using Glide
                    if (currentUserData.getProfileUrl() != null && !currentUserData.getProfileUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(currentUserData.getProfileUrl())
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .into(profilePhotoImageView);
                    } else {
                        profilePhotoImageView.setImageResource(R.drawable.baseline_account_circle_24); // Default image
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void enterEditMode() {
        // Enable editing
        usernameEditText.setEnabled(true);
        bioEditText.setEnabled(true);
        occupationEditText.setEnabled(true);
        birthdayEditText.setEnabled(true);
        instagramEditText.setEnabled(true);

        // Show finish edit button
        finish_edit.setVisibility(View.VISIBLE);

        isEditMode = true;
    }

    private void exitEditMode() {
        // Disable editing
        usernameEditText.setEnabled(false);
        bioEditText.setEnabled(false);
        occupationEditText.setEnabled(false);
        birthdayEditText.setEnabled(false);
        instagramEditText.setEnabled(false);

        // Hide finish edit button
        finish_edit.setVisibility(View.GONE);

        isEditMode = false;
    }

    private void updateProfile() {
        // Get updated data
        String updatedUsername = usernameEditText.getText().toString().trim();
        String updatedBio = bioEditText.getText().toString().trim();
        String updatedOccupation = occupationEditText.getText().toString().trim();
        String updatedBirthday = birthdayEditText.getText().toString().trim();
        String updatedInstagramUrl = instagramEditText.getText().toString().trim();

        // Validate if fields are empty
        if (updatedUsername.isEmpty() || updatedBio.isEmpty() || updatedOccupation.isEmpty() ||
                updatedBirthday.isEmpty() || updatedInstagramUrl.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update data in Firebase
        if (currentUser != null && currentUserData != null) {
            currentUserData.setUserName(updatedUsername);
            currentUserData.setBio(updatedBio);
            currentUserData.setOccupation(updatedOccupation);
            currentUserData.setBirthday(updatedBirthday);
            currentUserData.setInstagramUrl(updatedInstagramUrl);

            // Update Firebase database
            usersReference.child(currentUser.getUid()).setValue(currentUserData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            exitEditMode(); // Exit edit mode after successful update
                        } else {
                            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
