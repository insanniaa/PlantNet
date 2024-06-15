package com.example.plantnet.community_detail;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.example.plantnet.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MemberDetailActivity extends AppCompatActivity {

    private static final String TAG = "MemberDetailActivity";
    private DatabaseReference usersReference;

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView bioTextView;
    private TextView occupationTextView;
    private TextView birthdayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberdetail);

        // Initialize Firebase Database Reference
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        // Initialize views
        photoImageView = findViewById(R.id.detail_photo);
        nameTextView = findViewById(R.id.detail_name);
        usernameTextView = findViewById(R.id.detail_username);
        bioTextView = findViewById(R.id.detail_bio);
        occupationTextView = findViewById(R.id.detail_occupation);
        birthdayTextView = findViewById(R.id.detail_birthday);

        // Retrieve user_id from intent
        String userId = getIntent().getStringExtra("user_id");
        if (userId != null) {
            fetchUserDetails(userId);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User ID not found in intent extras");
            finish();
        }

        // Example of handling button clicks (add your logic here)
        ImageButton chatButton = findViewById(R.id.chat_button);
        ImageButton instagramButton = findViewById(R.id.instagram_button);
        ImageButton emailButton = findViewById(R.id.email_button);

        // Example of handling clicks (add your logic)
        chatButton.setOnClickListener(v -> {
            // Handle chat button click
        });

        instagramButton.setOnClickListener(v -> {
            // Handle Instagram button click
        });

        emailButton.setOnClickListener(v -> {
            // Handle email button click
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fetchUserDetails(String userId) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    updateUI(user);
                } else {
                    Toast.makeText(MemberDetailActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "User data not found for user ID: " + userId);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MemberDetailActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to fetch user data for user ID: " + userId, error.toException());
                finish();
            }
        });
    }

    private void updateUI(User user) {
        Glide.with(this).load(user.getProfileUrl()).into(photoImageView);
        nameTextView.setText(user.getUserName());
        usernameTextView.setText(user.getUserName());
        bioTextView.setText(user.getBio());
        occupationTextView.setText(user.getOccupation());
        birthdayTextView.setText(user.getBirthday());
    }

}
