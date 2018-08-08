package com.totalsize.floatWindow;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.totalsize.MyApplication;
import com.totalsize.R;
import com.totalsize.activity.AppManager;
import com.totalsize.ftp.FTPServer;
import com.totalsize.receiver.NetworkReceiver;
import com.totalsize.utils.ADBUtil;
import com.totalsize.utils.CommonUtil;
import com.totalsize.utils.NetWorkManager;
import com.totalsize.utils.StatusBar;
import com.totalsize.utils.ToastUtils;
import com.totalsize.utils.Utils;

public class SuspensionWindowManager {
    private static SuspensionWindowManager suspensionWindowManager;
    // private final ImageView view;
    private final View view;
    private final WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private Context mContext;

    public static int OVERLAY_PERMISSION_REQ_CODE = 100;

    private boolean initViewPlace;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private boolean isShow = false;
    private final NetWorkManager netWorkManager;
    private boolean isDebug;

    public static SuspensionWindowManager getInstance(Context context) {
        if (suspensionWindowManager == null) {
            suspensionWindowManager = new SuspensionWindowManager(context);
        }
        return suspensionWindowManager;
    }

    public SuspensionWindowManager(final Context context) {
        netWorkManager = NetWorkManager.getInstance(context);


        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        view = LayoutInflater.from(context).inflate(R.layout.window_suspend, null);
        //  view = new ImageView(context);
        // view.setBackgroundResource(R.mipmap.ic_launcher);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        initListener(view);
    }

