package com.example.plantnet.communitymain.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnet.R;
import com.example.plantnet.communitymain.recyclerview.CommunityAdapter;
import com.example.plantnet.model.Communities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment implements CommunityAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private List<Communities> communityList;
    private List<Communities> filteredCommunityList; // List untuk menyimpan hasil pencarian
    private DatabaseReference databaseReference;
    private DatabaseReference joinedCommunitiesRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> joinedCommunityIds;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.search_bar);

        // Inisialisasi list untuk data komunitas
        communityList = new ArrayList<>();
        filteredCommunityList = new ArrayList<>();

        // Inisialisasi adapter dengan data awal
        communityAdapter = new CommunityAdapter(getContext(), filteredCommunityList, this);
        recyclerView.setAdapter(communityAdapter);

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("forums");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            joinedCommunitiesRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("joinedCommunity");
        }

        joinedCommunityIds = new ArrayList<>();

        // Ambil data komunitas dari Firebase
        fetchCommunities();

        // Tambahkan listener untuk SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCommunities(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCommunities(newText);
                return false;
            }
        });

        return view;
    }

    // Metode untuk mengambil data komunitas dari Firebase
    private void fetchCommunities() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                communityList.clear();
                filteredCommunityList.clear(); // Kosongkan filteredCommunityList sebelum menambahkan data baru

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Communities community = dataSnapshot.getValue(Communities.class);
                    if (community != null) {
                        community.setId(dataSnapshot.getKey());
                        communityList.add(community);
                    }
                }

                filteredCommunityList.addAll(communityList); // Tambahkan semua komunitas ke filteredCommunityList
                checkJoinedCommunities(); // Cek komunitas yang diikuti
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("CommunityFragment", "Failed to fetch communities.", error.toException());
            }
        });
    }

    // Metode untuk mengecek komunitas yang diikuti oleh pengguna saat ini
    private void checkJoinedCommunities() {
        joinedCommunityIds.clear(); // Kosongkan daftar komunitas yang diikuti

        // Pastikan user terautentikasi sebelum mengambil data komunitas yang diikuti
        if (currentUser != null) {
            joinedCommunitiesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String communityId = dataSnapshot.getKey();
                        joinedCommunityIds.add(communityId); // Tambahkan ID komunitas yang diikuti ke dalam daftar
                    }
                    communityAdapter.notifyDataSetChanged(); // Perbarui adapter setelah memeriksa komunitas yang diikuti
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                    Log.e("CommunityFragment", "Failed to read joined communities.", error.toException());
                }
            });
        }
    }

    // Metode untuk menangani klik pada item komunitas
    @Override
    public void onItemClick(int position) {
        Communities clickedCommunity = filteredCommunityList.get(position);
        String communityId = clickedCommunity.getId();

        // Periksa apakah pengguna sudah bergabung dengan komunitas ini
        if (joinedCommunityIds.contains(communityId)) {
            Toast.makeText(requireContext(), "You have already joined this community.", Toast.LENGTH_SHORT).show();
        } else {
            // Update memberIds dan memberCount
            List<String> memberIds = new ArrayList<>(clickedCommunity.getMemberIds());
            memberIds.add(currentUser.getUid()); // Tambahkan ID pengguna saat ini ke memberIds

            // Update memberCount
            long updatedMemberCount = memberIds.size();

            // Update Firebase Database
            DatabaseReference communityRef = FirebaseDatabase.getInstance().getReference()
                    .child("forums").child(communityId);

            communityRef.child("memberIds").setValue(memberIds);
            communityRef.child("memberCount").setValue(updatedMemberCount)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Joined community successfully.", Toast.LENGTH_SHORT).show();
                            // Tambahkan komunitas yang diikuti ke dalam joinedCommunitiesRef
                            joinedCommunitiesRef.child(communityId).setValue(true);
                        } else {
                            Toast.makeText(requireContext(), "Failed to join community: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Metode untuk memfilter komunitas berdasarkan input pencarian
    private void filterCommunities(String query) {
        filteredCommunityList.clear();
        for (Communities community : communityList) {
            if (community.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredCommunityList.add(community);
            }
        }
        communityAdapter.notifyDataSetChanged(); // Perbarui adapter dengan hasil pencarian
    }
}
