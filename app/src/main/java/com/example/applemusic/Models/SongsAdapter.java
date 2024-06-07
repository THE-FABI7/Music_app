package com.example.applemusic.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applemusic.Models.Result;
import com.example.applemusic.R;

import java.util.List;

/**
 * Adaptador para mostrar una lista de canciones en un RecyclerView.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    private Context context;
    private List<Result> songs;
    private OnItemClickListener listener;

    /**
     * Interfaz para manejar eventos de clic en el botón de reproducción.
     */
    public interface OnItemClickListener {
        void onPlayButtonClick(Result song);
    }

    /**
     * Constructor del SongsAdapter.
     *
     * @param context  El contexto en el que opera el adaptador.
     * @param songs    La lista de canciones a mostrar.
     * @param listener El listener para manejar clics en el botón de reproducción.
     */
    public SongsAdapter(Context context, List<Result> songs, OnItemClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    /**
     * Crea un nuevo ViewHolder cuando no hay ViewHolders existentes que el RecyclerView pueda reutilizar.
     *
     * @param parent   El ViewGroup padre al que se adjuntará la nueva Vista.
     * @param viewType El tipo de vista del nuevo View.
     * @return Un nuevo SongViewHolder.
     */
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    /**
     * Vincula los datos al ViewHolder en la posición especificada.
     *
     * @param holder   El ViewHolder al que se deben vincular los datos.
     * @param position La posición del ítem dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Result song = songs.get(position);
        holder.songName.setText(song.getTrackName());
        holder.songDuration.setText(formatDuration(song.getTrackTimeMillis()));

        // Cargar la imagen de la canción usando Glide
        Glide.with(context)
                .load(song.getArtworkUrl100())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.songImage);

        holder.playButton.setOnClickListener(v -> listener.onPlayButtonClick(song));
    }

    /**
     * Devuelve el número total de ítems en el conjunto de datos mantenido por el adaptador.
     *
     * @return El número total de ítems en el conjunto de datos.
     */
    @Override
    public int getItemCount() {
        return songs.size();
    }

    /**
     * Clase ViewHolder para el ítem de la canción.
     */
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage;
        TextView songName;
        TextView songDuration;
        Button playButton;

        /**
         * Constructor del SongViewHolder.
         *
         * @param itemView La vista del ítem.
         */
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.song_image);
            songName = itemView.findViewById(R.id.song_name);
            songDuration = itemView.findViewById(R.id.song_duration);
            playButton = itemView.findViewById(R.id.play_button);
        }
    }

    /**
     * Formatea la duración de milisegundos a una cadena en el formato "M:SS".
     *
     * @param millis La duración en milisegundos.
     * @return La cadena de duración formateada.
     */
    private String formatDuration(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
