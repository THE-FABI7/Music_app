package com.example.applemusic.Datasource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;

import cafsoft.foundation.Data;
import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLRequest;
import cafsoft.foundation.URLSession;
import cafsoft.foundation.URLSessionDataTask;
import cafsoft.foundation.URLSessionDownloadTask;


public class RemoteDataSource {


    public URLSessionDataTask createDataTask(URLRequest request,
                                              URLSession session,
                                              DataCompletionHandler dataCompletionHandler,
                                              ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        URLSessionDataTask task = session.dataTask(request, (data, response, error) -> {
            // Handle general errors
            if (error != null) {
                // Network error
                errorCodeCompletionHandler.run(-1);
                return;
            }

            if (response instanceof HTTPURLResponse) {
                HTTPURLResponse httpResponse = (HTTPURLResponse) response;
                int statusCode = httpResponse.getStatusCode();

                if (statusCode == 200)
                    dataCompletionHandler.run(data);
                else
                    errorCodeCompletionHandler.run(statusCode);
            }
        });

        return task;
    }

    private URLSessionDownloadTask creteDownloadTask(URLRequest request,
                                                     URLSession session,
                                                     URLCompletionHandler urlCompletionHandler,
                                                     ErrorCodeCompletionHandler errorCodeCompletionHandler) {


        URLSessionDownloadTask task = session.downloadTask(request, (localURL, response, error) -> {
            // Handle general errors
            if (error != null) {
                // Network error
                errorCodeCompletionHandler.run(-1);
                return;
            }

            if (response instanceof HTTPURLResponse) {
                HTTPURLResponse httpResponse = (HTTPURLResponse) response;
                int statusCode = httpResponse.getStatusCode();

                if (statusCode == 200)
                    urlCompletionHandler.run(localURL);
                else
                    errorCodeCompletionHandler.run(statusCode);
            }
        });

        return task;
    }

    public void requestData(URLRequest request,
                             URLSession session,
                             DataCompletionHandler dataCompletionHandler,
                             ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        // Create a network task for the request
        URLSessionDataTask task = createDataTask(request, session, (data) -> {
            dataCompletionHandler.run(data);
        }, (errorCode) -> {
            errorCodeCompletionHandler.run(errorCode);
        });

        // Start the task
        task.resume();
    }

    public void requestText(URLRequest request,
                            URLSession session,
                            TextCompletionHandler textCompletionHandler,
                            ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        requestData(request, session, (data) -> {
            String text = (String) null;
            if (data != null)
                text = data.toText();
            textCompletionHandler.run(text);
        }, (errorCode) -> {
            errorCodeCompletionHandler.run(errorCode);
        });
    }

    public void requestImage(URLRequest request,
                             URLSession session,
                             ImageCompletionHandler imageCompletionHandler,
                             ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        requestData(request, session, (data) -> {
            Bitmap bitmap = (Bitmap) null;
            if (data != null)
                bitmap = BitmapFactory.decodeByteArray(data.toBytes(), 0, data.length());
            imageCompletionHandler.run(bitmap);
        }, (errorCode) -> {
            errorCodeCompletionHandler.run(errorCode);
        });
    }

    public void downloadFile(URLRequest request,
                             URLSession session,
                             URLCompletionHandler urlCompletionHandler,
                             ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        URLSessionDownloadTask task = creteDownloadTask(request, session, (localURL) -> {
            urlCompletionHandler.run(localURL);
        }, (errorCode) -> {
            errorCodeCompletionHandler.run(errorCode);
        });

        // Start the task
        task.resume();
    }

    public interface DataCompletionHandler {
        void run(Data data);
    }

    public interface URLCompletionHandler {
        void run(URL url);
    }

    public interface TextCompletionHandler {
        void run(String text);
    }

    public interface ImageCompletionHandler {
        void run(Bitmap image);
    }

    public interface ErrorCodeCompletionHandler {
        void run(int errorCode);
    }
}
