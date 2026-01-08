
package msku.ceng.madlab.branchify_mobile_app.model.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class ContentResolverHelper {

    private final Context mContext;
    private static final String TAG = "ContentResolverHelper";
    private static List<Song> allMusicCache;
    
    // Threading components for async operations
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Callback interface for async music loading
     */
    public interface OnMusicLoadedListener {
        void onMusicLoaded(List<Song> songs);
        void onError(Exception e);
    }

    public ContentResolverHelper(Context context) {
        mContext = context;
    }

    /**
     * Asynchronously loads audio files from the device.
     * The callback will be invoked on the main thread.
     * 
     * @param listener Callback to receive the loaded songs
     */
    public void getAudioFilesAsync(OnMusicLoadedListener listener) {
        // Return cached data immediately if available
        if (allMusicCache != null) {
            Log.d(TAG, "Returning cached music on thread: " + Thread.currentThread().getName());
            listener.onMusicLoaded(allMusicCache);
            return;
        }

        // Load from device on background thread
        executor.execute(() -> {
            Log.d(TAG, "Loading audio files on background thread: " + Thread.currentThread().getName());
            try {
                List<Song> songs = loadAudioFilesFromDevice();
                
                // Post result back to main thread
                mainHandler.post(() -> {
                    Log.d(TAG, "Delivering results on main thread: " + Thread.currentThread().getName());
                    listener.onMusicLoaded(songs);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading audio files asynchronously", e);
                mainHandler.post(() -> listener.onError(e));
            }
        });
    }

    /**
     * Synchronous method to get audio files (blocking).
     * Consider using getAudioFilesAsync for better UX.
     */
    public List<Song> getAudioFiles() {
        if (allMusicCache != null) {
            return allMusicCache;
        }
        return loadAudioFilesFromDevice();
    }

    /**
     * Internal method that performs the actual loading from MediaStore.
     * This should be called from a background thread.
     */
    private List<Song> loadAudioFilesFromDevice() {
        Log.d(TAG, "Loading from device on thread: " + Thread.currentThread().getName());
        
        List<Song> audioFiles = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
        
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";


        try (Cursor cursor = contentResolver.query(uri, projection, selection, null, null)) {
            if (cursor == null) {
                Log.e(TAG, "Query returned a null cursor.");
                return audioFiles; // Return empty list
            }

            if (cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

                do {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);
                    long albumId = cursor.getLong(albumIdColumn);
                    long duration = cursor.getLong(durationColumn);

                    Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);

                    audioFiles.add(new Song(id, title, artist, String.valueOf(duration), albumArtUri.toString()));
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "Cursor is empty. No audio files found.");
            }
            Log.d(TAG, "Number of audio files found: " + audioFiles.size());
        } catch (Exception e) {
            Log.e(TAG, "Error querying for audio files.", e);
        }
        allMusicCache = audioFiles;
        return audioFiles;
    }

    public static List<Song> getAllMusicCache() {
        return allMusicCache;
    }

}
