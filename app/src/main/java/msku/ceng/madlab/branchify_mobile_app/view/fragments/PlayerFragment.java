package msku.ceng.madlab.branchify_mobile_app.view.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.player.MusicPlayerManager;

public class PlayerFragment extends Fragment {

    private ImageView buttonClose, imageAlbumArt;
    private TextView textSongTitle, textArtistName, textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;
    private ImageButton buttonPrevious, buttonPlayPause, buttonNext;

    private MusicPlayerManager musicPlayerManager;
    private Handler handler;
    private Runnable progressUpdater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        musicPlayerManager = MusicPlayerManager.getInstance();
        handler = new Handler(Looper.getMainLooper());

        initializeViews(view);
        setupClickListeners();
        setupSeekBar();
    }

    private void initializeViews(View view) {
        buttonClose = view.findViewById(R.id.buttonClose);
        imageAlbumArt = view.findViewById(R.id.imageAlbumArt);
        textSongTitle = view.findViewById(R.id.textSongTitle);
        textArtistName = view.findViewById(R.id.textArtistName);
        playerSeekBar = view.findViewById(R.id.playerSeekBar);
        textCurrentTime = view.findViewById(R.id.playerTextCurrentTime);
        textTotalDuration = view.findViewById(R.id.playerTextTotalDuration);
        buttonPrevious = view.findViewById(R.id.buttonPrevious);
        buttonPlayPause = view.findViewById(R.id.playerButtonPlayPause);
        buttonNext = view.findViewById(R.id.buttonNext);
    }

    private void setupClickListeners() {
        buttonClose.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        buttonPlayPause.setOnClickListener(v -> {
            if (musicPlayerManager.isPlaying()) {
                musicPlayerManager.pause();
            } else {
                musicPlayerManager.resume();
            }
        });
        buttonNext.setOnClickListener(v -> musicPlayerManager.next());
        buttonPrevious.setOnClickListener(v -> musicPlayerManager.previous());
    }

    private void setupSeekBar() {
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayerManager.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { stopProgressUpdater(); }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { startProgressUpdater(); }
        });
    }

    private final MusicPlayerManager.PlayerListener playerListener = new MusicPlayerManager.PlayerListener() {
        @Override
        public void onStateChanged(MusicPlayerManager.PlaybackState state) {
            updatePlayPauseButton(state);
        }

        @Override
        public void onTrackChanged(Song newSong) {
            updateUIForNewSong(newSong);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        musicPlayerManager.addPlayerListener(playerListener);
        updateUIWithCurrentState();
        startProgressUpdater();
    }

    @Override
    public void onPause() {
        super.onPause();
        musicPlayerManager.removePlayerListener(playerListener);
        stopProgressUpdater();
    }

    private void updateUIWithCurrentState() {
        Song currentSong = musicPlayerManager.getCurrentSong();
        if (currentSong != null) {
            updateUIForNewSong(currentSong);
        }
        updatePlayPauseButton(musicPlayerManager.getCurrentState());
    }

    private void updateUIForNewSong(Song song) {
        textSongTitle.setText(song.getTitle());
        textArtistName.setText(song.getArtist());
        int duration = musicPlayerManager.getDuration();
        playerSeekBar.setMax(duration > 0 ? duration : 0);
        textTotalDuration.setText(formatDuration(duration));

        if (song.getAlbumArtUri() != null && getContext() != null) {
            Glide.with(getContext())
                    .load(song.getAlbumArtUri())
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(imageAlbumArt);
        }
    }

    private void updatePlayPauseButton(MusicPlayerManager.PlaybackState state) {
        buttonPlayPause.setImageResource(state == MusicPlayerManager.PlaybackState.PLAYING ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void startProgressUpdater() {
        stopProgressUpdater();
        progressUpdater = new Runnable() {
            @Override
            public void run() {
                if (musicPlayerManager != null && musicPlayerManager.isPlaying()) {
                    int currentPosition = musicPlayerManager.getCurrentPosition();
                    playerSeekBar.setProgress(currentPosition);
                    textCurrentTime.setText(formatDuration(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(progressUpdater);
    }

    private void stopProgressUpdater() {
        if (handler != null && progressUpdater != null) {
            handler.removeCallbacks(progressUpdater);
        }
    }

    private String formatDuration(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler = null;
        progressUpdater = null;
    }
}