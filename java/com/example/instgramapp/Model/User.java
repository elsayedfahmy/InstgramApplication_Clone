package com.example.instgramapp.Model;

public class User {
    private String Id;
    private String username;
    private String fullname;
    private String bio;
    private String ImageUrl;

    public User() {
    }

    public User(String Id,String bio, String fullname ,String ImageUrl ,String username ) {
        this.Id = Id;
        this.username = username;
        this.fullname = fullname;
        this.bio = bio;
        this.ImageUrl = ImageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }




}
