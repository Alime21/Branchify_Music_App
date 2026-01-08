package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.contract.PlaylistDetailContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.presenter.PlaylistDetailPresenter;
import msku.ceng.madlab.branchify_mobile_app.view.activities.MainActivity;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.PlaylistSongAdapter;

public class PlaylistDetailFragment extends Fragment implements PlaylistDetailContract.View, PlaylistSongAdapter.OnItemClickListener {

    private PlaylistDetailPresenter presenter;
    private PlaylistSongAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView textPlaylistName;
    private ImageView imagePlaylistArt;
    private Button btnPlay, btnShuffle;
    private String playlistId;
    private String playlistName;
    private List<Song> songList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_detail, container, false);

        presenter = new PlaylistDetailPresenter();
        presenter.attachView(this);

        if (getArguments() != null) {
            playlistId = getArguments().getString("playlistId");
            playlistName = getArguments().getString("playlistName");
        }

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlaylistSongAdapter(songList, this);
        recyclerView.setAdapter(adapter);

        textPlaylistName = view.findViewById(R.id.textPlaylistName);
        textPlaylistName.setText(playlistName);
        imagePlaylistArt = view.findViewById(R.id.imagePlaylistArt);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnShuffle = view.findViewById(R.id.btnShuffle);


        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this::showPopupMenu);
        
        btnPlay.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity && !songList.isEmpty()) {
                ((MainActivity) getActivity()).playSong(songList, 0);
            }
        });

        btnShuffle.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity && !songList.isEmpty()) {
                ((MainActivity) getActivity()).playShuffled(songList);
            }
        });

        presenter.loadSongs(playlistId);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void showSongs(List<Song> songs) {
        this.songList.clear();
        this.songList.addAll(songs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaylistDeleted() {
        Toast.makeText(getContext(), "Playlist deleted", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onPlaylistRenamed(String newName) {
        this.playlistName = newName;
        Toast.makeText(getContext(), "Playlist renamed to " + newName, Toast.LENGTH_SHORT).show();
        textPlaylistName.setText(newName);
    }

    @Override
    public void showPlaylistArtwork(String artworkUri) {
        if (getContext() != null && artworkUri != null && !artworkUri.isEmpty()) {
            Glide.with(getContext())
                    .load(Uri.parse(artworkUri))
                    .placeholder(R.drawable.musicicon)
                    .error(R.drawable.musicicon)
                    .into(imagePlaylistArt);
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.playlist_detail_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_rename_playlist) {
                showRenamePlaylistDialog();
                return true;
            } else if (itemId == R.id.action_delete_playlist) {
                presenter.deletePlaylist(playlistId);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showRenamePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Rename Playlist");

        final EditText input = new EditText(requireContext());
        input.setText(playlistName);
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString();
            presenter.renamePlaylist(playlistId, newName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onItemClick(int position) {
        if (getActivity() instanceof MainActivity && !songList.isEmpty() && position >= 0 && position < songList.size()) {
            ((MainActivity) getActivity()).playSong(songList, position);
        }
    }
}