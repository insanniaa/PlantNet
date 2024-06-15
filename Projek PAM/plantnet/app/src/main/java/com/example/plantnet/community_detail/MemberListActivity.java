package com.example.plantnet.community_detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plantnet.R;
import com.example.plantnet.community_detail.recyclerView.MemberAdapter;
import com.example.plantnet.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MemberListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MemberAdapter memberAdapter;
    private List<User> memberList;
    private DatabaseReference communityReference;
    private DatabaseReference usersReference;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberlist);

        recyclerView = findViewById(R.id.recycle_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        memberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(this, memberList);
        recyclerView.setAdapter(memberAdapter);

        String communityId = getIntent().getStringExtra("community_id");
        if (communityId == null) {
            Toast.makeText(this, "Community ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        communityReference = FirebaseDatabase.getInstance().getReference().child("forums").child(communityId).child("memberIds");
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMembers(newText.toLowerCase().trim());
                return true;
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

        fetchMembers();

        Log.d("MemberListActivity", "Community ID: " + communityId);
        Log.d("MemberListActivity", "Users Reference: " + usersReference.toString());
    }

    private void fetchMembers() {
        communityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Assuming dataSnapshot is a child under 'memberIds'
                    String memberId = dataSnapshot.getValue(String.class); // Get memberId
                    if (memberId != null) {
                        usersReference.child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    memberList.add(user);
                                    memberAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d("MemberListActivity", "User data is null for ID: " + memberId);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("MemberListActivity", "Error fetching user data for ID: " + memberId, error.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MemberListActivity", "Error fetching community members", error.toException());
            }
        });
    }

    private void filterMembers(String searchText) {
        List<User> filteredList = new ArrayList<>();
        if (TextUtils.isEmpty(searchText)) {
            filteredList.addAll(memberList); // Tampilkan semua anggota jika pencarian kosong
        } else {
            for (User user : memberList) {
                // Sesuaikan dengan kriteria pencarian yang diinginkan, misalnya berdasarkan username atau nama lengkap
                if (user.getUserName().toLowerCase().contains(searchText) ||
                        user.getUserName().toLowerCase().contains(searchText)) {
                    filteredList.add(user);
                }
            }
        }
        memberAdapter.filterList(filteredList); // Update adapter dengan daftar yang sudah difilter
    }

}
