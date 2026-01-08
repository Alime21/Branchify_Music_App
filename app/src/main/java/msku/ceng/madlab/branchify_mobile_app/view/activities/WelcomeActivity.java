package msku.ceng.madlab.branchify_mobile_app.view.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.MotionEvent;
import android.content.Intent;

import msku.ceng.madlab.branchify_mobile_app.R;

public class WelcomeActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable navigateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Auto-navigate to Splash1Activity after 0.5 seconds
        handler = new Handler(Looper.getMainLooper());
        navigateRunnable = this::navigateToSplash1;
        handler.postDelayed(navigateRunnable, 500);
    }

    private void navigateToSplash1() {
        if (handler != null) {
            handler.removeCallbacks(navigateRunnable);
        }
        Intent intent = new Intent(WelcomeActivity.this, Splash1Activity.class);
        startActivity(intent);
        finish();
    }

    // when you click anywhere on the screen,it will go to Splash1Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            navigateToSplash1();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(navigateRunnable);
        }
    }
}