package com.totalsize;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.widget.Toast;

/**
 * Created by RJYF-ZhangBo on 2018/3/26.
 */

public class WIFIManager {

    private static WIFIManager mManager;
    private final WifiManager mWifiManager;
    private Context mContext = null;

    public static WIFIManager getInstance(Context context) {
        if (mManager == null) {
            mManager = new WIFIManager(context);
        }
        return mManager;
    }

    public WIFIManager(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mContext = context;
    }

    public WifiInfo getConnectionInfo() {
        return mWifiManager.getConnectionInfo();
    }

    /**
     * @return true 为打开   false 为关闭
     */
    public boolean wifiToggle() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            Toast.makeText(mContext, "WIFI已经打开", Toast.LENGTH_SHORT).show();
            return true;
        } else if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
            Toast.makeText(mContext, "WIFI已经关闭", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getIpAddress() {
        int ipAddress = mWifiManager.getConnectionInfo().getIpAddress();
        return Formatter.formatIpAddress(ipAddress);


    }

    public boolean getWiFiState() {
        return mWifiManager.isWifiEnabled();
    }
}
