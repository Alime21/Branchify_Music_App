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
import java.util.Objects;
import java.util.stream.Collectors;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.contract.FavoritesContract;
import msku.ceng.madlab.branchify_mobile_app.contract.PlaylistsContract;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.presenter.FavoritesPresenter;
import msku.ceng.madlab.branchify_mobile_app.presenter.PlaylistsPresenter;
import msku.ceng.madlab.branchify_mobile_app.view.activities.MainActivity;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.FavoritesAdapter;


public class FavoritesFragment extends Fragment implements FavoritesContract.View, FavoritesAdapter.OnFavoriteRemovedListener, FavoritesAdapter.OnItemClickListener, FavoritesAdapter.OnSongLongClickListener, PlaylistsContract.View {

    private FavoritesPresenter presenter;
    private PlaylistsPresenter playlistsPresenter;
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private List<Song> favoritesList = new ArrayList<>();
    private List<Playlist> userPlaylists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // 1. RecyclerView
        recyclerView = view.findViewById(R.id.recyclerFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2.Back button
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }else {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        // 3. start MVP called the Presenter
        presenter = new FavoritesPresenter(this, requireContext());
        presenter.loadFavorites();

        playlistsPresenter = new PlaylistsPresenter();
        playlistsPresenter.attachView(this);
        playlistsPresenter.loadPlaylists();

        return view;
    }

    //MVP Methods

    @Override
    public void showFavoritesList(List<Song> songs) {
        favoritesList.clear();
        favoritesList.addAll(songs);
        adapter = new FavoritesAdapter(favoritesList, this, this, this);
        recyclerView.setAdapter(adapter);
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

    @Override
    public void onFavoriteRemoved(int position) {
        favoritesList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onItemClick(int position) {
        List<Song> allMusic = ContentResolverHelper.getAllMusicCache();
        if (allMusic == null || allMusic.isEmpty()) {
            Toast.makeText(getContext(), "Local music library not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Song> fullFavoritesQueue = favoritesList.stream()
                .map(favSong -> allMusic.stream()
                        .filter(localSong -> localSong.getTitle().equals(favSong.getTitle()) && localSong.getArtist().equals(favSong.getArtist()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (fullFavoritesQueue.size() != favoritesList.size()) {
             Toast.makeText(getContext(), "Some favorite songs are not found in the local library.", Toast.LENGTH_SHORT).show();
        }

        if (!fullFavoritesQueue.isEmpty() && getActivity() instanceof MainActivity) {
            Song clickedSong = favoritesList.get(position);
            int newPosition = -1;
            for (int i = 0; i < fullFavoritesQueue.size(); i++) {
                if (fullFavoritesQueue.get(i).getTitle().equals(clickedSong.getTitle()) && fullFavoritesQueue.get(i).getArtist().equals(clickedSong.getArtist())) {
                    newPosition = i;
                    break;
                }
            }
            if(newPosition != -1) {
                ((MainActivity) getActivity()).playSong(fullFavoritesQueue, newPosition);
            } else {
                Toast.makeText(getContext(), "Could not find the clicked song in the full queue.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSongLongClick(Song song, View view) {
        showAddToPlaylistDialog(song);
        return true;
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
}