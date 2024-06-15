package com.example.plantnet.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.plantnet.R;
import com.example.plantnet.authentication.fragment.LandingPageFragment;
import com.example.plantnet.authentication.fragment.LoginFragment;
import com.example.plantnet.authentication.fragment.RegisterFragment;

public class AuthenticationActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Load default fragment on first launch
        if (savedInstanceState == null) {
            loadFragment(new LandingPageFragment());
        }
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);  // Optional: Add to back stack if needed
        transaction.commit();
    }

    // Method to show LoginFragment
    public void showLoginFragment() {
        loadFragment(new LoginFragment());
    }

    // Method to show RegisterFragment
    public void showRegisterFragment() {
        loadFragment(new RegisterFragment());
    }
}
