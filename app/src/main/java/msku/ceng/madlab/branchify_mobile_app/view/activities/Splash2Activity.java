package msku.ceng.madlab.branchify_mobile_app.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import msku.ceng.madlab.branchify_mobile_app.R;

public class Splash2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        // 1. Find the button in the XML (write down whatever its ID is: btnNext, button2, etc.)
        ImageView nextArrow = findViewById(R.id.nextArrow2);

        // 2. write click event
        nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // home screen(MainActivity) pass code
                Intent intent = new Intent(Splash2Activity.this, MainActivity.class);
                startActivity(intent);
                finish(); // When you press the back button, it does not return to splash
            }
        });
    }
}