package msku.ceng.madlab.branchify_mobile_app.contract;

import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.model.Song;

public interface HistoryContract {
    interface View {
        void showHistory(List<Song> history, List<Song> favorites);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    interface Presenter {
        void loadData();
        void onDestroy();
    }
}