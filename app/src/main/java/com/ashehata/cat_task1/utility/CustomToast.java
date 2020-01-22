package com.ashehata.cat_task1.utility;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashehata.cat_task1.R;

public class CustomToast extends Toast {
    // Memory leak !!
    private static TextView mText ;
    // Memory leak !!
    private static CustomToast customToast;

    private CustomToast(Context context , String message, int duration ) {
        super(context);
        View mView = View.inflate(context,R.layout.custom_toast,null);
        mText = mView.findViewById(R.id.mText);
        setView(mView);
        setText(message);
        setDuration(duration);
    }

    public static void makeMessage(Context context,String message,int duration){
        // to create toast only one
        if(customToast != null){
            customToast.cancel();
        }

        // create a new toast
        customToast = new CustomToast(context,message,duration);
        customToast.show();

       // return customToast ;
    }

    public static void setText(String message) {
        if(mText!=null && message !=null){
            mText.setText(message);
        }
    }
}