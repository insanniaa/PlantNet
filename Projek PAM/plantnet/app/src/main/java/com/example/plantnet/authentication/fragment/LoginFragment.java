package com.example.plantnet.authentication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plantnet.R;
import com.example.plantnet.communitymain.CommunityMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPass;
    private Button btLogin;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etEmail = view.findViewById(R.id.et_email);
        etPass = view.findViewById(R.id.et_password);
        btLogin = view.findViewById(R.id.bt_login);

        // Set click listener for login button
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(etEmail.getText().toString(), etPass.getText().toString());
            }
        });

        return view;
    }

    private void login(String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(getActivity(), "Login successful: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                updateUI(user);
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
