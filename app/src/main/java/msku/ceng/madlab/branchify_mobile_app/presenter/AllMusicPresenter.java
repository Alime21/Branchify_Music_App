package msku.ceng.madlab.branchify_mobile_app.presenter;

import android.content.Context;
import android.util.Log;

import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.contract.AllMusicContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class AllMusicPresenter implements AllMusicContract.Presenter {

    private static final String TAG = "AllMusicPresenter";
    private final AllMusicContract.View view;
    private final Context context;
    private final FirestoreManager firestoreManager;


    public AllMusicPresenter(AllMusicContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.firestoreManager = new FirestoreManager();
    }

    @Override
    public void loadAllMusic() {
        view.showLoading();

        ContentResolverHelper contentResolverHelper = new ContentResolverHelper(context);
        List<Song> allMusicList = contentResolverHelper.getAudioFiles();

        if (allMusicList.isEmpty()) {
            view.showError("No music files found.");
        } else {
            // Save each song to the global songs collection in Firestore
            for (Song song : allMusicList) {
                firestoreManager.addSongToGlobalCollection(song, new FirestoreManager.OnTaskCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Successfully added song to global collection: " + song.getTitle());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error adding song to global collection: " + song.getTitle(), e);
                    }
                });
            }
            view.showAllMusicList(allMusicList);
        }

        view.hideLoading();
    }
}
