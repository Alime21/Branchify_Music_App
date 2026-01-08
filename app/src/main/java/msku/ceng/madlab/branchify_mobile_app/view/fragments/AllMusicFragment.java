package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.contract.AllMusicContract;
import msku.ceng.madlab.branchify_mobile_app.contract.PlaylistsContract;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.presenter.AllMusicPresenter;
import msku.ceng.madlab.branchify_mobile_app.presenter.PlaylistsPresenter;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.AllMusicAdapter;

public class AllMusicFragment extends Fragment implements AllMusicContract.View, PlaylistsContract.View {

    private AllMusicPresenter allMusicPresenter;
    private PlaylistsPresenter playlistsPresenter;
    private RecyclerView recyclerView;
    private AllMusicAdapter adapter;
    private List<Playlist> userPlaylists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_music, container, false);

        recyclerView = view.findViewById(R.id.recyclerAllMusic);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        allMusicPresenter = new AllMusicPresenter(this, requireContext());
        allMusicPresenter.loadAllMusic();

        playlistsPresenter = new PlaylistsPresenter();
        playlistsPresenter.attachView(this);
        playlistsPresenter.loadPlaylists();

        return view;
    }

    @Override
    public void showAllMusicList(List<Song> songs) {
        adapter = new AllMusicAdapter(songs, (song, view) -> {
            showAddToPlaylistDialog(song);
            return true;
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddToPlaylistDialog(Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add to Playlist");

        List<String> playlistNames = new ArrayList<>();
        for (Playlist playlist : userPlaylists) {
            playlistNames.add(playlist.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, playlistNames);
        builder.setAdapter(arrayAdapter, (dialog, which) -> {
            Playlist selectedPlaylist = userPlaylists.get(which);
            playlistsPresenter.addSongToPlaylist(song, selectedPlaylist);
        });

        builder.show();
    }

    @Override
    public void showPlaylists(List<Playlist> playlists) {
        this.userPlaylists = playlists;
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
