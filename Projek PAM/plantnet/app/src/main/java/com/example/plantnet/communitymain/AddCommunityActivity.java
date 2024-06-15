package com.example.plantnet.communitymain;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.example.plantnet.model.Communities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCommunityActivity extends AppCompatActivity {
    ImageView communityProfile;
    FrameLayout addLogo;
    Button btnCreateCommunity;
    EditText inputNama, inputBio, inputDetail;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            if (uri == null) {
                Toast.makeText(AddCommunityActivity.this, "No image Selected", Toast.LENGTH_SHORT).show();
            } else {
                imageUri = uri;
                Glide.with(getApplicationContext()).load(uri).into(communityProfile);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcommunity);

        communityProfile = findViewById(R.id.imageLogoCommunity);
        addLogo = findViewById(R.id.addLogo);
        btnCreateCommunity = findViewById(R.id.btnCreateCommunity);
        inputNama = findViewById(R.id.inputNama);
        inputBio = findViewById(R.id.inputBio);
        inputDetail = findViewById(R.id.inputDetail);


        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        addLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView communityProfile = addLogo.findViewById(R.id.imageLogoCommunity);

                // Mengubah properti scaleType ImageView menjadi centerCrop
                communityProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);

                launcher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btnCreateCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommunity();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Kembali ke Activity sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addCommunity() {
        String name = inputNama.getText().toString().trim();
        String bio = inputBio.getText().toString().trim();
        String detail = inputDetail.getText().toString().trim();
        String creatorId = mAuth.getCurrentUser().getUid(); // Get current user ID

        if (name.isEmpty() || bio.isEmpty() || detail.isEmpty() || imageUri == null) {
            Toast.makeText(this, "All fields and image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference communityImagesRef = storageRef.child("community_images/" + UUID.randomUUID().toString());

        communityImagesRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> communityImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();

            String communityId = databaseReference.child("forums").push().getKey();

            Map<String, Object> community = new HashMap<>();
            community.put("title", name);
            community.put("communityId", communityId);
            community.put("description", bio);
            community.put("detail", detail);
            community.put("creatorId", creatorId);
            community.put("memberCount", 1);
            community.put("postCount", 0);
            community.put("imageUrl", imageUrl);
            community.put("memberIds", Arrays.asList(creatorId));

            if (communityId != null) {
                databaseReference.child("forums").child(communityId).setValue(community)
                        .addOnSuccessListener(aVoid -> {
                            // Add community ID to joinedCommunity of current user
                            databaseReference.child("users").child(creatorId).child("joinedCommunity").child(communityId).setValue(true)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(AddCommunityActivity.this, "Community created successfully!", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddCommunityActivity.this, "Failed to add community to user's joined list", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddCommunityActivity.this, "Failed to create community", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(AddCommunityActivity.this, "Failed to generate community ID", Toast.LENGTH_SHORT).show();
            }
        })).addOnFailureListener(e -> {
            Toast.makeText(AddCommunityActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

}
