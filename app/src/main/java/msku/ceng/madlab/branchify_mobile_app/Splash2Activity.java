package msku.ceng.madlab.branchify_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        ImageView nextArrow = findViewById(R.id.nextArrow2);

        // when you click arrow it will go to MainActivity
        nextArrow.setOnClickListener(v -> {
            Intent intent = new Intent(Splash2Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}