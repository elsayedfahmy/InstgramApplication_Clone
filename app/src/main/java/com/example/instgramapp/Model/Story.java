package com.example.instgramapp.Model;

public class Story {
   private String imagUrL;
   private long starttime;
   private long endtime;
   private String userid;
   private String stroryid;

    public Story() {
    }

    public Story(String imagUrL, long starttime, long endtime, String stroryid, String userid) {
        this.imagUrL = imagUrL;
        this.starttime = starttime;
        this.endtime = endtime;
        this.userid = userid;
        this.stroryid = stroryid;
    }

    public String getImagUrL() {
        return imagUrL;
    }

    public void setImagUrL(String imagUrL) {
        this.imagUrL = imagUrL;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStroryid() {
        return stroryid;
    }

    public void setStroryid(String stroryid) {
        this.stroryid = stroryid;
    }
}
