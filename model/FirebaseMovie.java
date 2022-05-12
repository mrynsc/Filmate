package com.yeslabapps.fictionfocus.model;

public class FirebaseMovie {

    private  String moviePoster;
    private  String movieName;
    private  String postId;
    private  String userId;
    private String movieJsonObject;


    public FirebaseMovie(){

    }



    public FirebaseMovie(String moviePoster, String movieName, String postId,String userId
            ,String movieJsonObject) {
        this.moviePoster = moviePoster;
        this.movieName = movieName;
        this.postId = postId;
        this.userId = userId;
        this.movieJsonObject = movieJsonObject;


    }


    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieJsonObject() {
        return movieJsonObject;
    }

    public void setMovieJsonObject(String movieJsonObject) {
        this.movieJsonObject = movieJsonObject;
    }
}

