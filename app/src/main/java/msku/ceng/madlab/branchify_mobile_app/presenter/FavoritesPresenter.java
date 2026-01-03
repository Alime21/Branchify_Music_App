package msku.ceng.madlab.branchify_mobile_app.presenter;

import android.content.Context;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.FavoritesContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class FavoritesPresenter implements FavoritesContract.Presenter {

    private final FavoritesContract.View view;
    private final FirestoreManager firestoreManager;

    public FavoritesPresenter(FavoritesContract.View view, Context context) {
        this.view = view;
        this.firestoreManager = new FirestoreManager();
    }

    @Override
    public void loadFavorites() {
        view.showLoading();

        firestoreManager.getFavorites(new FirestoreManager.OnFavoritesCompleteListener() {
            @Override
            public void onComplete(List<Song> favList) {
                // Always update the list, even if it's empty.
                // The adapter will handle showing an empty state.
                view.showFavoritesList(favList);
                view.hideLoading();
            }

            @Override
            public void onError(Exception e) {
                view.showError("Error loading favorites: " + e.getMessage());
                view.hideLoading();
            }
        });
    }
}