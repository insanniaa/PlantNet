package com.example.plantnet.communitymain.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plantnet.R;
import com.example.plantnet.authentication.AuthenticationActivity;
import com.example.plantnet.community_detail.DetailCommunityActivity;
import com.example.plantnet.communitymain.recyclerview.RecyclerviewAdapter;
import com.example.plantnet.communitymain.recyclerview.TrendingAdapter;
import com.example.plantnet.model.Communities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecyclerviewAdapter.OnItemClickListener, TrendingAdapter.OnItemClickListener {
    private RecyclerView recyclerview1, recyclerview2;
    private Button btnLogOut;
    private RecyclerviewAdapter communityAdapter;
    private TrendingAdapter trendingAdapter;
    private List<Communities> communityList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnLogOut = view.findViewById(R.id.btnLogOut);
        recyclerview1 = view.findViewById(R.id.recViewCommunityLogo);
        recyclerview2 = view.findViewById(R.id.recyclerTrending);

        communityList = new ArrayList<>();

        communityAdapter = new RecyclerviewAdapter(getContext(), communityList, this);
        trendingAdapter = new TrendingAdapter(getContext(), communityList, this);

        recyclerview1.setAdapter(communityAdapter);
        recyclerview1.setLayoutManager(new GridLayoutManager(getContext(), 3));

        recyclerview2.setAdapter(trendingAdapter);
        recyclerview2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("forums");

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        fetchCommunities();

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Communities selectedCommunity = communityList.get(position);
        Intent intent = new Intent(getActivity(), DetailCommunityActivity.class);
        intent.putExtra("community_id", selectedCommunity.getId());
        intent.putExtra("community_name", selectedCommunity.getTitle());
        intent.putExtra("image_url", selectedCommunity.getImageUrl());
        startActivity(intent);
    }

    private void fetchCommunities() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                communityList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Communities community = dataSnapshot.getValue(Communities.class);
                    if (community != null) {
                        community.setId(dataSnapshot.getKey());
                        communityList.add(community);
                    }
                }

                communityAdapter.notifyDataSetChanged(); // Perbarui adapter setelah mengisi communityList dengan data baru
                trendingAdapter.notifyDataSetChanged(); // Perbarui adapter trendingAdapter juga jika perlu
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void logoutUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
