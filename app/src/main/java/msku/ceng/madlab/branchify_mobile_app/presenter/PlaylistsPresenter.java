package msku.ceng.madlab.branchify_mobile_app.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msku.ceng.madlab.branchify_mobile_app.contract.PlaylistsContract;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class PlaylistsPresenter implements PlaylistsContract.Presenter {

    private PlaylistsContract.View view;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private final FirestoreManager firestoreManager;

    public PlaylistsPresenter() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firestoreManager = new FirestoreManager();
    }

    @Override
    public void attachView(PlaylistsContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadPlaylists() {
        if (view == null) return;
        view.showLoading();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            view.showError("User not logged in");
            view.hideLoading();
            return;
        }

        db.collection("users").document(user.getUid()).collection("playlists")
                .get()
                .addOnCompleteListener(task -> {
                    if (view == null) return;
                    if (task.isSuccessful()) {
                        List<Playlist> playlists = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Playlist playlist = document.toObject(Playlist.class);
                            playlist.setDocumentId(document.getId());
                            playlists.add(playlist);
                        }
                        view.showPlaylists(playlists);
                    } else {
                        view.showError("Failed to load playlists.");
                    }
                    view.hideLoading();
                });
    }

    @Override
    public void createPlaylist(String name) {
        if (view == null) return;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            view.showError("User not logged in");
            return;
        }

        Map<String, Object> playlist = new HashMap<>();
        playlist.put("name", name);
        playlist.put("songIds", new ArrayList<String>());

        db.collection("users").document(user.getUid()).collection("playlists")
                .add(playlist)
                .addOnSuccessListener(documentReference -> loadPlaylists())
                .addOnFailureListener(e -> {
                    if (view != null) {
                        view.showError("Failed to create playlist.");
                    }
                });
    }

    @Override
    public void deletePlaylist(Playlist playlist) {
        // Implement deletion logic
    }
    
    public void addSongToPlaylist(Song song, Playlist playlist) {
        if (view == null) return;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            view.showError("User not logged in");
            return;
        }

        // Step 1: Ensure the song exists in the global collection
        firestoreManager.addSongToGlobalCollection(song, new FirestoreManager.OnTaskCompleteListener() {
            @Override
            public void onSuccess() {
                // Step 2: On success, add the song's ID to the playlist
                String songId = String.valueOf(song.getId());
                db.collection("users").document(user.getUid()).collection("playlists").document(playlist.getDocumentId())
                        .update("songIds", FieldValue.arrayUnion(songId))
                        .addOnSuccessListener(aVoid -> {
                            if (view != null) {
                                view.showError(song.getTitle() + " added to " + playlist.getName());
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (view != null) {
                                view.showError("Failed to add song to playlist.");
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) {
                if (view != null) {
                    view.showError("Could not save song details. Please try again.");
                }
            }
        });
    }
}