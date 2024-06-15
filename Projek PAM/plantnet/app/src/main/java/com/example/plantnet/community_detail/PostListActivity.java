package com.example.plantnet.community_detail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plantnet.R;
import com.example.plantnet.community_detail.recyclerView.PostListAdapter;
import com.example.plantnet.model.Posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostListAdapter adapter;
    private List<Posts> postList;
    private DatabaseReference postsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();

        // Retrieve data from the intent
        String communityId = getIntent().getStringExtra("community_id");

        // Initialize the adapter with context, postList, and communityId
        adapter = new PostListAdapter(this, postList, communityId);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        postsReference = FirebaseDatabase.getInstance().getReference().child("posts").child(communityId);

        fetchPosts();

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

    private void fetchPosts() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Posts post = dataSnapshot.getValue(Posts.class);
                    if (post != null) {
                        post.setPostId(dataSnapshot.getKey());
                        postList.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
