package com.example.plantnet.model;

public class Posts {
    private String postId;
    private String description;
    private String imageUrl;
    private String title;
    private long timestamp; // Add timestamp field

    public Posts() {}

    public Posts(String postId, String title, String description, String imageUrl, long timestamp) {
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp; // Initialize timestamp
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
