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
      //  initWindow();
        checkSDSize();
        checkSDSizeTimer();
    }

    private void initWindow() {
        mWifiManager = WIFIManager.getInstance(MainActivity.this);
        mFloatingManager = FloatingManager.getInstance(MainActivity.this);
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
              // addWindow();

                startService(intent);
                break;
            case R.id.bt_remove:
             //   mFloatingManager.removeView(view);
                stopService(intent);
                break;
        }
    }


    private void addWindow() {
        //mFloatingManager.addView(view, mParams);
        final TextView mTVAddress = view.findViewById(R.id.tv_address);
        Switch mSWToggle = view.findViewById(R.id.bt_toggle);
        final TextView mTVWiFiState = view.findViewById(R.id.tv_wifi_state);
        final TextView mTVWiFiSSID = view.findViewById(R.id.tv_wifi_ssid);

        if (mWifiManager.getWiFiState()) {
            mSWToggle.setChecked(true);
            getWiFiInfo(mTVAddress, mTVWiFiSSID);
            mTVWiFiState.setText("已打开");
        } else {
            mSWToggle.setChecked(false);
            mTVWiFiState.setText("已关闭");
        }

        mSWToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("main", isChecked + "");
                if (isChecked) {
                    boolean b = mWifiManager.wifiToggle();
                    if (b) {
                        buttonView.setChecked(true);
                        getWiFiInfo(mTVAddress, mTVWiFiSSID);
                        mTVWiFiState.setText("已打开");
                    }
                } else {
                    boolean b = mWifiManager.wifiToggle();
                    if (!b) {
                        buttonView.setChecked(false);
                        mTVWiFiState.setText("已关闭");
                    }
                }
            }
        });


/*        mBTToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();
                boolean b = mWifiManager.wifiToggle();
                if (!b) {
                    return;
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mBTAddress.setText("" + mWifiManager.getIpAddress());
                            }
                        });
                    }
                }, 2000L);
            }
        });*/
    }

    private void getWiFiInfo(final TextView tvAddress, final TextView tvSSID) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
                    final String ssid = connectionInfo.getSSID();
                    final String ipAddress = Formatter.formatIpAddress(connectionInfo.getIpAddress());

                    if (!ipAddress.equals("0.0.0.0")) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvAddress.setText(ipAddress);
                                tvSSID.setText(ssid);
                            }
                        });
                        break;
                    }
                }
            }
        }.start();
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
