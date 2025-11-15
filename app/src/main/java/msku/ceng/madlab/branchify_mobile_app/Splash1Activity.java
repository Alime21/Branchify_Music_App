package msku.ceng.madlab.branchify_mobile_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

public class Splash1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);

        // Set the first tab as selected
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }

        // when you click arrow it will go to Splash2Activity
        ImageView nextArrow = findViewById(R.id.nextArrow1);
        nextArrow.setOnClickListener(v -> {
            Intent intent = new Intent(Splash1Activity.this, Splash2Activity.class);
            startActivity(intent);
        });
    }
}