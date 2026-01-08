package msku.ceng.madlab.branchify_mobile_app.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import msku.ceng.madlab.branchify_mobile_app.contract.AllMusicContract;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirestoreManager;

public class AllMusicPresenter implements AllMusicContract.Presenter {

    private static final String TAG = "AllMusicPresenter";
    private final AllMusicContract.View view;
    private final Context context;
    private final FirestoreManager firestoreManager;
    
    // Threading components
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    public AllMusicPresenter(AllMusicContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.firestoreManager = new FirestoreManager();
    }

    @Override
    public void loadAllMusic() {
        view.showLoading();
        Log.d(TAG, "Starting to load music on thread: " + Thread.currentThread().getName());

        // Run heavy disk I/O operation on background thread
        executorService.execute(() -> {
            Log.d(TAG, "Loading music files on background thread: " + Thread.currentThread().getName());
            
            ContentResolverHelper contentResolverHelper = new ContentResolverHelper(context);
            List<Song> allMusicList = contentResolverHelper.getAudioFiles();
            
            Log.d(TAG, "Found " + allMusicList.size() + " songs on background thread");

            // Post results back to UI thread
            mainHandler.post(() -> {
                Log.d(TAG, "Updating UI on thread: " + Thread.currentThread().getName());
                
                if (allMusicList.isEmpty()) {
                    view.showError("No music files found.");
                } else {
                    view.showAllMusicList(allMusicList);
                    
                    // Upload songs to Firestore on another background thread
                    uploadSongsToFirestore(allMusicList);
                }
                view.hideLoading();
            });
        });
    }
    
    /**
     * Uploads songs to Firestore in batches on a background thread.
     * This prevents blocking the UI while syncing with the cloud.
     */
    private void uploadSongsToFirestore(List<Song> songs) {
        executorService.execute(() -> {
            Log.d(TAG, "Uploading " + songs.size() + " songs to Firestore on thread: " + Thread.currentThread().getName());
            
            int successCount = 0;
            int failCount = 0;
            
            for (Song song : songs) {
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
            
            Log.d(TAG, "Firestore upload batch completed");
        });
    }
    
    /**
     * Clean up resources when presenter is no longer needed.
     * Call this from the Fragment's onDestroy.
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            Log.d(TAG, "ExecutorService shutdown");
        }
    }
}
