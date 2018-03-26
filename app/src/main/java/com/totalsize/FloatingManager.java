package com.totalsize;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by RJYF-ZhangBo on 2018/3/26.
 */

public class FloatingManager {
    private static FloatingManager floatingManager;
    private WindowManager mWindowManager;
    private Context mContext;

    public static FloatingManager getInstance(Context context) {
        if (floatingManager == null) {
            floatingManager = new FloatingManager(context);
        }
        return floatingManager;
    }

    public FloatingManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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
}
