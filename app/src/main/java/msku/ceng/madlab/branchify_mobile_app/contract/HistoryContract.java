package msku.ceng.madlab.branchify_mobile_app.contract;

import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public interface HistoryContract {

    // View (Fragment)
    interface View {
        void showHistoryList(List<Song> songs);
        void showLoading();
        void hideLoading();
        void showError(String message);
    }

    // Presenter (Beyin)
    interface Presenter {
        void loadHistory();
    }
}