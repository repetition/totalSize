package com.totalsize.utils;

import android.widget.Toast;

import com.totalsize.MyApplication;

public class ToastUtils {

    public static void showMsg(final String msg){
        MyApplication.getHandler().post(new Runnable(){
            @Override
            public void run() {
                Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
