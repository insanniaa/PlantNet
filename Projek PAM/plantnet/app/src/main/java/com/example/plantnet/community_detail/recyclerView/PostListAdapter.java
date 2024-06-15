package com.example.plantnet.community_detail.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.example.plantnet.community_detail.PostDetailActivity;
import com.example.plantnet.model.Posts;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    private Context context;
    private List<Posts> postList;
    private DatabaseReference databaseReference;
    private String communityId; // Add communityId

    public PostListAdapter(Context context, List<Posts> postList, String communityId) { // Add communityId to constructor
        this.context = context;
        this.postList = postList;
        this.communityId = communityId; // Initialize communityId
        this.databaseReference = FirebaseDatabase.getInstance().getReference("posts");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.container_cardpost, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Posts post = postList.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.descriptionTextView.setText(post.getDescription());
        Glide.with(context).load(post.getImageUrl()).into(holder.imageView);

        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Posts post = postList.get(adapterPosition);
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("post_id", post.getPostId()); // Pass postId
                    intent.putExtra("community_id", communityId); // Pass communityId
                    context.startActivity(intent);
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    removePost(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void removePost(int position) {
        Posts post = postList.get(position);
        String postId = post.getPostId(); // Ensure that the Post class has a getId() method

        // Remove from Firebase Realtime Database
        databaseReference.child(postId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove from local list
                postList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, postList.size());
            } else {
                // Handle the failure, e.g., show a toast message
            }
        });
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView imageView;
        Button viewButton, deleteButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.card_nama);
            descriptionTextView = itemView.findViewById(R.id.card_deskripsi);
            imageView = itemView.findViewById(R.id.card_image);
            viewButton = itemView.findViewById(R.id.viewButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
