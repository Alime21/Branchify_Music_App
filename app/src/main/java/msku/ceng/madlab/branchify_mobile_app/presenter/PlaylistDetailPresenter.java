package msku.ceng.madlab.branchify_mobile_app.presenter;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.PlaylistDetailContract;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class PlaylistDetailPresenter implements PlaylistDetailContract.Presenter {

    private PlaylistDetailContract.View view;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    public PlaylistDetailPresenter() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void attachView(PlaylistDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadSongs(String playlistId) {
        if (view == null) return;
        view.showLoading();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            view.showError("User not logged in");
            view.hideLoading();
            return;
        }

        db.collection("users").document(user.getUid()).collection("playlists").document(playlistId)
                .get()
                .addOnCompleteListener(task -> {
                    if (view == null) return;
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Playlist playlist = document.toObject(Playlist.class);
                            if (playlist != null && playlist.getSongIds() != null && !playlist.getSongIds().isEmpty()) {
                                fetchSongsByIds(playlist.getSongIds());
                            } else {
                                view.showSongs(new ArrayList<>());
                                view.hideLoading();
                            }
                        } else {
                            view.showError("Playlist not found.");
                            view.hideLoading();
                        }
                    } else {
                        view.showError("Failed to load playlist songs.");
                        view.hideLoading();
                    }
                });
    }

    private void fetchSongsByIds(List<String> songIds) {
        if (view == null || songIds == null || songIds.isEmpty()) {
            if(view != null) {
                view.showSongs(new ArrayList<>());
                view.hideLoading();
            }
            return;
        }

        // The document IDs in the 'songs' collection are the string representation of the song's long ID
        db.collection("songs").whereIn(com.google.firebase.firestore.FieldPath.documentId(), songIds).get().addOnCompleteListener(task -> {
            if(view == null) return;
            if (task.isSuccessful()) {
                List<Song> songs = task.getResult().toObjects(Song.class);
                view.showSongs(songs);
                if (!songs.isEmpty() && songs.get(0).getAlbumArtUri() != null) {
                    view.showPlaylistArtwork(songs.get(0).getAlbumArtUri());
                }
            } else {
                view.showError("Failed to fetch song details.");
            }
            view.hideLoading();
        });
    }


    @Override
    public void deletePlaylist(String playlistId) {
        if (view == null) return;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            view.showError("User not logged in");
            return;
        }

        db.collection("users").document(user.getUid()).collection("playlists").document(playlistId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (view != null) {
                        view.onPlaylistDeleted();
                    }
                })
                .addOnFailureListener(e -> {
                    if (view != null) {
                        view.showError("Failed to delete playlist.");
                    }
                });
    }

    @Override
    public void renamePlaylist(String playlistId, String newName) {
        if (view == null) return;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            view.showError("User not logged in");
            return;
        }

        db.collection("users").document(user.getUid()).collection("playlists").document(playlistId)
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    if (view != null) {
                        view.onPlaylistRenamed(newName);
                    }
                })
                .addOnFailureListener(e -> {
                    if (view != null) {
                        view.showError("Failed to rename playlist.");
                    }
                });
    }
}