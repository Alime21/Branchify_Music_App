package msku.ceng.madlab.branchify_mobile_app.presenter;

import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.FavoritesContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class FavoritesPresenter implements FavoritesContract.Presenter {

    private FavoritesContract.View view;

    public FavoritesPresenter(FavoritesContract.View view) {
        this.view = view;
    }

    @Override
    public void loadFavorites() {
        view.showLoading();

        // dummy dataset
        List<Song> favList = new ArrayList<>();
        favList.add(new Song("Lorem ipsum dolor", "Lorem Lorem", "1:35"));
        favList.add(new Song("Another Hit", "Artist Name", "3:40"));
        favList.add(new Song("Best Song Ever", "Pop Star", "2:15"));
        favList.add(new Song("Chill Mix", "DJ Cool", "4:20"));
        favList.add(new Song("Jazz Vibes", "Smooth Jazz", "3:00"));
        favList.add(new Song("Rock Anthem", "The Rockers", "5:10"));
        favList.add(new Song("Piano Solo", "Classic Man", "2:50"));

        if (favList.isEmpty()) {
            view.showError("Favori şarkınız yok.");
        } else {
            view.showFavoritesList(favList);
        }

        view.hideLoading();
    }
}
