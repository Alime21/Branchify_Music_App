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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final List<Song> favoritesList;
    private final FirestoreManager firestoreManager;
    private final OnFavoriteRemovedListener removeListener;
    private final OnItemClickListener clickListener;


    public interface OnFavoriteRemovedListener {
        void onFavoriteRemoved(int position);
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public FavoritesAdapter(List<Song> favoritesList, OnFavoriteRemovedListener removeListener, OnItemClickListener clickListener) {
        this.favoritesList = favoritesList;
        this.firestoreManager = new FirestoreManager();
        this.removeListener = removeListener;
        this.clickListener = clickListener;
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