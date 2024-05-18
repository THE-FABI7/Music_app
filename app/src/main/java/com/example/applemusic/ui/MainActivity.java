package com.example.applemusic.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.applemusic.Datasource.ApleeMusicServiceApi;
import com.example.applemusic.Models.Result;
import com.example.applemusic.Models.Root;
import com.example.applemusic.Models.SongsAdapter;
import com.example.applemusic.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch = null;
    private ScrollView request = null;
    private EditText buscar = null;
    private ProgressDialog progressDialog;
    private RecyclerView songsRecyclerView;
    private SongsAdapter songsAdapter;
    private ApleeMusicServiceApi appleMusicServiceApi;
    private List<Result> songsList = new ArrayList<>();

    private MediaPlayer mediaPlayer =  new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();

        appleMusicServiceApi = new ApleeMusicServiceApi(); // Initialize the API service
        setupRecyclerView();
    }

    public void initViews(){
        btnSearch = findViewById(R.id.search_button);
        buscar = findViewById(R.id.search_field);
        songsRecyclerView = findViewById(R.id.songs_recycler_view);
    }

    public void initEvents(){
        btnSearch.setOnClickListener(view -> {
            showProgressDialog();
            requestMusic();
        });
    }

    private void setupRecyclerView() {
        songsAdapter = new SongsAdapter(this, songsList, song -> {
            // Handle play button click
            playSong(song.getPreviewUrl());
            Toast.makeText(MainActivity.this, "Playing " + song.getTrackName(), Toast.LENGTH_SHORT).show();
            // Implement actual play functionality here
        });
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsRecyclerView.setAdapter(songsAdapter);
    }

    public void requestMusic() {
        String buscarName = buscar.getText().toString();

        if (buscarName.isEmpty()) {
            progressDialog.dismiss();  // Close the dialog if the field is empty
            Toast.makeText(MainActivity.this, "Por favor ingrese el nombre del artista", Toast.LENGTH_SHORT).show();
            return;
        }

        appleMusicServiceApi.requestSongsByTerm(buscarName, 10,
                text -> {
                    // Parse the text response into a Root object
                    Root root = parseResponse(text);
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Log.d("data diomedes", text);
                        if (root != null && root.getResults() != null) {
                            songsList.clear();
                            songsList.addAll(root.getResults());
                            songsAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Búsqueda completada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                        }
                    });
                },
                errorCode -> {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Log.e("API_ERROR", "Error code: " + errorCode);
                        Toast.makeText(MainActivity.this, "Error en la búsqueda: " + errorCode, Toast.LENGTH_SHORT).show();
                    });
                }
        );
    }

    private Root parseResponse(String response) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(response, Root.class);
        } catch (JsonSyntaxException e) {
            Log.e("PARSE_ERROR", "Failed to parse JSON", e);
            return null;
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Buscando...");
        progressDialog.setCancelable(false);  // Make the dialog non-cancelable
        progressDialog.show();
    }

    private void playSong(String url) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
        } catch (IOException e) {
            Log.e("MEDIA_PLAYER", "Error preparing MediaPlayer", e);
            Toast.makeText(MainActivity.this, "Error al reproducir la canción", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
