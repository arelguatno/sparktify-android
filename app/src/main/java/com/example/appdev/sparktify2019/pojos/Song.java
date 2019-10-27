package com.example.appdev.sparktify2019.pojos;



public class Song {
    private String songID;
    private String songTitle;

    public Song() {
    }

    public Song(String songID, String songTitle) {
        this.songID = songID;
        this.songTitle = songTitle;
    }

    public String getSongID() {
        return songID;
    }

    public String getSongTitle() {
        return songTitle;
    }
}
