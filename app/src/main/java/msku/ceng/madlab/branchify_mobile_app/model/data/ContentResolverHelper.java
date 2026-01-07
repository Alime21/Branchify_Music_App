
package msku.ceng.madlab.branchify_mobile_app.model.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class ContentResolverHelper {

    private final Context mContext;
    private static final String TAG = "ContentResolverHelper";

    public ContentResolverHelper(Context context) {
        mContext = context;
    }

    public List<Song> getAudioFiles() {
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
        return audioFiles;
    }
}
