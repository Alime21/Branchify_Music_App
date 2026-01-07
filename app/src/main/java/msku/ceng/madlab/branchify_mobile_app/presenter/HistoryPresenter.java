package msku.ceng.madlab.branchify_mobile_app.presenter;

import android.content.Context;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.HistoryContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class HistoryPresenter implements HistoryContract.Presenter {

    private HistoryContract.View view;
    private final FirestoreManager firestoreManager;

    public HistoryPresenter(HistoryContract.View view, Context context) {
        this.view = view;
        this.firestoreManager = new FirestoreManager();
    }

    @Override
    public void loadData() {
        if (view != null) {
            view.showLoading();
        }
        firestoreManager.getHistory(new FirestoreManager.OnHistoryCompleteListener() {
            @Override
            public void onComplete(List<Song> historySongs) {
                // After getting history, get favorites
                firestoreManager.getFavorites(new FirestoreManager.OnFavoritesCompleteListener() {
                    @Override
                    public void onComplete(List<Song> favoriteSongs) {
                        if (view != null) {
                            view.hideLoading();
                            view.showHistory(historySongs, favoriteSongs);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (view != null) {
                            view.hideLoading();
                            view.showError(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                if (view != null) {
                    view.hideLoading();
                    view.showError(e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void onDestroy() {
        view = null;
    }
}