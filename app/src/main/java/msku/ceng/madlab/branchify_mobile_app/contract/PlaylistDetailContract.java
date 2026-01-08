package msku.ceng.madlab.branchify_mobile_app.contract;

import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.model.Song;

public interface PlaylistDetailContract {

    interface View {
        void showSongs(List<Song> songs);
        void showLoading();
        void hideLoading();
        void showError(String message);
        void onPlaylistDeleted();
        void onPlaylistRenamed(String newName);
        void showPlaylistArtwork(String artworkUri);
    }

    interface Presenter {
        void loadSongs(String playlistId);
        void deletePlaylist(String playlistId);
        void renamePlaylist(String playlistId, String newName);
        void attachView(View view);
        void detachView();
    }
}