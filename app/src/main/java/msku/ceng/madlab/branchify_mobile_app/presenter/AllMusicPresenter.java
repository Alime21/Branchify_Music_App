package msku.ceng.madlab.branchify_mobile_app.presenter;

import android.content.Context;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.AllMusicContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;

public class AllMusicPresenter implements AllMusicContract.Presenter {

    private final AllMusicContract.View view;
    private final Context context;

    public AllMusicPresenter(AllMusicContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void loadAllMusic() {
        view.showLoading();

        ContentResolverHelper contentResolverHelper = new ContentResolverHelper(context);
        List<Song> allMusicList = contentResolverHelper.getAudioFiles();

        if (allMusicList.isEmpty()) {
            view.showError("No music files found.");
        } else {
            view.showAllMusicList(allMusicList);
        }

        view.hideLoading();
    }
}