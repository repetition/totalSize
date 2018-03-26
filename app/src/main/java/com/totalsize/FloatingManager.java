package com.totalsize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * Created by RJYF-ZhangBo on 2018/3/26.
 */

public class FloatingManager {
    private static FloatingManager floatingManager;
    private WindowManager mWindowManager;
    private Context mContext;
    public static int OVERLAY_PERMISSION_REQ_CODE = 0;

    public static FloatingManager getInstance(Context context) {
        if (floatingManager == null) {
            floatingManager = new FloatingManager(context);
        }
        return floatingManager;
    }

    public FloatingManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mContext = context;
    }

    /**
     * 添加悬浮窗
     *
     * @param view
     * @param params
     * @return
     */
    public boolean addView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.addView(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"请打开悬浮窗权限！",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 删除悬浮窗
     *
     * @param view
     * @return
     */
    public boolean removeView(View view) {

        try {
            mWindowManager.removeView(view);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新悬浮窗
     *
     * @param view
     * @param params
     * @return
     */
    public boolean updateView(View view, WindowManager.LayoutParams params) {

        try {
            mWindowManager.updateViewLayout(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(mContext)) {
                Toast.makeText(mContext, "请打开悬浮窗权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName()));
                activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                return true;
            }
        }
        return true;
    }


}
