package com.example.plantnet.communitymain.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.plantnet.R;
import com.example.plantnet.model.Communities;

import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.CommunityViewHolder> {
    private Context context;
    private List<Communities> communityList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public RecyclerviewAdapter(Context context, List<Communities> communityList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.communityList = communityList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recview_community_list_item, parent, false);
        return new CommunityViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Communities community = communityList.get(position);
        holder.communityTitle.setText(community.getTitle());
        Glide.with(context)
                .load(community.getImageUrl())
                .apply(RequestOptions.placeholderOf(R.drawable.person))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.communityImage);
    }

    @Override
    public int getItemCount() {
        return communityList.size();
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView communityTitle;
        ImageView communityImage;
        OnItemClickListener onItemClickListener;

        public CommunityViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            communityTitle = itemView.findViewById(R.id.CommunityName);
            communityImage = itemView.findViewById(R.id.CommunityLogo);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
