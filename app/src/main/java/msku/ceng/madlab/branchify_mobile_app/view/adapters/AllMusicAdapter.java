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
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class AllMusicAdapter extends RecyclerView.Adapter<AllMusicAdapter.ViewHolder> {

    private final List<Song> allMusicList;
    private final Set<String> favoriteSongTitles = new HashSet<>();
    private final FirestoreManager firestoreManager;

    public AllMusicAdapter(List<Song> allMusicList) {
        this.allMusicList = allMusicList;
        this.firestoreManager = new FirestoreManager();
        fetchInitialFavorites();
    }

    private void fetchInitialFavorites() {
        firestoreManager.getFavorites(new FirestoreManager.OnFavoritesCompleteListener() {
            @Override
            public void onComplete(List<Song> favoriteSongs) {
                for (Song song : favoriteSongs) {
                    favoriteSongTitles.add(song.getTitle());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = allMusicList.get(position);
        holder.textTitle.setText(song.getTitle());
        holder.textArtist.setText(song.getArtist());
        holder.textDuration.setText(formatDuration(song.getDuration()));

        // Set icon and color based on favorite status
        updateHeartIcon(holder, song, false);

        holder.iconHeart.setOnClickListener(v -> {
            boolean isCurrentlyFavorite = favoriteSongTitles.contains(song.getTitle());
            if (isCurrentlyFavorite) {
                firestoreManager.removeFavorite(song);
                favoriteSongTitles.remove(song.getTitle());
                updateHeartIcon(holder, song, false);
            } else {
                firestoreManager.addFavorite(song);
                favoriteSongTitles.add(song.getTitle());
                updateHeartIcon(holder, song, true); // Animate when adding
            }
        });
    }

    private void updateHeartIcon(@NonNull ViewHolder holder, Song song, boolean animate) {
        if (favoriteSongTitles.contains(song.getTitle())) {
            holder.iconHeart.setImageResource(R.drawable.ic_heart_filled);
            holder.iconHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            if (animate) {
                Animation fillAnimation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.heart_fill_anim);
                holder.iconHeart.startAnimation(fillAnimation);
            }
        } else {
            holder.iconHeart.setImageResource(R.drawable.ic_heart_outline);
            // Set tint to a neutral grey color
            holder.iconHeart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        }
    }

    @Override
    public int getItemCount() {
        return allMusicList.size();
    }

    private String formatDuration(String durationStr) {
        try {
            long millis = Long.parseLong(durationStr);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textArtist, textDuration;
        ImageView iconHeart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            textDuration = itemView.findViewById(R.id.textDuration);
            iconHeart = itemView.findViewById(R.id.iconHeart);
        }
    }
}