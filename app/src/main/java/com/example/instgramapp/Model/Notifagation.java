package com.example.instgramapp.Model;

public class Notifagation {
    private String userid;
    private String text;
    private String postid;
    private String ispost;
    public Notifagation()
    {

    }
   /* public Notifagation( String postid, String text,String userid,boolean ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
    }*/
    public Notifagation(String ispost, String postid, String text,String userid) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
    }

    public String getUserid() {
        return userid;
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

    public String isIspost() {
        return ispost;
    }

    public void setIspost(String ispost) {
        this.ispost = ispost;
    }
}
