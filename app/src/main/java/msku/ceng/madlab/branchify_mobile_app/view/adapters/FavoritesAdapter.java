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

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;
import msku.ceng.madlab.branchify_mobile_app.player.MusicPlayerManager;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final List<Song> favoritesList;
    private final FirestoreManager firestoreManager;
    private final OnFavoriteRemovedListener removeListener;
    private final OnItemClickListener clickListener;
    private final MusicPlayerManager musicPlayerManager;
    private final OnSongLongClickListener longClickListener;


    public interface OnFavoriteRemovedListener {
        void onFavoriteRemoved(int position);
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnSongLongClickListener {
        boolean onSongLongClick(Song song, View view);
    }


    public FavoritesAdapter(List<Song> favoritesList, OnFavoriteRemovedListener removeListener, OnItemClickListener clickListener, OnSongLongClickListener longClickListener) {
        this.favoritesList = favoritesList;
        this.firestoreManager = new FirestoreManager();
        this.removeListener = removeListener;
        this.clickListener = clickListener;
        this.musicPlayerManager = MusicPlayerManager.getInstance();
        this.longClickListener = longClickListener;
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
        Song song = favoritesList.get(position);
        holder.textTitle.setText(song.getTitle());
        holder.textArtist.setText(song.getArtist());
        holder.textDuration.setText(formatDuration(song.getDuration()));

        updateHeartIcon(holder, true, false);

        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(holder.getAdapterPosition()));

        holder.iconHeart.setOnClickListener(v -> {
            firestoreManager.removeFavorite(song);
            removeListener.onFavoriteRemoved(holder.getAdapterPosition());
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
        return favoritesList.size();
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
        ImageView iconHeart, iconMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            textDuration = itemView.findViewById(R.id.textDuration);
            iconHeart = itemView.findViewById(R.id.iconHeart);
            iconMenu = itemView.findViewById(R.id.iconMenu);
        }
    }
}