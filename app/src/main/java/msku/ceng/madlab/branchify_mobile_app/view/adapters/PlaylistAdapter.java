package msku.ceng.madlab.branchify_mobile_app.view.adapters;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.PlaylistDetailFragment;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<Playlist> playlistList;
    private final FirebaseFirestore db;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    private final OnPlaylistClickListener listener;

    public PlaylistAdapter(List<Playlist> playlistList, OnPlaylistClickListener listener) {
        this.playlistList = playlistList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlistList.clear();
        this.playlistList.addAll(playlists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.textName.setText(playlist.getName());
        holder.textCount.setText(playlist.getTrackCount());
        holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
        
        // Load playlist artwork from first song
        loadPlaylistArt(playlist, holder.imagePlaylistArt);
    }

    private void loadPlaylistArt(Playlist playlist, ImageView imageView) {
        List<String> songIds = playlist.getSongIds();
        if (songIds == null || songIds.isEmpty()) return;

        String firstSongId = songIds.get(0);
        db.collection("songs").document(firstSongId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String artUri = documentSnapshot.getString("albumArtUri");
                        if (artUri != null && !artUri.isEmpty()) {
                            Glide.with(imageView.getContext())
                                    .load(Uri.parse(artUri))
                                    .placeholder(R.drawable.musicicon)
                                    .error(R.drawable.musicicon)
                                    .centerCrop()
                                    .into(imageView);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textCount;
        ImageView imagePlaylistArt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textPlaylistName);
            textCount = itemView.findViewById(R.id.textTrackCount);
            imagePlaylistArt = itemView.findViewById(R.id.imagePlaylistArt);
        }
    }
}
