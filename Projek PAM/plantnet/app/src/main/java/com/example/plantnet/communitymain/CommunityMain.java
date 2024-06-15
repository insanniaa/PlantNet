package com.example.plantnet.communitymain;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnet.R;
import com.example.plantnet.authentication.loginActivity;
import com.example.plantnet.community_detail.DetailCommunityActivity;
import com.example.plantnet.communitymain.fragment.CommunityFragment;
import com.example.plantnet.communitymain.fragment.HomeFragment;
import com.example.plantnet.communitymain.fragment.ProfileFragment;
import com.example.plantnet.communitymain.recyclerview.RecyclerviewAdapter;
import com.example.plantnet.model.Communities;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityMain extends AppCompatActivity{
    private RecyclerView recyclerview1;
    private Button btnAddCommunity, btnLogOut;
    private RecyclerviewAdapter communityAdapter;
    private List<Communities> communityList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            Intent intent = new Intent(CommunityMain.this, loginActivity.class);
            startActivity(intent);
            finish(); // Finish current activity to prevent returning to it
            return;
        }

        setSupportActionBar(findViewById(R.id.toolbar));

        bottomNavigationView  = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String title = getString(R.string.app_name);
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_community) {
                    selectedFragment = new CommunityFragment();
                    title = "Community";
                } else if (itemId == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                    title = "Profile";
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(title);
                    }
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btnAdd) {
            // Handle search icon click
            Intent intent = new Intent(CommunityMain.this, AddCommunityActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
