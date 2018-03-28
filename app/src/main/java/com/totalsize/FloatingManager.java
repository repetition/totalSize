package com.totalsize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.provider.Settings;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
    private View view;
    private boolean isShow = false;
    private static final String TAG = FloatingManager.class.getName();

    private WindowManager.LayoutParams mParams;

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
     * 初始化
     */
    public void initWindow() {
        view = LayoutInflater.from(mContext).inflate(R.layout.window_suspend, null);
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.format = PixelFormat.TRANSPARENT;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        //  mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
/*        mParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_FULLSCREEN;*/
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
/*        mParams.width = view.getWidth();
        mParams.height = view.getHeight();*/
        mParams.x = 0;
        mParams.y = 0;
    }


    public void showView() {
        try {
            if (!isShow) {
                mWindowManager.addView(view, mParams);
                isShow = true;
                Log.d(TAG,"showView");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "请打开悬浮窗权限！", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideView() {
        if (isShow) {
            mWindowManager.removeView(view);
            isShow=false;
            Log.d(TAG,"hideView");
        }
    }

    public View getView() {
        return view;
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
            Toast.makeText(mContext, "请打开悬浮窗权限！", Toast.LENGTH_SHORT).show();
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
