package com.example.plantnet.authentication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class loginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail, etPass;
    private Uri imageUri;
    private ImageView userProfile;
    private Button btnMasuk;
    private Button btnDaftar;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<PickVisualMediaRequest> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_pass);
        btnMasuk = findViewById(R.id.btn_masuk);
        btnDaftar = findViewById(R.id.btn_daftar);
        userProfile = findViewById(R.id.userProfile);

        mAuth = FirebaseAuth.getInstance();

        btnMasuk.setOnClickListener(this);
        btnDaftar.setOnClickListener(this);

        launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri == null) {
                    Toast.makeText(loginActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                } else {
                    imageUri = uri;
                    Glide.with(getApplicationContext()).load(uri).into(userProfile);
                }
            }
        });

        // Set an OnClickListener to the userProfile ImageView to open the image picker
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new PickVisualMediaRequest.Builder().build());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_masuk) {
            login(etEmail.getText().toString(), etPass.getText().toString());
        } else if (id == R.id.btn_daftar) {
            signUp(etEmail.getText().toString(), etPass.getText().toString());
        }
    }

    private void signUp(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                            Toast.makeText(loginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadProfilePicture(FirebaseUser user) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_pictures/" + user.getUid() + ".jpg");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Profile picture uploaded successfully: " + uri.toString());
                    saveUserInfoToDatabase(user, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to upload profile picture", e);
                    Toast.makeText(loginActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserInfoToDatabase(FirebaseUser user, String profilePhotoUrl) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = user.getUid(); // Get user ID from FirebaseUser
        String username = "YourUsername"; // Retrieve this from an EditText if you have one
        String name = "";
        String bio = ""; // Default value or retrieve from input
        String occupation = ""; // Default value or retrieve from input
        String instagramUrl = ""; // Default value or retrieve from input
        String email = user.getEmail(); // Or retrieve from input
        String chatUrl = ""; // Default value or retrieve from input
        String birthday = "";
        String phoneNumber = "";// Default value or retrieve from input

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


    public void login(String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(loginActivity.this, "Login successful: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                updateUI(user);
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(loginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Required");
            result = false;
        } else {
            etEmail.setError(null);
        }
        if (TextUtils.isEmpty(etPass.getText().toString())) {
            etPass.setError("Required");
            result = false;
        } else {
            etPass.setError(null);
        }
        return result;
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(loginActivity.this, CommunityMain.class);
            startActivity(intent);
        } else {
            Toast.makeText(loginActivity.this, "Log In First", Toast.LENGTH_SHORT).show();
        }
    }
}
