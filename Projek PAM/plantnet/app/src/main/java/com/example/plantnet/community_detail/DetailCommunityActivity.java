package com.example.plantnet.community_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.plantnet.R;
import com.example.plantnet.community_detail.recyclerView.PostsAdapter;
import com.example.plantnet.model.Communities;
import com.example.plantnet.model.Posts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

import java.util.ArrayList;
import java.util.List;

public class DetailCommunityActivity extends AppCompatActivity {
    private RecyclerView viewPost;
    private PostsAdapter adapter;
    private List<Posts> postsList;
    private DatabaseReference postsReference;
    private DatabaseReference communityReference;
    private View viewAbout;
    private TextView aboutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communitydetail);

        ImageView communityLogo = findViewById(R.id.CommunityLogo);
        ImageView communityBackground = findViewById(R.id.imagePoster);
        TextView textView = findViewById(R.id.communityName);
        TextView memberCount = findViewById(R.id.memberCount);
        aboutContent = findViewById(R.id.aboutContent); // Initialize aboutContent
        TabItem tabPosts = findViewById(R.id.tabPosts);
        TabItem tabAbout = findViewById(R.id.tabAbout);
        viewPost = findViewById(R.id.viewPost);
        viewAbout = findViewById(R.id.viewAbout);
        TabLayout tabLayout = findViewById(R.id.communityTab);
        ImageView postlist = findViewById(R.id.postlist);
        FloatingActionButton fabAddBtn = findViewById(R.id.fabAddPost);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout member_count = findViewById(R.id.member_count);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Retrieve data from the intent
        String communityId = getIntent().getStringExtra("community_id");

        // Check if communityId is null
        if (communityId == null) {
            throw new IllegalArgumentException("Community ID is missing");
        }

        // Initialize Firebase Database reference
        communityReference = FirebaseDatabase.getInstance().getReference().child("forums").child(communityId);
        postsReference = FirebaseDatabase.getInstance().getReference().child("posts").child(communityId);

        fetchCommunityDetails(communityLogo, communityBackground, textView, memberCount, toolbar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPost.setVisibility(View.VISIBLE);
                        viewAbout.setVisibility(View.GONE);
                        break;
                    case 1:
                        viewPost.setVisibility(View.GONE);
                        viewAbout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
                // Do nothing
            }
        });

        member_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCommunityActivity.this, MemberListActivity.class);
                intent.putExtra("community_id", communityId);
                startActivity(intent);
            }
        });

        postlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCommunityActivity.this, PostListActivity.class);
                intent.putExtra("community_id", communityId);
                startActivity(intent);
            }
        });


        viewPost.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        viewPost.setLayoutManager(layoutManager);

        postsList = new ArrayList<>();
        adapter = new PostsAdapter(this, postsList);
        adapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String postId) {
                Intent intent = new Intent(DetailCommunityActivity.this, PostDetailActivity.class);
                intent.putExtra("community_id", communityId);
                intent.putExtra("post_id", postId);
                startActivity(intent);
            }
        });
        viewPost.setAdapter(adapter);

        fetchPosts();
        fabAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open AddPostActivity
                Intent intent = new Intent(DetailCommunityActivity.this, UploadPostActivity.class);
                intent.putExtra("community_id", communityId);
                startActivity(intent);
            }
        });
    }

    private void fetchCommunityDetails(ImageView imageLogo,ImageView imageBackground, TextView textView, TextView memberCount, Toolbar toolbar) {
        communityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Communities community = snapshot.getValue(Communities.class);
                if (community != null) {
                    textView.setText(community.getTitle());
                    memberCount.setText(String.valueOf(community.getMemberCount()));
                    aboutContent.setText(community.getDetail());
                    Glide.with(DetailCommunityActivity.this).load(community.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(imageLogo);
                    Glide.with(DetailCommunityActivity.this)
                            .load(community.getImageUrl())
                            .transform(new CenterCrop(), new BlurTransformation(25, 3))
                            .into(imageBackground);

                    // Set the toolbar title to the community name
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(community.getTitle());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void fetchPosts() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Posts post = dataSnapshot.getValue(Posts.class);
                    if (post != null) {
                        post.setPostId(dataSnapshot.getKey());
                        postsList.add(post);
                    }
                }

                // Sort the postsList by timestamp in descending order
                postsList.sort((post1, post2) -> Long.compare(post2.getTimestamp(), post1.getTimestamp()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
