package msku.ceng.madlab.branchify_mobile_app.player;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class MusicPlayerManager {

    private static final String TAG = "MusicPlayerManager";
    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;

    private MusicPlayerManager() {
        mediaPlayer = new MediaPlayer();
    }

    public static synchronized MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public void play(Context context, Song song) {
        if (song == null) return;

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            
            Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.getId()
            );

            mediaPlayer.setDataSource(context, trackUri);
            mediaPlayer.prepareAsync(); // Use async preparation
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                currentSong = song;
                Log.d(TAG, "Playing: " + song.getTitle());
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error playing song", e);
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        instance = null;
    }
}