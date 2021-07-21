package com.example.photoera;

public class Photo {
    public String pid;
    public String date;
    public String username;
    public double lat;
    public double lon;
    public String path;

    public Photo(String pid, String date, String username, double lat, double lon, String path) {
        this.pid = pid;
        this.date = date;
        this.username = username;
        this.lat = lat;
        this.lon = lon;
        this.path = path;
    }
}
