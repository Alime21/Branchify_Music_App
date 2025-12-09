package msku.ceng.madlab.branchify_mobile.presenter;

import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.HistoryContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class HistoryPresenter implements HistoryContract.Presenter {

    private HistoryContract.View view;

    // connect the View (Fragment)
    public HistoryPresenter(HistoryContract.View view) {
        this.view = view;
    }

    @Override
    public void loadHistory() {
        view.showLoading();

        // dummy dataset
        List<Song> historyList = new ArrayList<>();
        historyList.add(new Song("Lorem ipsum", "Sanatçı 1", "1:35"));
        historyList.add(new Song("Branchify Song", "Bizim Grup", "3:10"));
        historyList.add(new Song("MVP Rules", "Coder", "2:45"));

        // if data is ready send view
        if (historyList.isEmpty()) {
            view.showError("not found");
        } else {
            view.showHistoryList(historyList);
        }

        view.hideLoading();
    }
}