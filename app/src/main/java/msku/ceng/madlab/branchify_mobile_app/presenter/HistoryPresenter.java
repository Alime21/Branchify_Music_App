package msku.ceng.madlab.branchify_mobile_app.presenter;

import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.HistoryContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class HistoryPresenter implements HistoryContract.Presenter {

    private HistoryContract.View view;

    public HistoryPresenter(HistoryContract.View view) {
        this.view = view;
    }

    @Override
    public void loadHistory() {
        view.showLoading();

        // dummy dataset using the correct constructor
        // List<Song> historyList = new ArrayList<>();
        // historyList.add(new Song(1L, "Lorem ipsum", "Sanatçı 1", "95000"));
       //  historyList.add(new Song(2L, "Branchify Song", "Bizim Grup", "190000"));
       //  historyList.add(new Song(3L, "MVP Rules", "Coder", "165000"));

        //if (historyList.isEmpty()) {
      //      view.showError("not found");
    //    } else {
     //       view.showHistoryList(historyList);
    ///    }

    //    view.hideLoading();
    }
}