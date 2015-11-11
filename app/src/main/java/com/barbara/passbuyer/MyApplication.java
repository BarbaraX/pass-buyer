package com.barbara.passbuyer;

import android.app.Application;
import android.content.Context;

/**
 * Created by barbara on 7/21/15.
 */
public class MyApplication extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
