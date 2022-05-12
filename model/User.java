package com.yeslabapps.fictionfocus.model;

public class User {
    private String Username;
    private String Email;
    private String Password;
    private String UserId;
    private String bio;
    private String ImageUrl;
    private String Country;
    private String Status;
    private String Prefer;



    public User() {
    }

    public User(String username, String email, String password, String userId, String bio, String imageUrl,
                String country,String status,String prefer) {
        Username = username;
        Email = email;
        Password = password;
        UserId = userId;
        this.bio = bio;
        ImageUrl = imageUrl;
        Country=country;
        Status=status;
        Prefer=prefer;

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPrefer() {
        return Prefer;
    }

    public void setPrefer(String prefer) {
        Prefer = prefer;
    }


}
