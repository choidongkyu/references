package com.skb.btv.upgrade.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;


public class SLog {

    private static final String SVC_TAG = "UpgradeSVC";
    private static final String LOG_ERR = "ERR";
    private static final String LOG_WARN = "WAR";
    private static final String LOG_INFO = "INF";
    private static final String LOG_DEBUG = "DBG";
    private static final String LOG_FILE = "/data/btv_home/run/upgradesvc.log";
    private static final String LOG_BACKUP_FILE = LOG_FILE + ".bak";
    private static final String LOG_DELIMITER = " ";

    private static Object writeLock = new Object();
    private static File logFile;
    private static File backupLogFile;

    private static void createLogFile() {
        logFile = new File(LOG_FILE);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                logFile.setReadable(true);
                logFile.setWritable(true);
            } catch (IOException e) {
                logFile = null;
                Log.e(SVC_TAG, "Can't create log file, msg[" + e.getMessage() + "]");
            }
        }
    }

    private static void createBackupLogFile() {
        backupLogFile = new File(LOG_BACKUP_FILE);
        if (backupLogFile.exists()) {
            backupLogFile.delete();
        }

        if ((logFile != null) && logFile.exists()) {
            logFile.renameTo(backupLogFile);
        }
    }

    private static void writeLog(String tag, String logLvl, String log) {
        String currentTime = getCurrentTime();

        if (log == null) {
            return;
        }

        if (logFile == null || !logFile.exists()) {
            createLogFile();
        }

        synchronized (writeLock) {
            if (isOverLogSize()) {
                backupLogFile();
            }

            FileWriter fw = null;
            BufferedWriter bw = null;

            try {
                fw = new FileWriter(logFile, true);
                bw = new BufferedWriter(fw);
                String fullLog = String.format("[%19s] %3s [%15s] - %s", currentTime, logLvl, tag, log);
                bw.write(fullLog);
                bw.newLine();
            } catch (Exception e) {
                //NOTHING
            } finally {
                try {
                    if (bw != null) {
                        bw.flush();
                        bw.close();
                    }

                    if (fw != null) {
                        fw.flush();
                        fw.close();
                    }
                } catch(Exception e) {
                    //NOTHING
                }
            }
        }
    }

    private static void backupLogFile() {
        createBackupLogFile();
        createLogFile();
    }

    private static boolean isOverLogSize() {
        if (logFile.length() > Units.File.MB) {
            return true;
        }

        return false;
    }

    private static String getCurrentTime() {
        Calendar timeCal = Calendar.getInstance();

        int year = timeCal.get(Calendar.YEAR);

        int month = timeCal.get(Calendar.MONTH)+1;

        int day = timeCal.get(Calendar.DATE);

        int hour = timeCal.get(Calendar.HOUR_OF_DAY);

        int min = timeCal.get(Calendar.MINUTE);

        int sec = timeCal.get(Calendar.SECOND);


        String systemTime = String.format("%4d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, min, sec);

        return systemTime;
    }

    public static final void e(String tag, String log) {
        if (log == null) {
            return;
        }

        writeLog(tag, LOG_ERR, log);
        Log.e(SVC_TAG, log);
    }

    public static final void w(String tag, String log) {
        if (log == null) {
            return;
        }
        writeLog(tag, LOG_WARN, log);
        Log.w(SVC_TAG, log);
    }

    public static final void i(String tag, String log) {
        if (log == null) {
            return;
        }

        writeLog(tag, LOG_INFO, log);
        Log.i(SVC_TAG, log);
    }

    public static final void d(String tag, String log) {
        if (log == null) {
            return;
        }
        writeLog(tag, LOG_DEBUG, log);
        Log.d(SVC_TAG, log);
    }

}
