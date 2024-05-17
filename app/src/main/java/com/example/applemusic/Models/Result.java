package com.example.applemusic.Models;

public class Result {
    private long collectionId = 0;
    private long trackId = 0;
    private String artistName = "";
    private String trackName = "";
    private String previewUrl = "";
    private String artworkUrl100 = "";

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    public String getLocalPreviewFilename() {
        return getTrackId() + ".m4a.tmp";
    }

    public String getLocalArtworkFilename() {
        return getCollectionId() + ".jpg.tmp";
    }
}

