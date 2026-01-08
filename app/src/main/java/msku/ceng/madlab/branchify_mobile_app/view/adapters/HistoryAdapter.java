package msku.ceng.madlab.branchify_mobile_app.view.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;
import msku.ceng.madlab.branchify_mobile_app.player.MusicPlayerManager;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<Song> historyList;
    private final OnItemClickListener listener;
    private final Set<String> favoriteSongTitles = new HashSet<>();
    private final FirestoreManager firestoreManager;
    private final MusicPlayerManager musicPlayerManager;
    private final OnSongLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnSongLongClickListener {
        boolean onSongLongClick(Song song, View view);
    }

    public HistoryAdapter(List<Song> historyList, List<Song> favoritesList, OnItemClickListener listener, OnSongLongClickListener longClickListener) {
        this.historyList = historyList;
        this.listener = listener;
        this.firestoreManager = new FirestoreManager();
        this.musicPlayerManager = MusicPlayerManager.getInstance();
        this.longClickListener = longClickListener;
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

        holder.iconMenu.setOnClickListener(v -> showPopupMenu(v, song));
    }

    private void showPopupMenu(View view, Song song) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.song_item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_add_to_queue) {
                musicPlayerManager.addToQueue(song);
                Toast.makeText(view.getContext(), "Added to queue: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_play_next) {
                musicPlayerManager.playNext(song);
                Toast.makeText(view.getContext(), "Playing next: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_sleep_timer) {
                showSleepTimerDialog(view.getContext());
                return true;
            } else if (itemId == R.id.action_add_to_playlist) {
                if (longClickListener != null) {
                    longClickListener.onSongLongClick(song, view);
                }
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showSleepTimerDialog(Context context) {
        final CharSequence[] options = {"15 minutes", "30 minutes", "1 hour", "Cancel Timer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sleep Timer");
        builder.setItems(options, (dialog, item) -> {
            long duration = 0;
            String message = "Sleep timer set.";
            if (options[item].equals("15 minutes")) {
                duration = TimeUnit.MINUTES.toMillis(15);
            } else if (options[item].equals("30 minutes")) {
                duration = TimeUnit.MINUTES.toMillis(30);
            } else if (options[item].equals("1 hour")) {
                duration = TimeUnit.MINUTES.toMillis(60);
            } else if (options[item].equals("Cancel Timer")) {
                musicPlayerManager.cancelSleepTimer();
                message = "Sleep timer canceled.";
                duration = -1;
            }

            if(duration > 0) {
                musicPlayerManager.setSleepTimer(duration);
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
        builder.show();
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
        final ImageView iconMenu;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            textTimestamp = itemView.findViewById(R.id.textDuration); // Reusing duration text view for timestamp
            iconHeart = itemView.findViewById(R.id.iconHeart);
            iconMenu = itemView.findViewById(R.id.iconMenu);
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