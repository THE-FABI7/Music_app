package com.example.applemusic.Models;

public class Result {

    private String trackName;
    private String artistName;
    private String previewUrl;
    private String artworkUrl100;
    private long trackTimeMillis;

    // Constructor
    public Result(String trackName, String artistName, String previewUrl, String artworkUrl100, long trackTimeMillis) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.previewUrl = previewUrl;
        this.artworkUrl100 = artworkUrl100;
        this.trackTimeMillis = trackTimeMillis;
    }

    // Getters
    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public long getTrackTimeMillis() {
        return trackTimeMillis;
    }

    // Methods to generate local filenames for preview and artwork
    public String getLocalPreviewFilename() {
        return trackName.replaceAll("\\s+", "_").toLowerCase() + "_preview.mp3";
    }

    public String getLocalArtworkFilename() {
        return trackName.replaceAll("\\s+", "_").toLowerCase() + "_artwork.jpg";
    }
}
