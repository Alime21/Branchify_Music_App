package msku.ceng.madlab.branchify_mobile_app.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.player.MusicPlayerManager;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.AllMusicFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.FavoritesFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HistoryFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HomeFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.PlayerFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.PlaylistsFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.SettingsFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.TreeFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private View nowPlayingBar;
    private TextView textNowPlayingTitle, textNowPlayingArtist;
    private ImageView albumArtSmall;
    private ImageButton buttonPlayPause, buttonPrevious, buttonNext;
    private MusicPlayerManager musicPlayerManager;
    private MusicPlayerManager.PlayerListener playerListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        musicPlayerManager = MusicPlayerManager.getInstance();

        nowPlayingBar = findViewById(R.id.now_playing_bar_include);
        textNowPlayingTitle = nowPlayingBar.findViewById(R.id.textNowPlayingTitle);
        textNowPlayingArtist = nowPlayingBar.findViewById(R.id.textNowPlayingArtist);
        albumArtSmall = nowPlayingBar.findViewById(R.id.albumArtSmall);
        buttonPlayPause = nowPlayingBar.findViewById(R.id.buttonPlayPause);
        buttonPrevious = nowPlayingBar.findViewById(R.id.buttonPrevious);
        buttonNext = nowPlayingBar.findViewById(R.id.buttonNext);

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

        nowPlayingBar.setOnClickListener(v -> openPlayerFragment());

        buttonPlayPause.setOnClickListener(v -> {
            if (musicPlayerManager.isPlaying()) {
                musicPlayerManager.pause();
            } else {
                musicPlayerManager.resume();
            }
        });

        buttonPrevious.setOnClickListener(v -> musicPlayerManager.previous());

        buttonNext.setOnClickListener(v -> musicPlayerManager.next());

        setupMusicPlayerListener();
        requestNotificationPermission();
        checkPermissionsAndLoadFiles();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


    public void playSong(List<Song> songQueue, int index) {
        musicPlayerManager.play(this, songQueue, index);
        nowPlayingBar.setVisibility(View.VISIBLE);
    }

    public void playShuffled(List<Song> songQueue) {
        Collections.shuffle(songQueue);
        playSong(songQueue, 0);
    }

    public void setNowPlayingBarVisibility(int visibility) {
        nowPlayingBar.setVisibility(visibility);
    }

    private void openPlayerFragment() {
        PlayerFragment playerFragment = new PlayerFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_down);
        transaction.add(android.R.id.content, playerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupMusicPlayerListener() {
        playerListener = new MusicPlayerManager.PlayerListener() {
            @Override
            public void onStateChanged(MusicPlayerManager.PlaybackState state) {
                buttonPlayPause.setImageResource(state == MusicPlayerManager.PlaybackState.PLAYING ? R.drawable.ic_pause : R.drawable.ic_play);
            }

            @Override
            public void onTrackChanged(Song newSong) {
                updateUIForNewSong(newSong);
            }
        };
    }

    private void updateUIForNewSong(Song song) {
        textNowPlayingTitle.setText(song.getTitle());
        textNowPlayingArtist.setText(song.getArtist());
        textNowPlayingTitle.setSelected(true); // Enable marquee scrolling
        buttonPlayPause.setImageResource(R.drawable.ic_pause);

        // Load album art
        if (song.getAlbumArtUri() != null && !song.getAlbumArtUri().isEmpty()) {
            albumArtSmall.setPadding(0, 0, 0, 0);
            albumArtSmall.setImageTintList(null);
            Glide.with(this)
                    .load(Uri.parse(song.getAlbumArtUri()))
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .centerCrop()
                    .into(albumArtSmall);
        } else {
            albumArtSmall.setImageResource(R.drawable.ic_music_note);
        }
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

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_playlists) {
                selectedFragment = new PlaylistsFragment();
            } else if (itemId == R.id.nav_all_music) {
                selectedFragment = new AllMusicFragment();
            } else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (itemId == R.id.nav_tree) {
                selectedFragment = new TreeFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Audio permission granted");
                    // Clear cache to force reload now that we have permission
                    ContentResolverHelper.clearCache();
                    loadAudioFiles();
                    // Refresh the current fragment to show music
                    recreate();
                } else {
                    Log.w(TAG, "Audio permission denied");
                    Toast.makeText(this, "Permission denied. Cannot load music files.", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Notification permission granted");
                } else {
                    Log.w(TAG, "Notification permission denied");
                }
            });

    private void checkPermissionsAndLoadFiles() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_AUDIO
            permission = Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            // Android 12 and below uses READ_EXTERNAL_STORAGE
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission already granted, loading audio files");
            loadAudioFiles();
        } else {
            Log.d(TAG, "Requesting permission: " + permission);
            requestPermissionLauncher.launch(permission);
        }
    }

    private void loadAudioFiles() {
        new ContentResolverHelper(this).getAudioFiles();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        musicPlayerManager.addPlayerListener(playerListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        musicPlayerManager.removePlayerListener(playerListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayerManager.release();
    }
}