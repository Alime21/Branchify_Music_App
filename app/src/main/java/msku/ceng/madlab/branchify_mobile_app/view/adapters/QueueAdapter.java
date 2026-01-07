package msku.ceng.madlab.branchify_mobile_app.view.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private final List<Song> queue;
    private int currentSongIndex;
    private final OnItemClickListener clickListener;
    private final OnRemoveClickListener removeListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    public QueueAdapter(List<Song> queue, int currentSongIndex, OnItemClickListener clickListener, OnRemoveClickListener removeListener) {
        this.queue = queue;
        this.currentSongIndex = currentSongIndex;
        this.clickListener = clickListener;
        this.removeListener = removeListener;
    }

    public void setCurrentSongIndex(int index) {
        int oldIndex = this.currentSongIndex;
        this.currentSongIndex = index;
        notifyItemChanged(oldIndex);
        notifyItemChanged(index);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = queue.get(position);
        holder.bind(song, position, currentSongIndex, clickListener, removeListener);
    }

    @Override
    public int getItemCount() {
        return queue.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(queue, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(queue, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView albumArt;
        private final TextView songTitle;
        private final TextView artistName;
        private final ImageButton menuButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.album_art_image_view);
            songTitle = itemView.findViewById(R.id.song_title_text_view);
            artistName = itemView.findViewById(R.id.artist_name_text_view);
            menuButton = itemView.findViewById(R.id.menu_button);
        }

        void bind(final Song song, final int position, int currentSongIndex, final OnItemClickListener clickListener, final OnRemoveClickListener removeListener) {
            songTitle.setText(song.getTitle());
            artistName.setText(song.getArtist());

            Glide.with(itemView.getContext())
                    .load(song.getAlbumArtUri())
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(albumArt);

            if (position == currentSongIndex) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.settings_blue_background));
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }

            itemView.setOnClickListener(v -> clickListener.onItemClick(getAdapterPosition()));
            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.queue_item_menu);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_remove_from_queue) {
                        removeListener.onRemoveClick(getAdapterPosition());
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}