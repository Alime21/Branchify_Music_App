package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.player.MusicPlayerManager;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.QueueAdapter;

public class QueueFragment extends Fragment {

    private MusicPlayerManager musicPlayerManager;
    private QueueAdapter adapter;
    private List<Song> songQueue;
    private RecyclerView recyclerView;

    private final MusicPlayerManager.PlayerListener playerListener = new MusicPlayerManager.PlayerListener() {
        @Override
        public void onStateChanged(MusicPlayerManager.PlaybackState state) {
            // Not needed for this screen
        }

        @Override
        public void onTrackChanged(Song newSong) {
            if (adapter != null) {
                adapter.setCurrentSongIndex(musicPlayerManager.getCurrentSongIndex());
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicPlayerManager = MusicPlayerManager.getInstance();
        songQueue = new ArrayList<>(musicPlayerManager.getSongQueue());

        Toolbar toolbar = view.findViewById(R.id.queue_toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.queue_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new QueueAdapter(songQueue, musicPlayerManager.getCurrentSongIndex(),
                position -> musicPlayerManager.play(getContext(), songQueue, position),
                position -> {
                    songQueue.remove(position);
                    adapter.notifyItemRemoved(position);
                    musicPlayerManager.play(getContext(), songQueue, musicPlayerManager.getCurrentSongIndex());
                });
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                adapter.onItemMove(fromPosition, toPosition);
                // Update the current playing index if it was moved
                int current = musicPlayerManager.getCurrentSongIndex();
                if (current == fromPosition) {
                    musicPlayerManager.play(getContext(), songQueue, toPosition);
                } else if (fromPosition < current && toPosition >= current) {
                     musicPlayerManager.play(getContext(), songQueue, current - 1);
                } else if (fromPosition > current && toPosition <= current) {
                    musicPlayerManager.play(getContext(), songQueue, current + 1);
                }

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not used
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // Update the music player manager with the new queue order
                musicPlayerManager.play(getContext(), songQueue, musicPlayerManager.getCurrentSongIndex());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        musicPlayerManager.addPlayerListener(playerListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        musicPlayerManager.removePlayerListener(playerListener);
    }
}