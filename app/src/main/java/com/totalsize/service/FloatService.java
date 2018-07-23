package com.totalsize.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.totalsize.floatWindow.FloatingManager;
import com.totalsize.R;
import com.totalsize.utils.NetWorkManager;
import com.totalsize.utils.Utils;
import com.totalsize.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.totalsize.activity.MainActivity.mHandler;

/**
 * Created by RJYF-ZhangBo on 2018/3/27.
 */

public class FloatService extends Service {

    private static final String TAG = FloatService.class.getName();
    boolean isHome = false;
    private View view;
    private NetWorkManager mNetWorkManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
/*        FloatBallManager.getInstance(this).showFloatWindow();
        FloatBallManager.getInstance(this).showView();
        view = FloatBallManager.getInstance(this).getView();*/

        FloatingManager.getInstance(this).initWindow2();
        FloatingManager.getInstance(this).showView();
       view = FloatingManager.getInstance(this).getView();
        mNetWorkManager = NetWorkManager.getInstance(this);

        initListener();
      //  checkSDSizeTimer();
        Toast.makeText(this,"启动了",Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkSDSizeTimer() {
        final Utils utils = new Utils();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final String sdTotalSize = utils.getSDTotalSize();
                final String sdAvailableSize = utils.getSDAvailableSize();
                final String romTotalSize = utils.getRomTotalSize();
                final String romAvailableSize = utils.getRomAvailableSize();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FloatService.this, "sdTotalSize:" + sdTotalSize + ", sdAvailableSize:" + sdAvailableSize, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, 0, 5000L);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatingManager.getInstance(this).hideView();
        Log.d(TAG, "onDestroy");
        Toast.makeText(this,"onDestroy",Toast.LENGTH_SHORT).show();
    }

    public void initListener() {

        final TextView mTVAddress = view.findViewById(R.id.tv_address);
        Switch mSWToggle = view.findViewById(R.id.bt_toggle);
        final TextView mTVWiFiSSID = view.findViewById(R.id.tv_wifi_ssid);
        Button Button = view.findViewById(R.id.tv_go_home);

        if (mNetWorkManager.getWiFiState()) {
            mSWToggle.setChecked(true);
            getWiFiInfo(mTVAddress, mTVWiFiSSID);
        } else {
            mSWToggle.setChecked(false);
        }


        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHome) {
                    try {

                        Intent intent = new Intent();
                        // 为Intent设置Action、Category属性
                        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
                        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        isHome = true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    startActivity(new Intent(FloatService.this, MainActivity.class));
                    isHome = false;
                }

            }
        });

/*        mSWToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("main", isChecked + "");
                if (isChecked) {
                    boolean b = mNetWorkManager.wifiToggle();
                    if (b) {
                        buttonView.setChecked(true);
                        getWiFiInfo(mTVAddress, mTVWiFiSSID);
                    }
                } else {
                    boolean b = mNetWorkManager.wifiToggle();
                    if (!b) {
                        buttonView.setChecked(false);
                    }
                }
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
                    WifiInfo connectionInfo = mNetWorkManager.getConnectionInfo();
                    final String ssid = connectionInfo.getSSID();
                    final String ipAddress = Formatter.formatIpAddress(connectionInfo.getIpAddress());
                    Log.d(TAG, "ipAddress:" + ipAddress);
                    //  mNetWorkManager.startScan();

                    if (!ipAddress.equals("0.0.0.0")) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvAddress.setText(ipAddress);
                                tvSSID.setText(ssid);
                                Log.d(TAG, "wifi已经连接！+\n" + ssid + "\n" + ipAddress);
                            }
                        });
                        break;
                    }
                }
            }
        }.start();
    }

}
