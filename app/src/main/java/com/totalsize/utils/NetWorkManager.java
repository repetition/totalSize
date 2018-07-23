package com.totalsize.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by RJYF-ZhangBo on 2018/3/26.
 */

public class NetWorkManager {

    private static final String TAG = NetWorkManager.class.getName();
    private static NetWorkManager mManager;

    private  WifiManager mWifiManager;
    private Context mContext = null;

    public static NetWorkManager getInstance(Context context) {
        if (mManager == null) {
            mManager = new NetWorkManager(context);
        }
        return mManager;
    }

    public NetWorkManager(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mContext = context;
    }

    public WifiInfo getConnectionInfo() {
        return mWifiManager.getConnectionInfo();
    }
    // 开始扫描 WIFI.
    public  void startScanWifi() {
            mWifiManager.startScan();
    }
    /**
     * @return true 为打开   false 为关闭
     */
    public boolean wifiToggle(boolean isEnabled) {
        boolean enabled = mWifiManager.setWifiEnabled(isEnabled);
        startScanWifi();
        return enabled;
    }

    /**
     * 获取wifi的打开或者关闭状态
     * @return true 为打开   false 为关闭
     */
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    //获取ip
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

    /**
     * 连接wifi
     * @param SSID
     * @param password
     * @return
     */
    public boolean connectWifi(String SSID, String password ){
        boolean b = false;
        if (null != password) {
            //需要密码
            b = mWifiManager.enableNetwork(mWifiManager.addNetwork(
                    setWifiParamsPassword(SSID, password)), true);
            ToastUtils.showMsg("正在连接网络");
        }else {
            //不需要密码
            b =  mWifiManager.enableNetwork(mWifiManager.addNetwork(setWifiParamsNoPassword(SSID)),
                    true);
        }
        return b;
    }


    /**
     * 连接有密码的wifi.
     *
     * @param SSID     ssid
     * @param password Password
     * @return apConfig
     */
    private WifiConfiguration setWifiParamsPassword(String SSID, String password) {
        WifiConfiguration apConfig = new WifiConfiguration();
        apConfig.SSID = "\"" + SSID + "\"";
        apConfig.preSharedKey = "\"" + password + "\"";
        //不广播其SSID的网络
        apConfig.hiddenSSID = true;
        apConfig.status = WifiConfiguration.Status.ENABLED;
        //公认的IEEE 802.11验证算法。
        apConfig.allowedAuthAlgorithms.clear();
        apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        //公认的的公共组密码
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        //公认的密钥管理方案
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        //密码为WPA。
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        //公认的安全协议。
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return apConfig;
    }

    /**
     * 连接没有密码wifi.
     *
     * @param ssid ssid
     * @return configuration
     */
    private WifiConfiguration setWifiParamsNoPassword(String ssid) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + ssid + "\"";
        configuration.status = WifiConfiguration.Status.ENABLED;
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return configuration;
    }
    public static final int WIFI_NO_PASS = 0;
    private static final int WIFI_WEP = 1;
    private static final int WIFI_PSK = 2;
    private static final int WIFI_EAP = 3;

    /**
     * 判断是否有密码.
     *
     * @param result ScanResult
     * @return 0
     */
    public static int getSecurity(ScanResult result) {
        if (null != result && null != result.capabilities) {
            if (result.capabilities.contains("WEP")) {
                return WIFI_WEP;
            } else if (result.capabilities.contains("PSK")) {
                return WIFI_PSK;
            } else if (result.capabilities.contains("EAP")) {
                return WIFI_EAP;
            }
        }
        return WIFI_NO_PASS;
    }








}
