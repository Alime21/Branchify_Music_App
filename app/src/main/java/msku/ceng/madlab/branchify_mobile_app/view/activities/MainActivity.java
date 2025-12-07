package msku.ceng.madlab.branchify_mobile_app.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import msku.ceng.madlab.branchify_mobile_app.R;

// Fragment'ları import ediyoruz (Paket yoluna dikkat et)
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HomeFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.FavoritesFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.TreeFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.HistoryFragment;
import msku.ceng.madlab.branchify_mobile_app.view.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // İçinde BottomNav ve FrameLayout olan XML

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 1. Uygulama ilk açıldığında varsayılan olarak HomeFragment (Playlists) görünsün
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // 2. Alt menüye tıklanınca ne olacağını belirleyen kod
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_playlists) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (itemId == R.id.nav_tree) {
                selectedFragment = new TreeFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            // Eğer geçerli bir fragment seçildiyse ekrana yükle
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}