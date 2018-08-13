package com.totalsize.ftp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.totalsize.MyApplication;
import com.totalsize.R;
import com.totalsize.activity.AppManager;
import com.totalsize.utils.ToastUtils;
import com.totalsize.utils.Utils;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;

public class FTPServer extends Service {

    private static final String TAG = "FTPServer";
    private static String hostip = ""; // 本机IP
    private static final int PORT = 1111;
    // sd卡目录
    @SuppressLint("SdCardPath")
    private static final String dirname = "/mnt/sdcard/";
    // ftp服务器配置文件路径
    private static final String filename = dirname + "totalSize/users.properties";
    private FtpServer mFtpServer = null;
    private static Handler mHandler = new Handler();
    private String ip = null;
    private String CHANNEL_ID = "com.totalsize.ftp";
    private CharSequence CHANNEL_NAME = "FTPServer";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
}


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Intent ftpIntent = new Intent(this, FTPServer.class);
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }*/

            Bundle bundle = intent.getExtras();
            ip = bundle.getString("ip");

            if (null == ip) {
                ip = Utils.getLocalIpAddress();
                Toast.makeText(MyApplication.getContext(), ip, Toast.LENGTH_SHORT).show();
            }
            PendingIntent notificationIntent = PendingIntent.getActivity(this, 0, ftpIntent, 0);
            Notification.Builder noti = new Notification.Builder(this)
                    .setContentTitle("FTPServerRunning")
                    .setContentText("ftp://"+ip+":1111")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(notificationIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                noti.setChannelId(CHANNEL_ID);
            }
            startForeground(123456, noti.build());
            new Thread(){
                @Override
                public void run() {
                    startFtpServer(ip);
                    Log.d(TAG, "ip" + ip + "port:" + PORT);
                    Log.d(TAG, "ftp started ！------------------------------------------------------------------------");
                    ToastUtils.showMsg("ftp started!");
                }
            }.start();
            MyApplication.getHandler().sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Thread() {
            @Override
            public void run() {
                super.run();
                stopFtpServer(ip);
            }
        }.start();
        stopForeground(true);//取消最高级进程
        MyApplication.getHandler().sendEmptyMessage(2);
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("conf", Context.MODE_PRIVATE).edit();
        editor.putBoolean("isRun", false);
        editor.apply();
    }


    /**
     * 开启FTP服务器
     *
     * @param hostIp 本机ip
     */
    private void startFtpServer(String hostIp) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        // 设置监听IP和端口号
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(PORT);
        factory.setServerAddress(hostIp);
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        File files = new File(filename);
        //设置配置文件
        userManagerFactory.setFile(files);
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        serverFactory.addListener("default", factory.createListener());
        // start the server
        mFtpServer = serverFactory.createServer();
        try {
            mFtpServer.start();
            SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("conf", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isRun", true);
            editor.apply();
            Log.d(TAG, "开启了FTP服务器  ip = " + hostip);
        } catch (Exception e) {
            e.printStackTrace();
            SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("conf", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isRun", false);
            editor.apply();
        }
    }

    /**
     * 关闭FTP服务器
     */
    private void stopFtpServer(String ip) {
        if (mFtpServer != null) {
            mFtpServer.stop();
            mFtpServer = null;
            SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("conf", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isRun", false);
            editor.apply();
            mHandler.sendEmptyMessage(0x0002);
            Log.d(TAG, "关闭了FTP服务器 ip = " + ip);
        } else {
            mHandler.sendEmptyMessage(0x0004);
        }
    }
}
