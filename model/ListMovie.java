package com.yeslabapps.fictionfocus.model;

public class ListMovie {

    private String listMovieJsonObject;
    private String listMovieId;
    private String listMovieName;
    private String listMoviePubId;
    private String listMoviePoster;
    private String listOwnerId;


    public ListMovie(){

    }

    public ListMovie(String listMovieJsonObject,String listMovieId, String listMovieName, String listMoviePubId, String listMoviePoster,
    String listOwnerId) {
        this.listMovieId = listMovieId;
        this.listMovieName = listMovieName;
        this.listMoviePubId = listMoviePubId;
        this.listMoviePoster = listMoviePoster;
        this.listOwnerId = listOwnerId;
        this.listMovieJsonObject = listMovieJsonObject;

    }

    public String getListMovieJsonObject() {
        return listMovieJsonObject;
    }

    public void setListMovieJsonObject(String listMovieJsonObject) {
        this.listMovieJsonObject = listMovieJsonObject;
    }

    public String getListMovieId() {
        return listMovieId;
    }

    public void setListMovieId(String listMovieId) {
        this.listMovieId = listMovieId;
    }

    public String getListMovieName() {
        return listMovieName;
    }

    public void setListMovieName(String listMovieName) {
        this.listMovieName = listMovieName;
    }

    public String getListMoviePubId() {
        return listMoviePubId;
    }

    public void setListMoviePubId(String listMoviePubId) {
        this.listMoviePubId = listMoviePubId;
    }

    public String getListMoviePoster() {
        return listMoviePoster;
    }

    public void setListMoviePoster(String listMoviePoster) {
        this.listMoviePoster = listMoviePoster;
    }


    public String getListOwnerId() {
        return listOwnerId;
    }

    public void setListOwnerId(String listOwnerId) {
        this.listOwnerId = listOwnerId;
    }



}
