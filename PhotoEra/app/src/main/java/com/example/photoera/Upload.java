package com.example.photoera;

public class Upload {
    public String Name;
    public String Date;
    public double longitude;
    public double latitude;
    public String picture;

    public Upload(String Name, String Date, String picture, double longitude, double latitude){
        this.Name=Name;
        this.Date=Date;
        this.longitude=longitude;
        this.latitude=latitude;
        this.picture=picture;
    }

}
