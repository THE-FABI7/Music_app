package com.example.applemusic.Datasource;

import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;


public class ApleeMusicServiceApi {

    private URLComponents components = null;

    public ApleeMusicServiceApi() {
        components = new URLComponents();
        components.setScheme("https");
        components.setHost("itunes.apple.com");
        components.setPath("/search");
    }

    public void searchSongsByTerm(String searchTerm) {
        components.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("media", "music"),
                new URLQueryItem("entity", "song"),
                new URLQueryItem("term", searchTerm)
        });
    }






}
