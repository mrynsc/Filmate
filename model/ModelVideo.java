package com.yeslabapps.fictionfocus.model;

public class ModelVideo {
    String id,title,timestamp,videoUrl,userId,videoDesc,videoType,thumbnail;



    public ModelVideo() {
        //firebase requirement
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUserId(){return userId;}

    public void setUserId(String userId){this.userId=userId;}

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoDesc(){return videoDesc;}

    public void setVideoDesc(String videoDesc){this.videoDesc=videoDesc;}

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
