package com.totalsize;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by RJYF-ZhangBo on 2018/3/26.
 */

public class WIFIManager {

    private static final String TAG = WIFIManager.class.getName();

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

    public List<WifiConfiguration> startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        List<ScanResult> scanResults = mWifiManager.getScanResults();

        // 得到配置好的网络连接
        List<WifiConfiguration> mWifiManagerConfiguredNetworks = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration mWifiManagerConfiguredNetwork : mWifiManagerConfiguredNetworks) {

            Log.d(TAG, mWifiManagerConfiguredNetwork.toString());
            // mWifiManager.enableNetwork(mWifiManagerConfiguredNetwork.networkId,true);
        }

        return mWifiManagerConfiguredNetworks;
    }

    public List<WifiConfiguration> getWifiConfigurations() {
        // 得到配置好的网络连接
        List<WifiConfiguration> mWifiManagerConfiguredNetworks = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration mWifiManagerConfiguredNetwork : mWifiManagerConfiguredNetworks) {

            Log.d(TAG, mWifiManagerConfiguredNetwork.toString());
            boolean b = mWifiManager.enableNetwork(mWifiManagerConfiguredNetwork.networkId, true);
          //  int i = mWifiManager.addNetwork(mWifiManagerConfiguredNetwork);
            Log.d(TAG,b+"");
        }

        return mWifiManagerConfiguredNetworks;
    }



    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
  /*          if (existingConfig.SSID.equals("/""+SSID+"/""))
            {
                return existingConfig;
            }*/
        }
        return null;
    }
}
