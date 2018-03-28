package com.totalsize;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;

import static com.totalsize.FloatingManager.OVERLAY_PERMISSION_REQ_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Handler mHandler = new Handler();
    TextView mStorageDirectory;
    TextView mDataDirectory;
    public Context mContext = Utils.getContext();
    private WindowManager.LayoutParams mParams;
    private Button mBTWindow;
    private WIFIManager mWifiManager;
    private FloatingManager mFloatingManager;
    private Utils utils;
    private View view;
    private Button mBTRemove;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkSDSize();
        checkSDSizeTimer();
    }
    private void initView() {
        mFloatingManager = FloatingManager.getInstance(MainActivity.this);
        mStorageDirectory = findViewById(R.id.StorageDirectory);
        mDataDirectory = findViewById(R.id.DataDirectory);
        mBTWindow = findViewById(R.id.bt_window);
        mBTWindow.setOnClickListener(this);
        mBTRemove = findViewById(R.id.bt_remove);
        mBTRemove.setOnClickListener(this);
        intent = new Intent(MainActivity.this, FloatService.class);
        view = FloatingManager.getInstance(this).getView();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_window:
                boolean b = mFloatingManager.checkPermission(this);
                if (!b) {
                    mFloatingManager.checkPermission(this);
                    return;
                }
                startService(intent);
                break;
            case R.id.bt_remove:
                stopService(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(mContext, "授权失败！请打开悬浮窗权限！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "授权成功！", Toast.LENGTH_SHORT).show();
                    mFloatingManager.checkPermission(MainActivity.this);
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


}
