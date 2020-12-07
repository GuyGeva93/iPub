package com.example.ipub;

import android.view.View;

public class Pub {

    private String Name;
    private String TitleName;
    private String Address;
    private double Lon;
    private double Lat;
    private double Distance;
    private String Kosher;
    private String Telephone;
    private String Website;
    private View.OnClickListener requestBtnClickListener;
    private View.OnClickListener btnGoToWebsite;
    private View.OnClickListener btnNavigateTopub;
    private View.OnClickListener btnAddToFavorites;
    private View.OnClickListener btnComments;



    private View.OnClickListener btnRatePub;
    private String Sunday;
    private String Monday;
    private String Tuesday;
    private String Wednesday;
    private String Thursday;
    private String Friday;
    private String Saturday;
    private String Area;
    private float Rating;

    public Pub() {
    }

    public Pub(String name, String titleName, String address, double lon, double lat, String kosher, String telephone, String website, String sunday,
               String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String area, float rating) {
        this.Name = name;
        this.TitleName = titleName;
        this.Address = address;
        this.Lon = lon;
        this.Lat = lat;
        this.Kosher = kosher;
        this.Telephone = telephone;
        this.Website = website;
        this.Sunday = sunday;
        this.Monday = monday;
        this.Tuesday = tuesday;
        this.Wednesday = wednesday;
        this.Thursday = thursday;
        this.Friday = friday;
        this.Saturday = saturday;
        this.Area = area;
        this.Rating = rating;
    }

    public String getMonday() {
        return Monday;
    }

    public void setMonday(String monday) {
        Monday = monday;
    }

    public String getTuesday() {
        return Tuesday;
    }

    public void setTuesday(String tuesday) {
        Tuesday = tuesday;
    }

    public String getWednesday() {
        return Wednesday;
    }

    public void setWednesday(String wednesday) {
        Wednesday = wednesday;
    }

    public String getThursday() {
        return Thursday;
    }

    public void setThursday(String thursday) {
        Thursday = thursday;
    }

    public String getFriday() {
        return Friday;
    }

    public void setFriday(String friday) {
        Friday = friday;
    }

    public String getSaturday() {
        return Saturday;
    }

    public void setSaturday(String saturday) {
        Saturday = saturday;
    }

    public String getSunday() {
        return Sunday;
    }

    public void setSunday(String sunday) {
        Sunday = sunday;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getKosher() {
        return Kosher;
    }

    public void setKosher(String kosher) {
        Kosher = kosher;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getTitleName() {
        return TitleName;
    }

    public void setTitleName(String titleName) {
        TitleName = titleName;
    }

    public double getLon() {
        return Lon;
    }

    public double getLat() {
        return Lat;
    }

    public void setRating(float rating) {
        this.Rating = rating;
    }

    public float getRating() {
        return Rating;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        this.Distance = distance;
    }

    public View.OnClickListener getRequestBtnClickListener() {
        return requestBtnClickListener;
    }

    public void setRequestBtnClickListener(View.OnClickListener requestBtnClickListener) {
        this.requestBtnClickListener = requestBtnClickListener;
    }

    public View.OnClickListener getBtnGoToWebsite() {
        return btnGoToWebsite;
    }

    public void setBtnGoToWebsite(View.OnClickListener btnGoToWebsite) {
        this.btnGoToWebsite = btnGoToWebsite;
    }

    public View.OnClickListener getBtnNavigateTopub() {
        return btnNavigateTopub;
    }

    public void setBtnNavigateTopub(View.OnClickListener btnNavigateTopub) {
        this.btnNavigateTopub = btnNavigateTopub;
    }

    public View.OnClickListener getBtnAddToFavorites() {
        return btnAddToFavorites;
    }

    public void setBtnAddToFavorites(View.OnClickListener btnAddToFavorites) {
        this.btnAddToFavorites = btnAddToFavorites;
    }

    public View.OnClickListener getBtnComments() {
        return btnComments;
    }

    public void setBtnComments(View.OnClickListener btnRating) {
        this.btnComments = btnRating;
    }

    public View.OnClickListener getBtnRatePub() {
        return btnRatePub;
    }

    public void setBtnRatePub(View.OnClickListener btnRatePub) {
        this.btnRatePub = btnRatePub;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }


}
