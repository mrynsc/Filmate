package com.yeslabapps.fictionfocus.model;

public class Post {
    private  String PostId;
    private  String ImageUrl;
    private  String Description;
    private  String FilmName;
    private  String publisher;
    private  String Tag;
    private String QuoteType;

    public Post() {
    }



    public Post(String postId, String imageUrl, String description, String publisher, String filmName, String tag
            ,String quoteType) {
        PostId = postId;
        FilmName=filmName;
        Tag=tag;
        ImageUrl = imageUrl;
        Description = description;
        this.publisher = publisher;
        QuoteType=quoteType;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getFilmName(){
        return FilmName;
    }
    public void setFilmName(String filmName){
        FilmName=filmName;
    }



    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getTag(){
        return Tag;
    }
    public void setTag(String tag){
        Tag=tag;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQuoteType() {
        return QuoteType;
    }

    public void setQuoteType(String quoteType) {
        QuoteType = quoteType;
    }
}