package msku.ceng.madlab.branchify_mobile_app.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import msku.ceng.madlab.branchify_mobile_app.R;

public class Splash2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        // Set the second tab as selected
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if (tab != null) {
            tab.select();
        }

        ImageView nextArrow = findViewById(R.id.nextArrow2);

        // when you click arrow it will go to MainActivity
        nextArrow.setOnClickListener(v -> {
            Intent intent = new Intent(Splash2Activity.this, msku.ceng.madlab.branchify_mobile_app.view.activities.MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}