package com.leadcom.android.isp.helper;

import android.util.Log;

import com.leadcom.android.isp.etc.Utils;

/**
 * <b>功能：</b>打印log记录<br />
 * <b>作者：</b>Hsiang Leekwok <br />
 * <b>时间：</b>2016/01/14 12:47 <br />
 * <b>邮箱：</b>xiang.l.g@gmail.com <br />
 */
public class LogHelper {

    /**
     * 每次最大显示的log长度
     */
    private static final int MAX_LOG_LIMIT = 3000;
    private static StringBuilder mLogCache;

    private static void logcat(String tag, String string) {
        if (string.length() > MAX_LOG_LIMIT) {
            print(tag, StringHelper.format("%s", string.substring(0, MAX_LOG_LIMIT)));
            logcat(tag, string.substring(MAX_LOG_LIMIT));
        } else {
            print(tag, StringHelper.format("%s", string));
        }
    }

    private static void print(String tag, String string) {
        Log.e(tag, string);
    }

    public static void log(String tag, String string) {
        log(tag, string, false);
    }

    public static void log(String tag, String string, Throwable e) {
        log(tag, string);
    }

    public static void log(String tag, String string, boolean replaceLineTag) {
        logcat(tag, string);
        if (null == mLogCache) {
            mLogCache = new StringBuilder();
        }
        mLogCache.append(StringHelper.format("[%s] %s %s", Utils.formatDateOfNow("yyyy-MM-dd HH:mm:ss"), tag, string)).append(string);
    }

    public static CharSequence getCache() {
        return mLogCache.toString();
    }

    public static void clearCache() {
        mLogCache.setLength(0);
    }
}
