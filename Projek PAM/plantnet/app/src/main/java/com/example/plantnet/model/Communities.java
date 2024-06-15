package com.example.plantnet.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Communities {
    private String Id;
    private String title;
    private String detail;
    private String bio;
    private String creatorId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int memberCount;
    private int postCount;
    private String imageUrl;
    private List<String> memberIds;
    public Communities() {}

    public Communities(String Id, String title, String description, String creatorId, Timestamp createdAt, Timestamp updatedAt, int memberCount, int postCount, String imageUrl, List<String> memberIds) {
        this.Id = Id;
        this.title = title;
        this.detail = description;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberCount = memberCount;
        this.postCount = postCount;
        this.imageUrl = imageUrl;
        this.memberIds = memberIds;
    }

    // Getters and Setters
    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}
