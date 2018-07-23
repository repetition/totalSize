package com.totalsize.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.util.Log;

import com.totalsize.MyApplication;
import com.totalsize.R;
import com.totalsize.utils.NetWorkManager;
import com.totalsize.utils.ToastUtils;

public class NetworkReceiver extends BroadcastReceiver {
    private NetWorkManager netWorkManager = NetWorkManager.getInstance(MyApplication.getContext());
    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            //密码错误广播,是不是正在获得IP地址
            int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
            if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                //密码错误
                ToastUtils.showMsg("密码错误");
            }
            SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(supplicantState);
            if (state == NetworkInfo.DetailedState.CONNECTING) {
                //正在连接
                ToastUtils.showMsg("正在连接");
            } else if (state == NetworkInfo.DetailedState.FAILED
                    || state == NetworkInfo.DetailedState.DISCONNECTING) {
                //连接失败
                ToastUtils.showMsg("网络连接失败");
            } else if (state == NetworkInfo.DetailedState.CONNECTED) {
                //连接成功
                ToastUtils.showMsg("网络连接成功");
                mOnNetWordListener.onNetConnected();
            } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                //正在获取ip地址
            }

        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            // 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLING://正在停止0
                    ToastUtils.showMsg("wifi正在关闭");
                    break;
                case WifiManager.WIFI_STATE_DISABLED://已停止1
                    mOnNetWordListener.onNetDisabled();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN://未知4
                    break;
                case WifiManager.WIFI_STATE_ENABLING://正在打开2
                    ToastUtils.showMsg("wifi正在打开");
                    mOnNetWordListener.onNetOpen();
                    break;
                case WifiManager.WIFI_STATE_ENABLED://已开启3
                    break;
                default:
                    break;
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            // 监听wifi的连接状态即是否连上了一个有效无线路由
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                // 获取联网状态的NetWorkInfo对象
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                //获取的State对象则代表着连接成功与否等状态
                NetworkInfo.State state = networkInfo.getState();
                //判断网络是否已经连接
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                if (isConnected) {
                }
            }
        }
    }

    /**
     * 发送网络状态eventBus.
     *
     * @param info info
     */
    private void sendNetworkStateChange(NetWorkInfo info) {
    }

    @SuppressWarnings("unused")
    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "移动网络";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }

    public static class NetWorkInfo {
        public int state;//1密码错误，2:连接成功，3:连接失败

        private NetWorkInfo setState(int state) {
            this.state = state;
            return this;
        }
    }

    private static onNetWordListener mOnNetWordListener = null;
    public static void setmOnNetWordListener(onNetWordListener onNetWordListener) {
        mOnNetWordListener = onNetWordListener;
    }
    public interface onNetWordListener{
        void onNetOpen();
        void onNetConnected();
        void onNetDisabled();
    }
}
