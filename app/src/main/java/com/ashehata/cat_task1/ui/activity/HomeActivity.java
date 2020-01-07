package com.ashehata.cat_task1.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ashehata.cat_task1.R;
import com.ashehata.cat_task1.utility.HelperMethod;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    NavController navController;
    @BindView(R.id.mToolbar)
    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        navController =HelperMethod.initializeNavigation(this);
    }

    @Override
    public void onBackPressed() {
        if (!navController.popBackStack()) {
            finish();
        }
    }
}