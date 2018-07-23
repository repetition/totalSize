package com.totalsize.floatWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.totalsize.R;

import java.lang.reflect.Field;


/**
 * Created by RJYF-ZhangBo on 2018/3/26.
 */

public class FloatingManager implements View.OnTouchListener{
    private static FloatingManager floatingManager;
    private WindowManager mWindowManager;
    private Context mContext;
    public static int OVERLAY_PERMISSION_REQ_CODE = 100;
    private View view;
    private boolean isShow = false;
    private static final String TAG = FloatingManager.class.getName();

    private WindowManager.LayoutParams mParams;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;
    private int screenWidth;
    private int screenHeight;

    float downX = 0;
    float downY = 0;
    private int viewX;
    private int viewY;

    public static FloatingManager getInstance(Context context) {
        if (floatingManager == null) {
            floatingManager = new FloatingManager(context);
        }
        return floatingManager;
    }

    public FloatingManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mContext = context;
        view = LayoutInflater.from(mContext).inflate(R.layout.window_suspend, null);
        view.setOnTouchListener(this);
    }

    /**
     * 初始化
     */
    public void initWindow() {

/*        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);

        //通过像素密度来设置按钮的大小
        int dpi = dpi(dm.densityDpi);
        //屏宽
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        //屏高
        screenHeight = mWindowManager.getDefaultDisplay().getHeight();*/

        view = LayoutInflater.from(mContext).inflate(R.layout.window_suspend, null);
        view.setOnTouchListener(this);
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        // mParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
           // mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
             mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

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
/*        mmParams.width = view.getWidth();
        mmParams.height = view.getHeight();*/
        mParams.x = 0;
        mParams.y = 0;
    }

    /**
     * 初始化
     */
    public void initWindow2() {
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.TOP | Gravity.LEFT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mParams.x = 0;
        mParams.y = 0;

        mParams.format = PixelFormat.TRANSPARENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
    }
    public void showView() {
        if (!isShow) {
            mWindowManager.addView(view, mParams);
            isShow = true;
            Log.d(TAG, "showView");
        }
    }

    public void hideView() {
        if (isShow) {
            mWindowManager.removeView(view);
            isShow = false;
            Log.d(TAG, "hideView");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                Toast.makeText(mContext, "请打开悬浮窗权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName()));
                activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                viewX = mParams.x;
                viewY = mParams.y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 更新浮动窗口位置参数
                float moveX = event.getX();
                float moveY =  event.getY();

                mParams.x += (moveX - downX)/3;
                mParams.y += (moveY - downY)/3;
                // 手指移动的时候更新小悬浮窗的位置
                updateView(view,mParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }


    // 获取系统状态栏高度
    public static int getSysBarHeight(Context contex) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = contex.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * 根据密度选择控件大小
     *
     */
    private int dpi(int densityDpi) {
        if (densityDpi <= 120) {
            return 36;
        } else if (densityDpi <= 160) {
            return 48;
        } else if (densityDpi <= 240) {
            return 72;
        } else if (densityDpi <= 320) {
            return 96;
        }
        return 108;
    }
}
