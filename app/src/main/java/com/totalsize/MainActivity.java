package com.totalsize;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static Handler mHandler = new Handler();
    TextView mStorageDirectory;
    TextView mDataDirectory;
    public Context mContext = MainActivity.this;
    private WindowManager.LayoutParams mParams;
    private Button mBTWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageDirectory = findViewById(R.id.StorageDirectory);
        mDataDirectory = findViewById(R.id.DataDirectory);
        mBTWindow = findViewById(R.id.bt_window);
        mBTWindow.setOnClickListener(this);
        checkSDSize();
        checkSDSizeTimer();
        addWindow();
    }

    @Override
    public void onClick(View v) {
        addWindow();
    }


    private void addWindow() {

        final WIFIManager wifiManager = WIFIManager.getInstance(mContext);

        FloatingManager manager = FloatingManager.getInstance(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.window_suspend, null);
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        manager.addView(view, mParams);

        final TextView mBTAddress = view.findViewById(R.id.tv_address);
        Button mBTToggle = view.findViewById(R.id.bt_toggle);
        mBTToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击了", Toast.LENGTH_SHORT).show();
                boolean b = wifiManager.wifiToggle();
                if (!b) {
                    return;
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mBTAddress.setText("" + wifiManager.getIpAddress());
                            }
                        });
                    }
                }, 2000L);
            }
        });


    }

    private void checkSDSizeTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("main", "定时器执行");
                final String sdTotalSize = getSDTotalSize();
                final String sdAvailableSize = getSDAvailableSize();
                final String romTotalSize = getRomTotalSize();
                final String romAvailableSize = getRomAvailableSize();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "sdTotalSize:" + sdTotalSize + ", sdAvailableSize:" + sdAvailableSize, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, 0, 5000L);
    }


    private void checkSDSize() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final String sdTotalSize = getSDTotalSize();
                final String sdAvailableSize = getSDAvailableSize();
                final String romTotalSize = getRomTotalSize();
                final String romAvailableSize = getRomAvailableSize();

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
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(MainActivity.this, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    private String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(MainActivity.this, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
    }
}
