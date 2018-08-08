package com.totalsize.utils;

import java.io.OutputStream;

public class ADBUtil {

    public static void adbStart()
    {
        try
        {
            ProcessBuilder processBuilder = newProcessBuilder();
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.command("su").start();
            OutputStream os = process.getOutputStream();
            os.write("setprop service.adb.tcp.port 5555\n".getBytes());
            os.write("stop adbd\n".getBytes());
            os.write("start adbd\n".getBytes());
            os.flush();
   /*         Runtime.getRuntime().exec("su setprop service.adb.tcp.port 5555");
            Runtime.getRuntime().exec("su stop adbd");
            Runtime.getRuntime().exec("su start adbd");*/
            ToastUtils.showMsg("开启调试");
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public static void adbStop()
    {
        try
        {
            ProcessBuilder processBuilder = newProcessBuilder();
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.command("su").start();
            OutputStream os = process.getOutputStream();
            os.write("stop adbd\n".getBytes());
            os.flush();
          //  Runtime.getRuntime().exec("su stop adbd");
            ToastUtils.showMsg("关闭调试");
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public static ProcessBuilder newProcessBuilder() {
        return new ProcessBuilder();
    }
}
