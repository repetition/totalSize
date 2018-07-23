package com.totalsize.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.totalsize.floatWindow.SuspensionWindowManager;
import com.totalsize.service.FloatDeniService;
import com.totalsize.service.FloatService;
import com.totalsize.floatWindow.FloatingManager;
import com.totalsize.MyApplication;
import com.totalsize.R;
import com.totalsize.utils.NetWorkManager;
import com.totalsize.utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.totalsize.floatWindow.FloatingManager.OVERLAY_PERMISSION_REQ_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Handler mHandler = new Handler();
    TextView mStorageDirectory;
    TextView mDataDirectory;
    public Context mContext = Utils.getContext();
    private WindowManager.LayoutParams mParams;
    private Button mBTWindow;
    private NetWorkManager mNetWorkManager;
    private FloatingManager mFloatingManager;
    private Utils utils;
    private View view;
    private Button mBTRemove;
    private Intent intent;
    private TextView mTVWifiInfo;
    private Button mBTWifiInfo;

    private static boolean isPermission = false;
    private Button mBTDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkSDSize();
        getWindow();
        AppManager.getAppManager().addActivity(this);
        //   checkSDSizeTimer();
    }

    private void initView() {
        mNetWorkManager = NetWorkManager.getInstance(MyApplication.getContext());
        mFloatingManager = FloatingManager.getInstance(MainActivity.this);
        mStorageDirectory = findViewById(R.id.StorageDirectory);
        mDataDirectory = findViewById(R.id.DataDirectory);
        mBTWindow = findViewById(R.id.bt_window);
        mBTWindow.setOnClickListener(this);
        mBTDemo = findViewById(R.id.bt_demo);
        mBTDemo.setOnClickListener(this);
        mBTRemove = findViewById(R.id.bt_remove);
        mBTRemove.setOnClickListener(this);
        mTVWifiInfo = findViewById(R.id.tv_wifi_info);
        mBTWifiInfo = findViewById(R.id.bt_wifiInfo);
        mBTWifiInfo.setOnClickListener(this);
        intent = new Intent(MainActivity.this, FloatService.class);
        view = FloatingManager.getInstance(this).getView();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_window:
                //检查权限
                isPermission = mFloatingManager.checkPermission(this);

                if (isPermission) {
                    startService(intent);
                    return;
                }
/*                if (!b) {
                    mFloatingManager.checkPermission(this);
                    return;
                }
                startService(intent);*/
                break;
            case R.id.bt_remove:
                stopService(intent);
                break;

            case R.id.bt_wifiInfo:

                List<WifiConfiguration> wifiConfigurations = mNetWorkManager.getWifiConfigurations();
                for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                    mTVWifiInfo.append(wifiConfiguration + toString() + "\n");
                }
                break;
            case R.id.bt_demo:

                boolean checkPermission = SuspensionWindowManager.getInstance(getApplicationContext()).checkPermission(this);
                if (checkPermission) {
                    Intent  demoIntent = new Intent(MainActivity.this, FloatDeniService.class);
                    startService(demoIntent);
                    return;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(mContext, "授权失败！请打开悬浮窗权限！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "授权成功！", Toast.LENGTH_SHORT).show();
                    //mFloatingManager.checkPermission(MainActivity.this);
                    isPermission = true;
                    //startService(intent);
                    Intent  demoIntent = new Intent(MainActivity.this, FloatDeniService.class);
                    startService(demoIntent);
                }
            }

        }

    }

    /**
     * 定时任务 监测机身存储大小
     */
    private void checkSDSizeTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("main", "定时器执行");
                final String sdTotalSize = utils.getSDTotalSize();
                final String sdAvailableSize = utils.getSDAvailableSize();
                final String romTotalSize = utils.getRomTotalSize();
                final String romAvailableSize = utils.getRomAvailableSize();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "sdTotalSize:" + sdTotalSize + ", sdAvailableSize:" + sdAvailableSize, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, 0, 5000L);
    }

    /**
     * 查看机身存储大小
     */
    private void checkSDSize() {
        utils = new Utils();
        new Thread() {
            @Override
            public void run() {
                super.run();
                final String sdTotalSize = utils.getSDTotalSize();
                final String sdAvailableSize = utils.getSDAvailableSize();
                final String romTotalSize = utils.getRomTotalSize();
                final String romAvailableSize = utils.getRomAvailableSize();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDataDirectory.setText("getRomTotalSize：" + romTotalSize + "  -  romAvailableSize: " + romAvailableSize);
                        Log.d("Main", "getRomTotalSize：" + romTotalSize + "  -  romAvailableSize: " + romAvailableSize);
                        mStorageDirectory.setText("sdTotalSize：" + sdTotalSize + "  -  sdAvailableSize: " + sdAvailableSize);
                        Log.d("Main", "sdTotalSize：" + sdTotalSize + "  -  sdAvailableSize: " + sdAvailableSize);
                    }
                });
            }
        }.start();
    }
    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void showBottomUIMenu() {
        // 显示虚拟按键
    }
}
