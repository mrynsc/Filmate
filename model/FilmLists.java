package com.yeslabapps.fictionfocus.model;

public class FilmLists {

    private String listTitle;
    private String listDesc;
    private String listId;
    private String listPublisher;
    private String listType;


    public FilmLists(){

    }


    public FilmLists(String listTitle, String listDesc, String listId, String listPublisher,String listType) {
        this.listTitle = listTitle;
        this.listDesc = listDesc;
        this.listId = listId;
        this.listPublisher = listPublisher;
        this.listType = listType;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public String getListDesc() {
        return listDesc;
    }

    public void setListDesc(String listDesc) {
        this.listDesc = listDesc;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListPublisher() {
        return listPublisher;
    }

    public void setListPublisher(String listPublisher) {
        this.listPublisher = listPublisher;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }
}



