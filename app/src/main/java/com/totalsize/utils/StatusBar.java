package com.totalsize.utils;

public class StatusBar {
    public static void hidden()
    {
        try
        {
            Runtime.getRuntime().exec("su -c service call activity " + "42" + " s16 com.android.systemui").waitFor();
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public static void show()
    {
        try
        {
            Runtime.getRuntime().exec("su -c am startservice -n com.android.systemui/.SystemUIService").waitFor();
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }
}