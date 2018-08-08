package com.totalsize.utils;

import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Log4jConfig {

    public void configLog(String logFileNamePrefix) {
        LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(getFileName(logFileNamePrefix));
        logConfigurator.setRootLevel(org.apache.log4j.Level.INFO);
        logConfigurator.setLevel("org.apache", org.apache.log4j.Level.INFO);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 2);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.configure();
    }
    @NonNull
    public String getFileName(String logFileNamePrefix) {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + "logdemo" + File.separator + "logs"
                + File.separator + (logFileNamePrefix == null ? "" : logFileNamePrefix) + "log4j.txt";

        File file = new File(path);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // file.createNewFile();
        }
        return path;
    }
}
