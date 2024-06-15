package com.example.plantnet.community_detail;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.plantnet.R;
import com.example.plantnet.model.Posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;

public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetail";
    private static final int REQUEST_PERMISSIONS = 1001;
    private ImageView postImage;
    private TextView postDetailDescription, postTitle;
    private Button downloadButton;
    private DatabaseReference postReference;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        postImage = findViewById(R.id.detail_image);
        postTitle = findViewById(R.id.detail_title);
        postDetailDescription = findViewById(R.id.detail_description);
        downloadButton = findViewById(R.id.buttonDownload);

        // Retrieve the post ID from the intent
        String postId = getIntent().getStringExtra("post_id");
        String communityId = getIntent().getStringExtra("community_id");

        if (postId == null) {
            Toast.makeText(this, "Post ID is missing", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Post ID is missing");
            finish(); // Close the activity if postId is missing
            return;
        }

        // Log the postId for debugging
        Log.d(TAG, "Received post ID: " + postId);

        // Initialize Firebase Database reference
        postReference = FirebaseDatabase.getInstance().getReference().child("posts").child(communityId).child(postId);

        // Fetch post details
        fetchPostDetails();

        downloadButton.setOnClickListener(v -> {
            if (imageUrl != null) {
                checkPermissionsAndDownloadImage(imageUrl);
            } else {
                Toast.makeText(PostDetailActivity.this, "No image URL available", Toast.LENGTH_SHORT).show();
            }
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

    private void fetchPostDetails() {
        postReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Posts post = snapshot.getValue(Posts.class);
                if (post != null) {
                    Log.d(TAG, "Post details: " + post.getDescription() + ", " + post.getImageUrl());
                    postDetailDescription.setText(post.getDescription());
                    postTitle.setText(post.getTitle());
                    imageUrl = post.getImageUrl();
                    Glide.with(PostDetailActivity.this).load(imageUrl).into(postImage);
                } else {
                    Log.e(TAG, "Post data is null");
                    Toast.makeText(PostDetailActivity.this, "Failed to retrieve post details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch post details: " + error.getMessage());
                Toast.makeText(PostDetailActivity.this, "Failed to fetch post details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionsAndDownloadImage(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                }, REQUEST_PERMISSIONS);
            } else {
                downloadImage(url);
            }
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        } else {
            downloadImage(url);
        }
    }

    private void downloadImage(String url) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImageToGallery(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup if needed
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(PostDetailActivity.this, "Image downloaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PostDetailActivity.this, "Failed to download image", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PostDetailActivity.this, "Failed to download image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (imageUrl != null) {
                    downloadImage(imageUrl);
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
