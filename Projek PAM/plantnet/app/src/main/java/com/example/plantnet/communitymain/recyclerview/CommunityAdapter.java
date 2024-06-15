package com.example.plantnet.communitymain.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.plantnet.R;
import com.example.plantnet.model.Communities;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    private Context context;
    private List<Communities> communityList;
    private OnItemClickListener onItemClickListener;

    public CommunityAdapter(Context context, List<Communities> communityList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.communityList = communityList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Communities community = communityList.get(position);
        holder.communityTitle.setText(community.getTitle());
        holder.communityBio.setText(community.getDetail());
        Glide.with(context)
                .load(community.getImageUrl())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_foreground)) // Placeholder if needed
                .into(holder.communityImage);

        holder.buttonJoin.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }


    public class CommunityViewHolder extends RecyclerView.ViewHolder {

        TextView communityTitle, communityBio;
        ImageView communityImage;
        Button buttonJoin;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            communityTitle = itemView.findViewById(R.id.community_name);
            communityBio = itemView.findViewById(R.id.community_bio);
            communityImage = itemView.findViewById(R.id.community_image);
            buttonJoin = itemView.findViewById(R.id.btn_join);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
