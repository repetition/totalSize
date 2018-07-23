package com.totalsize.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.totalsize.floatWindow.SuspensionWindowManager;

/**
 * Created by RJYF-ZhangBo on 2018/3/27.
 */

public class FloatDeniService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SuspensionWindowManager instance = SuspensionWindowManager.getInstance(this);
        instance.createView();
        instance.showView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
