package com.example.applemusic.Datasource;

import com.example.applemusic.Models.Result;
import com.example.applemusic.Models.Root;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLRequest;
import cafsoft.foundation.URLSession;


public class ApleeMusicServiceApi  extends RemoteDataSource{

    private final String BASE_URL = "https://itunes.apple.com/search";

    public ApleeMusicServiceApi(){

    }

    public URL fileToURL(File file){
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    public URL localPathToURL(String localPath){
        return fileToURL(new File(localPath));
    }
    public void requestSongsByTerm(String searchTerm, int limit,
                                          TextCompletionHandler textCompletionHandler,
                                          ErrorCodeCompletionHandler errorCodeCompletion) {
        URLComponents components = new URLComponents(BASE_URL);
        components.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("media", "music"),
                new URLQueryItem("entity", "song"),
                new URLQueryItem("limit", String.valueOf(limit)),
                new URLQueryItem("term", searchTerm)
        });
        // Generate the URL from the components
        URL url = components.getURL();
        // Create a URLRequest
        URLRequest request = new URLRequest(url);
        // Get Default URLSession
        URLSession session = URLSession.getShared();
        requestText(request, session, (text) -> {
            textCompletionHandler.run(text);
        }, errorCode -> {
            errorCodeCompletion.run(errorCode);
        });
    }
    public void downloadPreviewTrack(Result result,
                                     String basePath,
                                     URLCompletionHandler urlCompletionHandler,
                                     ErrorCodeCompletionHandler errorCodeCompletion){
        String fullFilename = basePath + "/" + result.getLocalPreviewFilename();
        File newFile = new File(fullFilename);
        URL newFileURL = fileToURL(newFile);
        if (newFile.exists()) {
            urlCompletionHandler.run(newFileURL);
            return;
        }
        URLComponents components = new URLComponents(result.getPreviewUrl());
        URL url = components.getURL();
        // Create a URLRequest
        URLRequest request = new URLRequest(url);
        // Get Default URLSession
        URLSession session = URLSession.getShared();
        downloadFile(request, session, localURL -> {
            File tempFile = new File(localURL.getFile());
            tempFile.renameTo(newFile);
            if (urlCompletionHandler != null){
                urlCompletionHandler.run(newFileURL);
            }
        }, errorCode -> {
            errorCodeCompletion.run(errorCode);
        });
    }
    public void downloadArtworkTrack(Result result,
                                     String basePath,
                                     URLCompletionHandler urlCompletionHandler,
                                     ErrorCodeCompletionHandler errorCodeCompletion){
        String fullFilename = basePath + "/" + result.getLocalArtworkFilename();
        File newFile = new File(fullFilename);
        URL newFileURL = fileToURL(newFile);
        if (newFile.exists()) {
            urlCompletionHandler.run(newFileURL);
            return;
        }
        URLComponents components = new URLComponents(result.getArtworkUrl100());
        URL url = components.getURL();
        // Create a URLRequest
        URLRequest request = new URLRequest(url);
        // Get Default URLSession
        URLSession session = URLSession.getShared();
        downloadFile(request, session, localURL -> {
            File tempFile = new File(localURL.getFile());
            tempFile.renameTo(newFile);
            if (urlCompletionHandler != null){
                urlCompletionHandler.run(newFileURL);
            }
        }, errorCode -> {
            errorCodeCompletion.run(errorCode);
        });
    }
    public void downloadAllArtworkTracks(Root root,
                                         String basePath,
                                         URLCompletionHandler urlCompletionHandler){
        if (root == null)
            return;
        for (Result result : root.getResults()){
            String fullFilename = basePath + "/" + result.getLocalArtworkFilename();
            if (!new File(fullFilename).exists()) {
                downloadArtworkTrack(result, basePath, (localURL) -> {
                    urlCompletionHandler.run(localURL);
                },errorCode -> {
                });
            }
        }
    }





}
