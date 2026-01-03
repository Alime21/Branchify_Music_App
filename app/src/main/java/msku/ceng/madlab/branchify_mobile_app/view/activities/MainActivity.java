package msku.ceng.madlab.branchify_mobile_app.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import msku.ceng.madlab.branchify_mobile_app.R;
import msku.ceng.madlab.branchify_mobile_app.model.Song;
import msku.ceng.madlab.branchify_mobile_app.model.data.ContentResolverHelper;
import msku.ceng.madlab.branchify_mobile_app.model.data.FirebaseManager;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.AllMusicFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.FavoritesFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HistoryFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HomeFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.SettingsFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.TreeFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "READ_MEDIA_AUDIO permission granted.");
                    loadAudioFiles();
                } else {
                    Log.d(TAG, "READ_MEDIA_AUDIO permission denied.");
                    Toast.makeText(this, "Permission denied. Cannot load audio files.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Authenticate the user with Firebase
        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.signInAnonymously();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
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

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "READ_MEDIA_AUDIO permission already granted.");
            loadAudioFiles();
        } else {
            Log.d(TAG, "READ_MEDIA_AUDIO permission not granted. Requesting permission.");
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
        }
    }

    private void loadAudioFiles() {
        ContentResolverHelper contentResolverHelper = new ContentResolverHelper(this);
        List<Song> audioFiles = contentResolverHelper.getAudioFiles();
    }
}