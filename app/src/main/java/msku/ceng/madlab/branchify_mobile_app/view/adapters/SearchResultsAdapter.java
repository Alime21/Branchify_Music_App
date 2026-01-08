package msku.ceng.madlab.branchify_mobile_app.view.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<Song> searchResults = new ArrayList<>();
    private final OnSearchResultClickListener clickListener;

    public interface OnSearchResultClickListener {
        void onSearchResultClick(List<Song> songs, int position);
    }

    public SearchResultsAdapter(OnSearchResultClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateResults(List<Song> results) {
        this.searchResults = results;
        notifyDataSetChanged();
    }

    public void clearResults() {
        this.searchResults.clear();
        notifyDataSetChanged();
    }

    public List<Song> getSearchResults() {
        return searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = searchResults.get(position);
        holder.textTitle.setText(song.getTitle());
        holder.textArtist.setText(song.getArtist());
        holder.textDuration.setText(formatDuration(song.getDuration()));

        // Load album art
        if (song.getAlbumArtUri() != null && !song.getAlbumArtUri().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(song.getAlbumArtUri()))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                    .placeholder(R.drawable.bg_gradient_card)
                    .error(R.drawable.bg_gradient_card)
                    .into(holder.imageAlbumArt);
        } else {
            holder.imageAlbumArt.setImageResource(R.drawable.bg_gradient_card);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSearchResultClick(searchResults, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    private String formatDuration(String durationMs) {
        try {
            long duration = Long.parseLong(durationMs);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes);
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return durationMs;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAlbumArt;
        TextView textTitle, textArtist, textDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAlbumArt = itemView.findViewById(R.id.imageAlbumArt);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            textDuration = itemView.findViewById(R.id.textDuration);
        }
    }
}
