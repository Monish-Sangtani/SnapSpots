package com.example.sangt.find_spots;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alex on 3/10/2017.
 */

public class Photo implements Parcelable {

    //Location is a Google Maps Class. We can change this if need be
    private double lat;
    private double lon;

    //Only gets assigned when sending to database (Will also be used to find the picture in Storage)
    private String id;
    private String creationDate;
    private String expirationDate;
    private String comment;
    private String creator;
    private String uri;

    public Photo(){

    }

    public Photo(Location location, String creationDate, String expirationDate, String comment, String creator){
        setLat(location.getLatitude());
        setLon(location.getLongitude());
        setCreationDate(creationDate);
        setExpirationDate(expirationDate);
        setComment(comment);
        setCreator(creator);

    }



    //---------These are just getters and setters for the properties above -------------
    public double getLon() { return lon; }

    public void setLon(double lon) { this.lon = lon; }

    public double getLat() { return lat; }

    public void setLat(double lat) { this.lat = lat; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    //---------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(creationDate);
        dest.writeString(expirationDate);
        dest.writeString(comment);
        dest.writeString(creator);
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>(){
        public Photo createFromParcel(Parcel in){
            return new Photo(in);
        }

        public Photo[] newArray(int size){
            return new Photo[size];
        }
    };

    private Photo(Parcel in){
        lat = in.readDouble();
        lon = in.readDouble();
        creationDate = in.readString();
        expirationDate = in.readString();
        comment = in.readString();
        creator = in.readString();
    }


}
