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

import com.example.applemusic.Models.Result;
import com.example.applemusic.R;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    private Context context;
    private List<Result> songs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onPlayButtonClick(Result song);
    }

    public SongsAdapter(Context context, List<Result> songs, OnItemClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Result song = songs.get(position);
        holder.songName.setText(song.getTrackName());
        holder.songDuration.setText(formatDuration(song.getTrackTimeMillis()));
        // Load image with your preferred image loading library (like Picasso or Glide)
        // Example: Glide.with(context).load(song.getArtworkUrl100()).into(holder.songImage);
        holder.playButton.setOnClickListener(v -> listener.onPlayButtonClick(song));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage;
        TextView songName;
        TextView songDuration;
        Button playButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.song_image);
            songName = itemView.findViewById(R.id.song_name);
            songDuration = itemView.findViewById(R.id.song_duration);
            playButton = itemView.findViewById(R.id.play_button);
        }
    }

    private String formatDuration(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
