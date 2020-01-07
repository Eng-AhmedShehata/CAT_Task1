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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                // close splash activity
                finish();
            }
        },DELAY );
    }
}