package com.example.plantnet.community_detail.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantnet.R;
import com.example.plantnet.community_detail.MemberDetailActivity;
import com.example.plantnet.model.User;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private Context context;
    private List<User> memberList;
    private List<User> originalList;

    public MemberAdapter(Context context, List<User> memberList) {
        this.context = context;
        this.memberList = memberList;
        this.originalList = new ArrayList<>(memberList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.container_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User member = memberList.get(position);
        holder.memberName.setText(member.getUserName());
        holder.memberBio.setText(member.getBio());
        Glide.with(holder.memberPhoto.getContext())
                .load(member.getProfileUrl())
                .circleCrop()
                .into(holder.memberPhoto);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MemberDetailActivity
                Intent intent = new Intent(context, MemberDetailActivity.class);
                intent.putExtra("user_id", member.getUserId()); // Pass user ID to MemberDetailActivity
                context.startActivity(intent);
            }
        });
        Log.d("MemberAdapter", "Binding user: " + member.getUserName());
    }

    public void filterList(List<User> filteredList) {
        memberList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberName;
        public TextView memberBio;
        public ImageView memberPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.contact_name);
            memberBio = itemView.findViewById(R.id.contact_info);
            memberPhoto = itemView.findViewById(R.id.contact_photo);
        }
    }
}
