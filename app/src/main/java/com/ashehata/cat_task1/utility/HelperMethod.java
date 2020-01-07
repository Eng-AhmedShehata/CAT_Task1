package com.ashehata.cat_task1.utility;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashehata.cat_task1.R;

public class HelperMethod {
    private static LinearLayoutManager linearLayoutManager;
    private static NavController navController;

    public static void disappearKeypad(Activity activity, View v) {
        try {
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }
    //set recycler view configuration
    public static void setRecyclerConfig(RecyclerView recyclerView , Context context) {

        linearLayoutManager = new LinearLayoutManager(context);
        // Set items on linear manager
        recyclerView.setLayoutManager(linearLayoutManager);

        // Fixed size
        recyclerView.setHasFixedSize(true);
    }

    public static void displayMessage(Activity activity,String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomToast.makeMessage(activity,message,
                        Toast.LENGTH_SHORT);
            }
        });
    }

    public static NavController initializeNavigation(Activity activity) {
        navController = Navigation.findNavController(activity, R.id.navi_host_fragment);
        return navController ;
    }
}