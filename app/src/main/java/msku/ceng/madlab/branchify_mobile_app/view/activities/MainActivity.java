package msku.ceng.madlab.branchify_mobile_app.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.player.MusicPlayerManager;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.AllMusicFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.FavoritesFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HistoryFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HomeFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.SettingsFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.TreeFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI for Now Playing Bar
    private View nowPlayingBar;
    private TextView textNowPlayingTitle, textCurrentTime, textTotalDuration;
    private ImageButton buttonPlayPause;
    private SeekBar seekBar;
    private MusicPlayerManager musicPlayerManager;
    private Handler handler;
    private Runnable progressUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        musicPlayerManager = MusicPlayerManager.getInstance();
        handler = new Handler(Looper.getMainLooper());
        
        nowPlayingBar = findViewById(R.id.now_playing_bar_include);
        textNowPlayingTitle = nowPlayingBar.findViewById(R.id.textNowPlayingTitle);
        textCurrentTime = nowPlayingBar.findViewById(R.id.textCurrentTime);
        textTotalDuration = nowPlayingBar.findViewById(R.id.textTotalDuration);
        buttonPlayPause = nowPlayingBar.findViewById(R.id.buttonPlayPause);
        seekBar = nowPlayingBar.findViewById(R.id.seekBar);

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                setupApp();
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                signInAnonymously();
            }
        };

        buttonPlayPause.setOnClickListener(v -> {
            if (musicPlayerManager.isPlaying()) {
                musicPlayerManager.pause();
                buttonPlayPause.setImageResource(R.drawable.ic_play);
            } else {
                musicPlayerManager.resume();
                buttonPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayerManager.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopProgressUpdater();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startProgressUpdater();
            }
        });
    }
    
    public void playSong(Song song) {
        musicPlayerManager.play(this, song);
        nowPlayingBar.setVisibility(View.VISIBLE);
        updateUIForNewSong(song);
    }

    private void updateUIForNewSong(Song song) {
        textNowPlayingTitle.setText(song.getTitle());
        buttonPlayPause.setImageResource(R.drawable.ic_pause);
        
        handler.postDelayed(() -> {
            int duration = musicPlayerManager.getDuration();
            if(duration > 0){
                seekBar.setMax(duration);
                textTotalDuration.setText(formatDuration(duration));
                startProgressUpdater();
            }
        }, 100);
    }
    
    private void startProgressUpdater() {
        if (progressUpdater == null) {
            progressUpdater = new Runnable() {
                @Override
                public void run() {
                    if (musicPlayerManager != null && musicPlayerManager.isPlaying()) {
                        int currentPosition = musicPlayerManager.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        textCurrentTime.setText(formatDuration(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            };
        }
        handler.post(progressUpdater);
    }

    private void stopProgressUpdater() {
        if (progressUpdater != null) {
            handler.removeCallbacks(progressUpdater);
        }
    }

    private String formatDuration(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    private void setupApp() {
        bottomNavigationView.setVisibility(View.VISIBLE);

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_playlists) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_all_music) {
                selectedFragment = new AllMusicFragment();
            } else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (itemId == R.id.nav_tree) {
                selectedFragment = new TreeFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        checkPermissionsAndLoadFiles();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "signInAnonymously:failure", task.getException());
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void checkPermissionsAndLoadFiles() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            loadAudioFiles();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
        }
    }

    private void loadAudioFiles() {
        new ContentResolverHelper(this).getAudioFiles();
    }
    
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadAudioFiles();
                } else {
                    Toast.makeText(this, "Permission denied. Cannot load audio files.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayerManager.release();
        stopProgressUpdater();
    }
}