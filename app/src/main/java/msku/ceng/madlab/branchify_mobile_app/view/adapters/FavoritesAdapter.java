package msku.ceng.madlab.branchify_mobile_app.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Song> favoritesList;

    public FavoritesAdapter(List<Song> favoritesList) {
        this.favoritesList = favoritesList;
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
        holder.textDuration.setText(song.getDuration());
        // Since this is the Favorites page, we can make the Heart icon blue/filled.
        // If the heart in 'item_history_song' is already filled, there is no need to touch it.
        // holder.iconHeart.setImageResource(R.drawable.ic_heart_filled);

    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
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