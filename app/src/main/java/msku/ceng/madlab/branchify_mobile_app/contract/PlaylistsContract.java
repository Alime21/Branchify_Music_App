package msku.ceng.madlab.branchify_mobile_app.contract;

import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.Playlist;

public interface PlaylistsContract {

    interface View {
        void showPlaylists(List<Playlist> playlists);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadPlaylists();
        void createPlaylist(String name);
        void deletePlaylist(Playlist playlist);
        void attachView(View view);
        void detachView();
    }
}
