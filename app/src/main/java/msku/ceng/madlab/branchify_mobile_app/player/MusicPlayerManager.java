package msku.ceng.madlab.branchify_mobile_app.player;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.SeekBar;

import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class MusicPlayerManager {

    private static final String TAG = "MusicPlayerManager";
    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private Handler handler;
    private Runnable progressUpdater;

    private MusicPlayerManager() {
        mediaPlayer = new MediaPlayer();
        handler = new Handler(Looper.getMainLooper());
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
            mediaPlayer.prepareAsync();
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

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer.isPlaying() ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer.isPlaying() ? mediaPlayer.getDuration() : 0;
    }
    
    public void startProgressUpdater(final SeekBar seekBar) {
        if (progressUpdater == null) {
            progressUpdater = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                    handler.postDelayed(this, 1000); // Update every second
                }
            };
        }
        handler.post(progressUpdater);
    }
    
    public void stopProgressUpdater() {
        if (progressUpdater != null) {
            handler.removeCallbacks(progressUpdater);
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
        stopProgressUpdater();
        instance = null;
    }
}