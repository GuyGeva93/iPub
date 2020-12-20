package com.example.ipub;


import android.view.View;

/*comment class for the rating comments.*/

public class CommentInfo {
    private String name;
    private String comment;
    private float rating;
    private long timeStamp;
    private View.OnClickListener btnDeleteComment;

    public CommentInfo() {
    }

    public CommentInfo(String name, String comment, float rating, long timeStamp) {
        this.name = name;
        this.comment = comment;
        this.rating = rating;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public View.OnClickListener getBtnDeleteComment() {
        return btnDeleteComment;
    }

    public void setBtnDeleteComment(View.OnClickListener btnDeleteComment) {
        this.btnDeleteComment = btnDeleteComment;
    }
}
