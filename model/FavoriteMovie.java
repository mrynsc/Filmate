package com.yeslabapps.fictionfocus.model;

public class FavoriteMovie {

    private String favoriteUserName;
    private String favoriteUserImage;
    private String favoriteUserId;
    private String favoritePostId;
    private String favoriteMovieName;
    private String favoriteMoviePoster;
    private String favoriteUserCountry;
    private String favoriteJsonObject;


    public FavoriteMovie() {

    }


    public FavoriteMovie(String favoriteUserName, String favoriteUserImage, String favoriteUserId, String favoritePostId,
                         String favoriteMovieName, String favoriteMoviePoster, String favoriteUserCountry
    ,String favoriteJsonObject) {
        this.favoriteUserName = favoriteUserName;
        this.favoriteUserImage = favoriteUserImage;
        this.favoriteUserId = favoriteUserId;
        this.favoritePostId = favoritePostId;
        this.favoriteMovieName = favoriteMovieName;
        this.favoriteMoviePoster = favoriteMoviePoster;
        this.favoriteUserCountry = favoriteUserCountry;
        this.favoriteJsonObject = favoriteJsonObject;


    }


    public String getFavoriteUserName() {
        return favoriteUserName;
    }

    public void setFavoriteUserName(String favoriteUserName) {
        this.favoriteUserName = favoriteUserName;
    }

    public String getFavoriteUserImage() {
        return favoriteUserImage;
    }

    public void setFavoriteUserImage(String favoriteUserImage) {
        this.favoriteUserImage = favoriteUserImage;
    }

    public String getFavoriteUserId() {
        return favoriteUserId;
    }

    public void setFavoriteUserId(String favoriteUserId) {
        this.favoriteUserId = favoriteUserId;
    }

    public String getFavoritePostId() {
        return favoritePostId;
    }

    public void setFavoritePostId(String favoritePostId) {
        this.favoritePostId = favoritePostId;
    }

    public String getFavoriteMovieName() {
        return favoriteMovieName;
    }

    public void setFavoriteMovieName(String favoriteMovieName) {
        this.favoriteMovieName = favoriteMovieName;
    }

    public String getFavoriteMoviePoster() {
        return favoriteMoviePoster;
    }

    public void setFavoriteMoviePoster(String favoriteMoviePoster) {
        this.favoriteMoviePoster = favoriteMoviePoster;
    }

    public String getFavoriteUserCountry() {
        return favoriteUserCountry;
    }

    public void setFavoriteUserCountry(String favoriteUserCountry) {
        this.favoriteUserCountry = favoriteUserCountry;
    }

    public String getFavoriteJsonObject() {
        return favoriteJsonObject;
    }

    public void setFavoriteJsonObject(String favoriteJsonObject) {
        this.favoriteJsonObject = favoriteJsonObject;
    }


}

