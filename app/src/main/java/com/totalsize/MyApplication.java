package com.totalsize;

import android.app.Application;
import android.content.Context;

/**
 * Created by LIANGSE on 2018/3/26.
 */

public class MyApplication extends Application {

    private static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }


    public static Context getContext() {
        return mContext;
    }
}
