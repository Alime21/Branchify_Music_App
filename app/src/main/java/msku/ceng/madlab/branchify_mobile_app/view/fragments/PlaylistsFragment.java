package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.contract.PlaylistsContract;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;
import msku.ceng.madlab.branchify_mobile_app.presenter.PlaylistsPresenter;
import msku.ceng.madlab.branchify_mobile_app.view.adapters.PlaylistAdapter;

public class PlaylistsFragment extends Fragment implements PlaylistsContract.View, PlaylistAdapter.OnPlaylistClickListener {

    private PlaylistsPresenter presenter;
    private PlaylistAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        presenter = new PlaylistsPresenter();
        presenter.attachView(this);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerPlaylists);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new PlaylistAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        ImageView btnBack = view.findViewById(R.id.btnBackPlaylists);
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        ImageView btnAdd = view.findViewById(R.id.btnAddPlaylist);
        btnAdd.setOnClickListener(v -> showCreatePlaylistDialog());

        presenter.loadPlaylists();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void showPlaylists(List<Playlist> playlists) {
        adapter.setPlaylists(playlists);
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

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create Playlist");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_create_playlist, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Create", (dialog, which) -> {
            TextInputEditText editText = customLayout.findViewById(R.id.edit_text_playlist_name);
            String playlistName = editText.getText().toString();
            if (!playlistName.isEmpty()) {
                presenter.createPlaylist(playlistName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPlaylistClick(Playlist playlist) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("playlistId", playlist.getDocumentId());
        bundle.putString("playlistName", playlist.getName());
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
