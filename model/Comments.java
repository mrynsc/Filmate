package com.yeslabapps.fictionfocus.model;

public class Comments {
    private  String comment;
    private  String publisher;
    private  String PostId;
    private String CommentId;

    public Comments() {
    }

    public Comments(String comment, String publisher, String postId, String Commentid) {
        this.comment = comment;
        this.publisher = publisher;
        PostId = postId;
        this.CommentId = Commentid;
    }

    public String getCommentid() {
        return CommentId;
    }

    public void setCommentid(String commentid) {
        CommentId = commentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }
}
