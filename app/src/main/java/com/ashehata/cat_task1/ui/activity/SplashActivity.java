package com.ashehata.cat_task1.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ashehata.cat_task1.R;

public class SplashActivity extends AppCompatActivity {

    public static final int DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        // open home activity
        openHome();

    }

    private void openHome() {
        // Use Java 8 lambda
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);

            // In some slow phones, using finish will close the app then reopen it, and you will have a laggy experience
            // which is bad UX
            // Instead you set these flags to tell the os to finish the current activity and start the target.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }, DELAY);
    }
}