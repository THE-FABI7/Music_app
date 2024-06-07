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

/**
 * Servicio API para interactuar con Apple Music.
 */
public class ApleeMusicServiceApi extends RemoteDataSource {

    private final String BASE_URL = "https://itunes.apple.com/search";

    /**
     * Constructor del ApleeMusicServiceApi.
     */
    public ApleeMusicServiceApi() {
    }

    /**
     * Convierte un archivo en una URL.
     *
     * @param file El archivo a convertir.
     * @return La URL correspondiente al archivo.
     */
    public URL fileToURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convierte una ruta local en una URL.
     *
     * @param localPath La ruta local a convertir.
     * @return La URL correspondiente a la ruta local.
     */
    public URL localPathToURL(String localPath) {
        return fileToURL(new File(localPath));
    }

    /**
     * Solicita canciones por término de búsqueda.
     *
     * @param searchTerm                 El término de búsqueda.
     * @param limit                      El número máximo de resultados a devolver.
     * @param textCompletionHandler      El handler para manejar la finalización de la solicitud de texto.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     */
    public void requestSongsByTerm(String searchTerm, int limit,
                                   TextCompletionHandler textCompletionHandler,
                                   ErrorCodeCompletionHandler errorCodeCompletionHandler) {
        URLComponents components = new URLComponents(BASE_URL);
        components.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("media", "music"),
                new URLQueryItem("entity", "song"),
                new URLQueryItem("limit", String.valueOf(limit)),
                new URLQueryItem("term", searchTerm)
        });
        // Generar la URL a partir de los componentes
        URL url = components.getURL();
        // Crear una URLRequest
        URLRequest request = new URLRequest(url);
        // Obtener la sesión URL predeterminada
        URLSession session = URLSession.getShared();
        requestText(request, session, text -> textCompletionHandler.run(text), errorCode -> errorCodeCompletionHandler.run(errorCode));
    }

    /**
     * Descarga una vista previa de la pista.
     *
     * @param result                     El resultado que contiene la vista previa de la pista.
     * @param basePath                   La ruta base donde se guardará el archivo.
     * @param urlCompletionHandler       El handler para manejar la finalización de la descarga.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     */
    public void downloadPreviewTrack(Result result,
                                     String basePath,
                                     URLCompletionHandler urlCompletionHandler,
                                     ErrorCodeCompletionHandler errorCodeCompletionHandler) {
        String fullFilename = basePath + "/" + result.getLocalPreviewFilename();
        File newFile = new File(fullFilename);
        URL newFileURL = fileToURL(newFile);
        if (newFile.exists()) {
            urlCompletionHandler.run(newFileURL);
            return;
        }
        URLComponents components = new URLComponents(result.getPreviewUrl());
        URL url = components.getURL();
        // Crear una URLRequest
        URLRequest request = new URLRequest(url);
        // Obtener la sesión URL predeterminada
        URLSession session = URLSession.getShared();
        downloadFile(request, session, localURL -> {
            File tempFile = new File(localURL.getFile());
            tempFile.renameTo(newFile);
            if (urlCompletionHandler != null) {
                urlCompletionHandler.run(newFileURL);
            }
        }, errorCode -> errorCodeCompletionHandler.run(errorCode));
    }

    /**
     * Descarga la carátula de una pista.
     *
     * @param result                     El resultado que contiene la URL de la carátula.
     * @param basePath                   La ruta base donde se guardará la carátula.
     * @param urlCompletionHandler       El handler para manejar la finalización de la descarga.
     * @param errorCodeCompletionHandler El handler para manejar los errores de la solicitud.
     */
    public void downloadArtworkTrack(Result result,
                                     String basePath,
                                     URLCompletionHandler urlCompletionHandler,
                                     ErrorCodeCompletionHandler errorCodeCompletionHandler) {
        String fullFilename = basePath + "/" + result.getLocalArtworkFilename();
        File newFile = new File(fullFilename);
        URL newFileURL = fileToURL(newFile);
        if (newFile.exists()) {
            urlCompletionHandler.run(newFileURL);
            return;
        }
        URLComponents components = new URLComponents(result.getArtworkUrl100());
        URL url = components.getURL();
        // Crear una URLRequest
        URLRequest request = new URLRequest(url);
        // Obtener la sesión URL predeterminada
        URLSession session = URLSession.getShared();
        downloadFile(request, session, localURL -> {
            File tempFile = new File(localURL.getFile());
            tempFile.renameTo(newFile);
            if (urlCompletionHandler != null) {
                urlCompletionHandler.run(newFileURL);
            }
        }, errorCode -> errorCodeCompletionHandler.run(errorCode));
    }

    /**
     * Descarga todas las carátulas de las pistas.
     *
     * @param root                 El objeto raíz que contiene los resultados.
     * @param basePath             La ruta base donde se guardarán las carátulas.
     * @param urlCompletionHandler El handler para manejar la finalización de cada descarga.
     */
    public void downloadAllArtworkTracks(Root root,
                                         String basePath,
                                         URLCompletionHandler urlCompletionHandler) {
        if (root == null)
            return;
        for (Result result : root.getResults()) {
            String fullFilename = basePath + "/" + result.getLocalArtworkFilename();
            if (!new File(fullFilename).exists()) {
                downloadArtworkTrack(result, basePath, localURL -> {
                    urlCompletionHandler.run(localURL);
                }, errorCode -> {
                });
            }
        }
    }
}
