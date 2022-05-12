package com.yeslabapps.fictionfocus.model;

public class Movie {
    private String moviePosterResource; //URI for movie poster
    private String movieTitle;
    private String jsonObject;  //JSON data for specific movie

    public Movie(){

    }


    public Movie(String moviePosterResource, String movieTitle, String jsonObject) {
        this.moviePosterResource = moviePosterResource;
        this.movieTitle = movieTitle;
        this.jsonObject = jsonObject;


    }



    public String getMoviePosterResource() {
        return moviePosterResource;
    }

    public String getMovieTitle() {
        return movieTitle;
    }


    public String getJsonObject() {
        return jsonObject;
    }


}
