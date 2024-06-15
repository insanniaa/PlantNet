package com.example.plantnet.community_detail;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadPostActivity extends AppCompatActivity {
    private ImageView imagePreview;
    private EditText description, title;
    private Button btnSubmitPost;
    private LinearLayout addPost;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            if (uri == null) {
                Toast.makeText(UploadPostActivity.this, "No image Selected", Toast.LENGTH_SHORT).show();
            } else {
                imageUri = uri;
                Glide.with(UploadPostActivity.this).load(imageUri).into(imagePreview);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        imagePreview = findViewById(R.id.uploadImageIcon);
        title = findViewById(R.id.input_nama);
        description = findViewById(R.id.input_deskripsi);
        addPost = findViewById(R.id.uploadImageButtonContainer);
        btnSubmitPost = findViewById(R.id.btnUploadPost);

        String communityId = getIntent().getStringExtra("community_id");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts").child(communityId);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("post_images");

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });
    }

    private void uploadPost() {
        String titleText = title.getText().toString().trim();
        String desc = description.getText().toString().trim();

        if (titleText.isEmpty() || desc.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Title, description, and image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String postId = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child(postId);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Map<String, Object> post = new HashMap<>();
                    post.put("title", titleText);
                    post.put("description", desc);
                    post.put("imageUrl", imageUrl);

                    databaseReference.child(postId).setValue(post)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UploadPostActivity.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(UploadPostActivity.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(UploadPostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }
}
