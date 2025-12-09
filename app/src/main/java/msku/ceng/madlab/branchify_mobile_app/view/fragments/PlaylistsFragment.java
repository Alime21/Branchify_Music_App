package msku.ceng.madlab.branchify_mobile_app.view.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;

public class PlaylistsFragment extends Fragment{
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        // back button logic
        ImageView btnBack = view.findViewById(R.id.btnBackPlaylists);
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
        // + button
        ImageView btnAdd = view.findViewById(R.id.btnAddPlaylist);
        btnAdd.setOnClickListener(v -> {
            // Şimdilik boş
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPlaylists);
        // 2 boxes side by side
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // dummy datasets
        List<Playlist> data = new ArrayList<>();
        data.add(new Playlist("My Playlist", "33 tracks"));
        data.add(new Playlist("Chill Vibes", "12 tracks"));
        data.add(new Playlist("Gym Hits", "45 tracks"));
        data.add(new Playlist("Sleep Focus", "20 tracks"));
        data.add(new Playlist("Road Trip", "50 tracks"));
        data.add(new Playlist("Coding Mode", "99 tracks"));

        msku.ceng.madlab.branchify_mobile_app.view.adapters.PlaylistAdapter adapter = new msku.ceng.madlab.branchify_mobile_app.view.adapters.PlaylistAdapter(data);
        recyclerView.setAdapter(adapter);

        return view;

    }
}
