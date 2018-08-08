package com.totalsize;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.log4j.Logger;
import org.slf4j.event.EventConstants;
import org.slf4j.event.Level;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by LIANGSE on 2018/3/26.
 */

public class MyApplication extends Application {

    private static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        InitLog4jConfig();
      //  configLog("log");

        CrashHandler.getInstance().init(mContext);
    }


    public static Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mListener != null) {
                mListener.handleMessage(msg);
            }
            System.out.println("mListener 地址值==== " + mListener);
        }
    };


    private static HandlerListener mListener;

    public static void setOnHandlerListener(HandlerListener listener) {
        mListener = listener;
    }

    public static HandlerListener getListener() {
        return mListener;
    }

    public interface HandlerListener {
        void handleMessage(Message msg);
    }

    private void InitLog4jConfig() {
 /*       Properties props = null;
        FileInputStream fis = null;
        try {
            // 从配置文件dbinfo.properties中读取配置信息
            props = new Properties();
            //   getClass().getResourceAsStream("log4j.properties");
            //   fis = new FileInputStream(getClass().getResource("log4j.properties").getFile());
            InputStream inputStream = getAssets().open("log4j.properties");
            props.load(inputStream);
            PropertyConfigurator.configure(props);//装入log4j配置信息
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }*/
    }

    private void configLog(String logFileNamePrefix) {
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(getFileName(logFileNamePrefix));
        logConfigurator.setRootLevel(org.apache.log4j.Level.INFO);
        logConfigurator.setLevel("org.apache", org.apache.log4j.Level.INFO);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 2);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.configure();
    }

    @NonNull
    private String getFileName(String logFileNamePrefix) {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + "logdemo" + File.separator + "logs"
                + File.separator + (logFileNamePrefix == null ? "" : logFileNamePrefix) + "log4j.txt";

        File file = new File(path);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // file.createNewFile();
        }
            return path;
    }

    private void create() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //如果为挂载状态，那么就通过Environment的getExternalStorageDirectory()方法来获取
           // 外置存储卡的目录，然后加上我们自己要创建的文件名（记住文件名前要加一个"/"），这样
           // 就生成了我们要创建的文件路径。
            String path = Environment.getExternalStorageDirectory() + "/zhiyuan.txt";
            //新建一个File对象，把我们要建的文件路径传进去。
            File file = new File(path);
            //方便查看，在控制台打印一下我们的存储卡目录。
            Log.d("=====TAG=====", "onClick: "+Environment.getExternalStorageDirectory());
            //判断文件是否存在，如果存在就删除。
            if (file.exists()) {
                file.delete();
            }
            try {
                //通过文件的对象file的createNewFile()方法来创建文件
                file.createNewFile();
                //新建一个FileOutputStream()，把文件的路径传进去
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                //给定一个字符串，将其转换成字节数组
                byte[] bytes = "你好啊，今天天气不错！".getBytes();
                //通过输出流对象写入字节数组
                fileOutputStream.write(bytes);
                //关流
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
