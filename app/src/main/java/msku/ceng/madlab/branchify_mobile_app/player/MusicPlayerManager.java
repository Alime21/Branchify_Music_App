package msku.ceng.madlab.branchify_mobile_app.player;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import msku.ceng.madlab.branchify_mobile_app.model.Song;

public class MusicPlayerManager implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "MusicPlayerManager";
    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private Context context;

    private List<Song> songQueue = Collections.emptyList();
    private int currentSongIndex = -1;

    // Listeners to notify UI of changes
    private final List<PlayerListener> playerListeners = new ArrayList<>();

    private MusicPlayerManager() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    public static synchronized MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public void addPlayerListener(PlayerListener listener) {
        if (!playerListeners.contains(listener)) {
            playerListeners.add(listener);
        }
    }

    public void removePlayerListener(PlayerListener listener) {
        playerListeners.remove(listener);
    }
    
    public void setPlayerListener(PlayerListener listener) {
        // This is now deprecated, but we can keep it for backward compatibility or refactor to remove it.
        // For now, it just adds a single listener, clearing others.
        playerListeners.clear();
        playerListeners.add(listener);
    }


    public void play(Context context, List<Song> queue, int index) {
        this.context = context.getApplicationContext();
        if (queue == null || queue.isEmpty() || index < 0 || index >= queue.size()) {
            return;
        }

        this.songQueue = queue;
        this.currentSongIndex = index;
        Song songToPlay = songQueue.get(currentSongIndex);

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();

            Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songToPlay.getId()
            );

            mediaPlayer.setDataSource(context, trackUri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Log.d(TAG, "Playing: " + songToPlay.getTitle());
                for (PlayerListener listener : playerListeners) {
                    listener.onStateChanged(PlaybackState.PLAYING);
                    listener.onTrackChanged(songToPlay);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error playing song", e);
        }
    }

    public void next() {
        if (context != null && !songQueue.isEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % songQueue.size();
            play(context, songQueue, currentSongIndex);
        }
    }

    public void previous() {
        if (context != null && !songQueue.isEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + songQueue.size()) % songQueue.size();
            play(context, songQueue, currentSongIndex);
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            for (PlayerListener listener : playerListeners) {
                listener.onStateChanged(PlaybackState.PAUSED);
            }
        }
    }

    public void resume() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            for (PlayerListener listener : playerListeners) {
                listener.onStateChanged(PlaybackState.PLAYING);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    // --- Getters and Setters ---
    public void seekTo(int position) { mediaPlayer.seekTo(position); }
    public int getCurrentPosition() { return mediaPlayer.isPlaying() ? mediaPlayer.getCurrentPosition() : 0; }
    public int getDuration() { return mediaPlayer.getDuration(); }
    public boolean isPlaying() { return mediaPlayer.isPlaying(); }
    public Song getCurrentSong() {
        if (currentSongIndex != -1 && currentSongIndex < songQueue.size()) {
            return songQueue.get(currentSongIndex);
        }
        return null;
    }
    public PlaybackState getCurrentState() {
        return mediaPlayer.isPlaying() ? PlaybackState.PLAYING : PlaybackState.PAUSED;
    }


    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        instance = null;
    }

    // --- Listener Interface and Enum ---
    public enum PlaybackState { PLAYING, PAUSED }

    public interface PlayerListener {
        void onStateChanged(PlaybackState state);
        void onTrackChanged(Song newSong);
    }
}