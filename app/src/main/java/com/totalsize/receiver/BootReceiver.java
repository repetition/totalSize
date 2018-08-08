package com.totalsize.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.totalsize.MyApplication;
import com.totalsize.ftp.FTPServer;
import com.totalsize.service.FloatDeniService;
import com.totalsize.utils.Utils;

public class BootReceiver extends BroadcastReceiver {

    private static String TAG = BootReceiver.class.getName();
    private SharedPreferences.Editor editor;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
  /*          MyApplication.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    *//* 服务开机自启动 *//*
                    Intent ftpIntent = new Intent(context, FTPServer.class);
                    Bundle bundle = new Bundle();
                    String localIpAddress = Utils.getLocalIpAddress();
                    bundle.putString("id", localIpAddress);
                    ftpIntent.putExtras(bundle);
                    ComponentName componentName = context.startService(ftpIntent);

                    Toast.makeText(context, "服务启动！" + localIpAddress, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "服务启动 | " + localIpAddress + "========" + componentName);

                    editor = context.getSharedPreferences("conf", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isRun",true);
                    editor.commit();
                }
            }, 2000L);
        }*/
            Log.i(TAG, "已经开机，正在启动服务！");
            Intent floatIntent = new Intent(context, FloatDeniService.class);
            context.startService(floatIntent);
        }
    }
}
