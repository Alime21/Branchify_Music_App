package msku.ceng.madlab.branchify_mobile_app.contract;

import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public interface AllMusicContract {

    interface View {
        void showAllMusicList(List<Song> songs);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadAllMusic();
    }
}