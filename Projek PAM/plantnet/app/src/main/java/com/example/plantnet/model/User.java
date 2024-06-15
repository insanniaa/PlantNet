package com.example.plantnet.model;

public class User {
    private String userId;
    private String name; // New attribute
    private String userName;
    private String profileUrl;
    private String bio;
    private String occupation;
    private String instagramUrl;
    private String email;
    private String chatUrl;
    private String birthday;
    private String phoneNumber; // New attribute

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String name, String userName, String profileUrl, String bio, String occupation, String instagramUrl, String email, String chatUrl, String birthday, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.userName = userName;
        this.profileUrl = profileUrl;
        this.bio = bio;
        this.occupation = occupation;
        this.instagramUrl = instagramUrl;
        this.email = email;
        this.chatUrl = chatUrl;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
