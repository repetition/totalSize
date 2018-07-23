package com.totalsize.floatWindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.totalsize.R;
import com.totalsize.utils.Utils;

/**
 * Created by xiongxunxiang on 2017/2/14.
 * 悬浮窗管理器，单例模式创建
 */

public class FloatBallManager {
    private static FloatBallManager sFloatBallManager;
    private View view;
    private Context mContext;
    private WindowManager mWindowManager;
    private float mStartRawX;
    private float mStartRawY;
    private WindowManager.LayoutParams mParams;

    private FloatBallManager(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    //    view = LayoutInflater.from(context).inflate(R.layout.window_suspend, null);
        view = new ImageView(context);
        view.setBackgroundResource(R.mipmap.ic_launcher);
    }

    public static FloatBallManager getInstance(Context context) {
        if (sFloatBallManager == null) {
            sFloatBallManager = new FloatBallManager(context);
        }
        return sFloatBallManager;
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartRawX = event.getRawX();
                    mStartRawY = event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float rawX2 = event.getRawX();
                    float rawY2 = event.getRawY();

                    float dx = rawX2 - mStartRawX;
                    float dy = rawY2 - mStartRawY;

                    mParams.x += dx;
                    mParams.y += dy;

                    mWindowManager.updateViewLayout(view, mParams);


                    mStartRawX = rawX2;
                    mStartRawY = rawY2;
                    break;

                case MotionEvent.ACTION_UP:
                    float rawX3 = event.getRawX();
                    float rawY3 = event.getRawY();


                    if (rawX3 >= (Utils.getScreenWidth(mContext) / 2)) {
                        mParams.x = Utils.getScreenWidth(mContext) - view.getWidth() / 2;
                    } else {
                        mParams.x = 0;
                    }
                    mWindowManager.updateViewLayout(view, mParams);


                    if (Math.abs(rawX3 - mStartRawX) > 5 || Math.abs(rawY3 - mStartRawY) > 5) {
                        return true;
                    } else {
                        return false;
                    }

                default:
                    break;
            }
            return false;
        }
    };

  /*  private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "点击了悬浮小球", Toast.LENGTH_SHORT).show();
            // 隐藏自身
            mWinMgr.removeView(view);
            // 显示底部
            showBottomWindow();
        }
    };*/

    /**
     * 显示悬浮小球
     */
    public void showFloatWindow() {
        mParams = new WindowManager.LayoutParams();
/*        mParams.width = view.getWidth();
        mParams.height = view.getHeight();*/
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = 0;
        mParams.y = 0;
        // 如果使用TYPE_TOAST，可以不用使用悬浮窗权限，解决小米等手机无法显示的问题.https://gold.xitu.io/entry/5621a9cb60b27457e85c8c07
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
          //  mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        // mParams.format = PixelFormat.RGBA_8888;
        mParams.format = PixelFormat.TRANSPARENT;
      //  mWindowManager.addView(view, mParams);
        Toast.makeText(mContext, "show", Toast.LENGTH_SHORT).show();
    }

    public void showView() {
        mWindowManager.addView(view, mParams);
    }

    public void hideView() {
        mWindowManager.removeView(view);
    }

    public View getView() {
        return view;
    }

    /*    *//**
     * 显示底部窗口
     *//*
    public void showBottomWindow() {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        mParams.width = Utils.getScreenWidth(mContext);
        mParams.height = Utils.getScreenHeight(mContext) - Utils.getStatusBarHeight(mContext);
        mParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mParams.x = 0;
        mParams.y = 0;
        // 如果使用TYPE_TOAST，可以不用使用悬浮窗权限，解决小米等手机无法显示的问题.https://gold.xitu.io/entry/5621a9cb60b27457e85c8c07
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParams.format = PixelFormat.RGBA_8888;
        mWinMgr.addView(mBottomWindow, mParams);
    }

    public void hideBottomWindow() {
        if (mBottomWindow != null) {
            mWinMgr.removeView(mBottomWindow);
        }
        showFloatWindow();
    }*/
}
