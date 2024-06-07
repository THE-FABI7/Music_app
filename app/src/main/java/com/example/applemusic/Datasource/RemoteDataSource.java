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

/**
 * Clase para gestionar las solicitudes de datos remotos.
 */
public class RemoteDataSource {

    /**
     * Crea una tarea de datos para la solicitud dada.
     *
     * @param request                    La solicitud URL.
     * @param session                    La sesión URL.
     * @param dataCompletionHandler      El handler para manejar la finalización de la solicitud de datos.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     * @return La tarea de datos de la sesión URL.
     */
    public URLSessionDataTask createDataTask(URLRequest request,
                                             URLSession session,
                                             DataCompletionHandler dataCompletionHandler,
                                             ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        URLSessionDataTask task = session.dataTask(request, (data, response, error) -> {
            // Manejar errores generales
            if (error != null) {
                // Error de red
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

    /**
     * Crea una tarea de descarga para la solicitud dada.
     *
     * @param request                    La solicitud URL.
     * @param session                    La sesión URL.
     * @param urlCompletionHandler       El handler para manejar la finalización de la descarga.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     * @return La tarea de descarga de la sesión URL.
     */
    private URLSessionDownloadTask creteDownloadTask(URLRequest request,
                                                     URLSession session,
                                                     URLCompletionHandler urlCompletionHandler,
                                                     ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        URLSessionDownloadTask task = session.downloadTask(request, (localURL, response, error) -> {
            // Manejar errores generales
            if (error != null) {
                // Error de red
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

    /**
     * Solicita datos usando la solicitud dada.
     *
     * @param request                    La solicitud URL.
     * @param session                    La sesión URL.
     * @param dataCompletionHandler      El handler para manejar la finalización de la solicitud de datos.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     */
    public void requestData(URLRequest request,
                            URLSession session,
                            DataCompletionHandler dataCompletionHandler,
                            ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        // Crear una tarea de red para la solicitud
        URLSessionDataTask task = createDataTask(request, session, (data) -> {
            dataCompletionHandler.run(data);
        }, (errorCode) -> {
            errorCodeCompletionHandler.run(errorCode);
        });

        // Iniciar la tarea
        task.resume();
    }

    /**
     * Solicita texto usando la solicitud dada.
     *
     * @param request                    La solicitud URL.
     * @param session                    La sesión URL.
     * @param textCompletionHandler      El handler para manejar la finalización de la solicitud de texto.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     */
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

    /**
     * Solicita una imagen usando la solicitud dada.
     *
     * @param request                    La solicitud URL.
     * @param session                    La sesión URL.
     * @param imageCompletionHandler     El handler para manejar la finalización de la solicitud de imagen.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     */
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

    /**
     * Descarga un archivo usando la solicitud dada.
     *
     * @param request                    La solicitud URL.
     * @param session                    La sesión URL.
     * @param urlCompletionHandler       El handler para manejar la finalización de la descarga.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     */
    public void downloadFile(URLRequest request,
                             URLSession session,
                             URLCompletionHandler urlCompletionHandler,
                             ErrorCodeCompletionHandler errorCodeCompletionHandler) {

        URLSessionDownloadTask task = creteDownloadTask(request, session, (localURL) -> {
            urlCompletionHandler.run(localURL);
        }, (errorCode) -> {
            errorCodeCompletionHandler.run(errorCode);
        });

        // Iniciar la tarea
        task.resume();
    }

    /**
     * Interfaz para manejar la finalización de solicitudes de datos.
     */
    public interface DataCompletionHandler {
        void run(Data data);
    }

    /**
     * Interfaz para manejar la finalización de solicitudes de URL.
     */
    public interface URLCompletionHandler {
        void run(URL url);
    }

    /**
     * Interfaz para manejar la finalización de solicitudes de texto.
     */
    public interface TextCompletionHandler {
        void run(String text);
    }

    /**
     * Interfaz para manejar la finalización de solicitudes de imagen.
     */
    public interface ImageCompletionHandler {
        void run(Bitmap image);
    }

    /**
     * Interfaz para manejar los errores de las solicitudes.
     */
    public interface ErrorCodeCompletionHandler {
        void run(int errorCode);
    }
}
