package com.yeslabapps.fictionfocus.model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private boolean isPost;
    private  String NotificationId;
    private String timeStamp;

    public Notification() {
    }

    public Notification(String userid, String text, String postid, boolean isPost,String NotificationId,String timeStamp) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.isPost = isPost;
        this.NotificationId = NotificationId;
        this.timeStamp=timeStamp;
    }

    public String getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(String notificationId) {
        NotificationId = notificationId;
    }

    public String getUserid() {
        return userid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setIsPost(boolean post) {
        isPost = post;
    }
}