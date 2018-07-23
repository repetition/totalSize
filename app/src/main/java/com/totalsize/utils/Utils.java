package com.totalsize.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.WindowManager;

import com.totalsize.MyApplication;

import java.io.File;


/**
 * Created by LIANGSE on 2018/3/26.
 */

public class Utils {
    private static Handler handler = new Handler();

    public static void runOnUIThread(Runnable runnable){
        handler.post(runnable);
    }

    public static Context getContext() {
        return MyApplication.getContext();
    }


    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(getContext(), blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(getContext(), blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(getContext(), blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(getContext(), blockSize * availableBlocks);
    }

    public static int getScreenWidth(Context context) {
        Point point = new Point();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        return point.x;
    }

    public static int getScreenHeight(Context context) {
        Point point = new Point();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        return point.y;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