    private void initListener(View view) {
        //悬浮窗拖动
        viewTouch(view);

        final TextView mTVAddress = view.findViewById(R.id.tv_address);
        final Switch mSWToggle = view.findViewById(R.id.bt_toggle);
        final TextView mTVWiFiSSID = view.findViewById(R.id.tv_wifi_ssid);
        final TextView mTVFtp = view.findViewById(R.id.tv_ftp);
        ImageButton button = view.findViewById(R.id.tv_go_home);
        ImageButton fullScreen = view.findViewById(R.id.tv_full_screen);
        ImageButton debugButton = view.findViewById(R.id.ib_adb_debut);
        final ImageButton ftpButton = view.findViewById(R.id.ib_ftp_server);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                // 为Intent设置Action、Category属性
                intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
                intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        //全屏
        fullScreen.setOnClickListener(new View.OnClickListener() {
            private boolean isFullScreen;

            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    isFullScreen = false;
                    StatusBar.hidden();
                    ToastUtils.showMsg("全屏");
                } else {
                    StatusBar.show();
                    isFullScreen = true;
                    ToastUtils.showMsg("取消全屏");
                }
            }
        });
        //网络调试开启
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDebug) {
                    isDebug = false;
                    ADBUtil.adbStop();
                } else {
                    ADBUtil.adbStart();
                    isDebug = true;
                }
            }
        });
        //ftp服务
        ftpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ftpIntent = new Intent(mContext, FTPServer.class);
                Bundle bundle = new Bundle();
                String localIpAddress = Utils.getLocalIpAddress();
                bundle.putString("ip", localIpAddress);
                ftpIntent.putExtras(bundle);
                SharedPreferences sp = MyApplication.getContext().getSharedPreferences("conf", Context.MODE_PRIVATE);
                boolean isRun = sp.getBoolean("isRun", false);
                if (isRun) {
                    mContext.stopService(ftpIntent);
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("conf", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isRun", false);
                    editor.apply();
                    ToastUtils.showMsg("ftp stopping!");
                    return;
                }
                ComponentName componentName = mContext.startService(ftpIntent);
                Log.i("11", "服务启动 | " + localIpAddress + "========" + componentName);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("conf", Context.MODE_PRIVATE).edit();
                editor.putBoolean("isRun", true);
                editor.apply();
                mTVFtp.setText("ftp://"+localIpAddress+":1111");
            }
        });

        boolean wifiEnabled = netWorkManager.isWifiEnabled();
        if (wifiEnabled) {
            mSWToggle.setChecked(true);
            String ipAddress = netWorkManager.getIpAddress();
            mTVAddress.setText(ipAddress);
            mTVWiFiSSID.setText(netWorkManager.getConnectionInfo().getSSID());
        } else {
            mSWToggle.setChecked(false);
            mTVWiFiSSID.setText("Unknown");
            mTVAddress.setText("Unknown");
        }

        mSWToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (netWorkManager.isWifiEnabled()) {
                        mSWToggle.setChecked(true);
                    } else {
                        netWorkManager.wifiToggle(true);
                        netWorkManager.connectWifi("ThinkWin-WiFi", "810ThinkWin811");
                        new Thread() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep(1000L);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    String ipAddress = netWorkManager.getIpAddress();

                                    if (!ipAddress.contains("0.0.0.0")) {
                                        break;
                                    }
                                }
                                Utils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String ipAddress = netWorkManager.getIpAddress();
                                        mTVAddress.setText(ipAddress);
                                        mTVWiFiSSID.setText(netWorkManager.getConnectionInfo().getSSID());
                                    }
                                });
                            }
                        }.start();

                    }
                } else {
                    if (netWorkManager.isWifiEnabled()) {
                        netWorkManager.wifiToggle(false);
                    } else {
                        mSWToggle.setChecked(false);
                        mTVWiFiSSID.setText("Unknown");
                        mTVAddress.setText("Unknown");
                    }
                }
            }
        });

        NetworkReceiver.setmOnNetWordListener(new NetworkReceiver.onNetWordListener() {
            @Override
            public void onNetOpen() {

            }

            @Override
            public void onNetConnected() {
                String ipAddress = netWorkManager.getIpAddress();
                mTVAddress.setText(ipAddress);
                mTVWiFiSSID.setText(netWorkManager.getConnectionInfo().getSSID());
            }

            @Override
            public void onNetDisabled() {
                mTVWiFiSSID.setText("Unknown");
                mTVAddress.setText("Unknown");
            }
        });
    }

    private void viewTouch(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!initViewPlace) {
                            initViewPlace = true;
                            //获取初始位置
                            mTouchStartX = event.getRawX();
                            mTouchStartY = event.getRawY();
                            x = event.getRawX();
                            y = event.getRawY();
                        } else {
                            //根据上次手指离开的位置与此次点击的位置进行初始位置微调
                            mTouchStartX += (event.getRawX() - x);
                            mTouchStartY += (event.getRawY() - y);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取相对屏幕的坐标，以屏幕左上角为原点
                        x = event.getRawX();
                        y = event.getRawY();
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        params.x = (int) (x - mTouchStartX);
        params.y = (int) (y - mTouchStartY);
        mWindowManager.updateViewLayout(view, params);
    }

    public void createView() {
        params = new WindowManager.LayoutParams();
        params.gravity = Gravity.TOP | Gravity.LEFT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.x = 0;
        params.y = 0;

        params.format = PixelFormat.TRANSPARENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
    }

    public void showView() {
        if (!isShow) {
            mWindowManager.addView(view, params);
            isShow=true;
        }else {
            hideView();
        }
    }

    public void hideView() {
        if (isShow) {
            mWindowManager.removeView(view);
            isShow=false;
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


    public void showBottomMenu() {
        Activity activity = AppManager.getAppManager().currentActivity();
        CommonUtil.cancelFullScreen(activity);
    }

    /**
     * 全屏切换
     */
    public void fullScreenChange() {
        Activity activity = AppManager.getAppManager().currentActivity();
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean fullScreen = mPreferences.getBoolean("fullScreen", false);
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        System.out.println("fullScreen的值:" + fullScreen);
        if (fullScreen) {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
//取消全屏设置
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mPreferences.edit().putBoolean("fullScreen", false).commit();
        } else {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mPreferences.edit().putBoolean("fullScreen", true).commit();
        }
    }
}
