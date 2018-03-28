package com.totalsize;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import static com.totalsize.MainActivity.mHandler;

/**
 * Created by RJYF-ZhangBo on 2018/3/27.
 */

public class FloatService extends Service {

    private static final String TAG = FloatService.class.getName();

    private View view;
    private WIFIManager mWifiManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FloatingManager.getInstance(this).initWindow();

        FloatingManager.getInstance(this).showView();

        view = FloatingManager.getInstance(this).getView();

        mWifiManager = WIFIManager.getInstance(this);

        initListener();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatingManager.getInstance(this).hideView();
        Log.d(TAG,"onDestroy");
    }

    public void initListener() {

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
                    Log.d(TAG,"ipAddress:"+ ipAddress);

                    if (!ipAddress.equals("0.0.0.0")) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvAddress.setText(ipAddress);
                                tvSSID.setText(ssid);
                                Log.d(TAG,"wifi已经连接！+\n"+ssid+"\n"+ipAddress);
                            }
                        });
                        break;
                    }
                }
            }
        }.start();
    }

}
