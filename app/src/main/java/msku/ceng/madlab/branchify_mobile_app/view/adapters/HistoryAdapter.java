package msku.ceng.madlab.branchify_mobile_app.view.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<Song> historyList;
    private final OnItemClickListener listener;
    private final Set<String> favoriteSongTitles = new HashSet<>();
    private final FirestoreManager firestoreManager;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public HistoryAdapter(List<Song> historyList, List<Song> favoritesList, OnItemClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
        this.firestoreManager = new FirestoreManager();
        for (Song song : favoritesList) {
            favoriteSongTitles.add(song.getTitle());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = historyList.get(position);
        holder.bind(song, listener);

        updateHeartIcon(holder, favoriteSongTitles.contains(song.getTitle()), false);

        holder.iconHeart.setOnClickListener(v -> {
            boolean isCurrentlyFavorite = favoriteSongTitles.contains(song.getTitle());
            if (isCurrentlyFavorite) {
                firestoreManager.removeFavorite(song);
                favoriteSongTitles.remove(song.getTitle());
                updateHeartIcon(holder, false, false);
                Toast.makeText(v.getContext(), song.getTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                firestoreManager.addFavorite(song);
                favoriteSongTitles.add(song.getTitle());
                updateHeartIcon(holder, true, true);
                Toast.makeText(v.getContext(), song.getTitle() + " added to favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHeartIcon(@NonNull ViewHolder holder, boolean isFavorite, boolean animate) {
        if (isFavorite) {
            holder.iconHeart.setImageResource(R.drawable.ic_heart_filled);
            holder.iconHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            if (animate) {
                Animation fillAnimation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.heart_fill_anim);
                holder.iconHeart.startAnimation(fillAnimation);
            }
        } else {
            holder.iconHeart.setImageResource(R.drawable.ic_heart_outline);
            holder.iconHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        }
    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textTitle;
        final TextView textArtist;
        final TextView textTimestamp;
        final ImageView iconHeart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            textTimestamp = itemView.findViewById(R.id.textDuration); // Reusing duration text view for timestamp
            iconHeart = itemView.findViewById(R.id.iconHeart);
        }

        void bind(final Song song, final OnItemClickListener listener) {
            textTitle.setText(song.getTitle());
            textArtist.setText(song.getArtist());

            if (song.getTimestamp() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                textTimestamp.setText(sdf.format(song.getTimestamp()));
            } else {
                textTimestamp.setText("");
            }

            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
        }
    }
}