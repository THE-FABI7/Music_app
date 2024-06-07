package com.example.applemusic.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
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

/**
 * Actividad principal de la aplicación Apple Music.
 */
public class MainActivity extends AppCompatActivity {

    private Button btnSearch = null;
    private ScrollView request = null;
    private EditText buscar = null;
    private ProgressDialog progressDialog;
    private RecyclerView songsRecyclerView;
    private SongsAdapter songsAdapter;
    private ApleeMusicServiceApi appleMusicServiceApi;
    private List<Result> songsList = new ArrayList<>();

    private Button btnPause;
    private SeekBar seekBar;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();

        appleMusicServiceApi = new ApleeMusicServiceApi(); // Inicializar el servicio de API
        setupRecyclerView();
        DrawableCompat.setTint(btnPause.getBackground(), ContextCompat.getColor(this, R.color.green_700));
    }

    /**
     * Inicializa las vistas de la actividad.
     */
    public void initViews() {
        btnSearch = findViewById(R.id.search_button);
        buscar = findViewById(R.id.search_field);
        songsRecyclerView = findViewById(R.id.songs_recycler_view);
        btnPause = findViewById(R.id.pause_button);
        seekBar = findViewById(R.id.music_seekbar);

        seekBar.setVisibility(View.GONE);
        btnPause.setVisibility(View.GONE);
    }

    /**
     * Inicializa los eventos de la actividad.
     */
    public void initEvents() {
        btnSearch.setOnClickListener(view -> {
            showProgressDialog();
            requestMusic();
        });
        btnPause.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPause.setText("Play");
            } else {
                mediaPlayer.start();
                btnPause.setText("Pause");
                updateSeekBar();
            }
        });
    }

    /**
     * Configura el RecyclerView para mostrar las canciones.
     */
    private void setupRecyclerView() {
        songsAdapter = new SongsAdapter(this, songsList, song -> {
            // Manejar el clic en el botón de reproducción
            playSong(song.getPreviewUrl());
            Toast.makeText(MainActivity.this, "Reproduciendo " + song.getTrackName(), Toast.LENGTH_SHORT).show();
        });
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsRecyclerView.setAdapter(songsAdapter);
    }

    /**
     * Realiza una solicitud para obtener música basada en el término de búsqueda ingresado.
     */
    public void requestMusic() {
        String buscarName = buscar.getText().toString();

        if (buscarName.isEmpty()) {
            progressDialog.dismiss();  // Cerrar el diálogo si el campo está vacío
            Toast.makeText(MainActivity.this, "Por favor ingrese el nombre del artista", Toast.LENGTH_SHORT).show();
            return;
        }

        appleMusicServiceApi.requestSongsByTerm(buscarName, 10,
                text -> {
                    // Analizar la respuesta de texto en un objeto Root
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

    /**
     * Analiza la respuesta JSON y la convierte en un objeto Root.
     *
     * @param response La respuesta JSON en formato de cadena.
     * @return El objeto Root resultante.
     */
    private Root parseResponse(String response) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(response, Root.class);
        } catch (JsonSyntaxException e) {
            Log.e("PARSE_ERROR", "Failed to parse JSON", e);
            return null;
        }
    }

    /**
     * Muestra un diálogo de progreso mientras se realiza una búsqueda.
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Buscando...");
        progressDialog.setCancelable(false);  // Hacer que el diálogo no se pueda cancelar
        progressDialog.show();
    }

    /**
     * Reproduce una canción desde la URL proporcionada.
     *
     * @param url La URL de la canción a reproducir.
     */
    private void playSong(String url) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar();

                // Mostrar la barra de progreso y el botón de pausa
                seekBar.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.VISIBLE);
            });
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

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * Actualiza la barra de progreso de la canción.
     */
    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            mRunnable = () -> {
                updateSeekBar();
            };
            mHandler.postDelayed(mRunnable, 1000);
        }
    }
}
