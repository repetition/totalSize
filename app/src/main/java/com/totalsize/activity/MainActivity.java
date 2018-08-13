package com.totalsize.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.totalsize.floatWindow.SuspensionWindowManager;
import com.totalsize.ftp.Config;
import com.totalsize.service.FloatDeniService;
import com.totalsize.service.FloatService;
import com.totalsize.floatWindow.FloatingManager;
import com.totalsize.MyApplication;
import com.totalsize.R;
import com.totalsize.utils.Log4jConfig;
import com.totalsize.utils.NetWorkManager;
import com.totalsize.utils.Utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private static final String dirname = "/mnt/sdcard/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkSDSize();
        getWindow();
        AppManager.getAppManager().addActivity(this);
        //   checkSDSizeTimer();
        initHandler();
        SharedPreferences preferences = this.getSharedPreferences("conf", MODE_PRIVATE);
     /*   boolean isWrite = preferences.getBoolean("isWrite", false);
        if (!isWrite) {
            request();
        }*/
        request();
    }

    private void initHandler() {
        MyApplication.setOnHandlerListener(new MyApplication.HandlerListener() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });

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


    /**
     * 创建服务器配置文件
     */
    private void createDirsFiles() throws IOException {
        File dir = new File(dirname);
        if (!dir.exists()) {
            dir.mkdir();
        }
        FileOutputStream fos = null;
        //    String tmp = getString(R.string.users);

        String tmp = Config.UserConfig;
        File sourceFile = new File(dirname + "totalSize/users.properties");
        if (!sourceFile.exists()) {
            sourceFile.getParentFile().mkdirs();
            sourceFile.createNewFile();
        }
        fos = new FileOutputStream(sourceFile);
        fos.write(tmp.getBytes());
        fos.flush();
        if (fos != null) {
            fos.close();
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


    public void request() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            try {
                createDirsFiles();
                initLog4j();
                Toast.makeText(MainActivity.this, "写入配置！", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = this.getSharedPreferences("conf", MODE_PRIVATE).edit();
                editor.putBoolean("isWrite", true);
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        requestPermission(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, MY_PERMISSION_REQUEST_CODE);
    }

    private void initLog4j() {
        Log4jConfig log4jConfig = new Log4jConfig();
        log4jConfig.configLog("totalSize");
    }


    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    // 只要有一个权限没有被授予, 则直接返回 false
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * 检查是否拥有指定的所有权限
     */
    private void requestPermission(String[] permissions, int code) {

        if (Build.VERSION.SDK_INT >= 23) {
            // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
            this.requestPermissions(
                    permissions,
                    code
            );
        }
    }


    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                try {
                    createDirsFiles();
                    initLog4j();
                    Logger log = Logger.getLogger(MyApplication.class);
                    log.info("My Application Created");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                Toast.makeText(MainActivity.this, "请打开权限", Toast.LENGTH_SHORT).show();
                request();
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
