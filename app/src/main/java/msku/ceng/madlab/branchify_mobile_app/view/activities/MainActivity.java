package msku.ceng.madlab.branchify_mobile_app.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private View nowPlayingBar; // Changed to View to be more generic
    private TextView textNowPlayingTitle;
    private ImageButton buttonPlayPause;
    private MusicPlayerManager musicPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Correctly find the included layout and its children
        nowPlayingBar = findViewById(R.id.now_playing_bar_include);
        textNowPlayingTitle = nowPlayingBar.findViewById(R.id.textNowPlayingTitle);
        buttonPlayPause = nowPlayingBar.findViewById(R.id.buttonPlayPause);
        musicPlayerManager = MusicPlayerManager.getInstance();

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
    }
    
    public void playSong(Song song) {
        musicPlayerManager.play(this, song);
        textNowPlayingTitle.setText(song.getTitle());
        nowPlayingBar.setVisibility(View.VISIBLE);
        buttonPlayPause.setImageResource(R.drawable.ic_pause);
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
    }
}