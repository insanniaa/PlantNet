package com.example.plantnet.community_detail.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.example.plantnet.model.Posts;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private List<Posts> posts;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String postId);
    }

    public PostsAdapter(Context context, List<Posts> posts) {
        this.posts = posts;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_community_detail_list_item, parent, false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        Posts post = posts.get(position);
        Glide.with(context).load(post.getImageUrl()).into(holder.postView);

        holder.postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    String postId = post.getPostId();
                    onItemClickListener.onItemClick(postId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView postView;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            postView = itemView.findViewById(R.id.postView);
        }
    }
}
