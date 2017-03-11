package com.example.sangt.find_spots;

import android.location.Location;

/**
 * Created by Alex on 3/10/2017.
 */

public class Photo {

    //Location is a Google Maps Class. We can change this if need be
    private Location location;

    //Only gets assigned when sending to database (Will also be used to find the picture in Storage)
    private String id;
    private String creationDate;
    private String expirationDate;
    private String comment;
    private String creator;

    public Photo(Location location, String creationDate, String expirationDate, String comment, String creator){
        setLocation(location);
        setCreationDate(creationDate);
        setExpirationDate(expirationDate);
        setComment(comment);
        setCreator(creator);
    }



    //---------These are just getters and setters for the properties above -------------
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

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
    //---------------------------------------------------------------------


}
