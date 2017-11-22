package com.gzlk.android.isp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.gzlk.android.isp.BuildConfig;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.HttpHelper;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.NotificationHelper;
import com.gzlk.android.isp.helper.StringHelper;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/22 20:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/22 20:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DownloadingService extends Service {

    public static final String PARAM_URL = "ds_service_url";
    private static final String START = BuildConfig.APPLICATION_ID + ".service.START";
    private static final String STOP = BuildConfig.APPLICATION_ID + ".service.STOP";
    private static final String BACK = BuildConfig.APPLICATION_ID + ".service.BACKGROUND";
    public static final String RETRY = BuildConfig.APPLICATION_ID + ".service.RETRY";

    /**
     * 启动下载服务
     */
    public static void start(Context context, String url) {
        Intent intent = new Intent(context, DownloadingService.class);
        intent.putExtra(PARAM_URL, url);
        intent.setAction(START);
        context.startService(intent);
    }

    /**
     * 关闭下载服务
     */
    public static void stop(Context context) {
        Intent intent = new Intent(context, DownloadingService.class);
        intent.setAction(STOP);
        context.startService(intent);
    }

    /**
     * 转后台下载，并开启通知栏
     */
    public static void background(Context context) {
        Intent intent = new Intent(context, DownloadingService.class);
        intent.setAction(BACK);
        context.startService(intent);
    }

    /**
     * 重试下载
     */
    public static void retry(Context context) {
        Intent intent = new Intent(context, DownloadingService.class);
        intent.setAction(RETRY);
        context.startService(intent);
    }

    private boolean background = false;
    private String downloadingUrl = "";
    private NotificationHelper notificationHelper;

    private void log(String string) {
        LogHelper.log("DownService", string);
    }

    private boolean isEmpty(String text) {
        return StringHelper.isEmpty(text, true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("Downloading service created.");
    }

    @Override
    public void onDestroy() {
        onProgressListener = null;
        super.onDestroy();
        log("Downloading service destroy.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (null != intent) {
            String action = intent.getAction();
            if (!isEmpty(action)) {
                log(action);
                assert action != null;
                switch (action) {
                    case START:
                        downloadingUrl = intent.getStringExtra(PARAM_URL);
                        if (!isEmpty(downloadingUrl)) {
                            downloading(downloadingUrl);
                        }
                        break;
                    case STOP:
                        stopSelf();
                        onProgressListener = null;
                        break;
                    case BACK:
                        background = true;
                        break;
                    case RETRY:
                        downloadingUrl = intent.getStringExtra(PARAM_URL);
                        if (!isEmpty(downloadingUrl)) {
                            downloading(downloadingUrl);
                        }
                        break;
                }
            }
        } else {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private NotificationHelper helper() {
        if (null == notificationHelper) {
            notificationHelper = NotificationHelper.helper(this).progress();
        }
        return notificationHelper;
    }

    private void downloading(final String url) {
        if (isEmpty(url)) {
            throw new IllegalArgumentException("download url is empty.");
        }
        HttpHelper.helper().addCallback(new HttpHelper.HttpHelperCallback() {
            @Override
            public void onStart(int current, int total, String startedUrl) {
                super.onStart(current, total, startedUrl);
                if (null != onProgressListener) {
                    onProgressListener.onStart();
                }
            }

            @Override
            public void onProgressing(int current, int total, int currentHandled, int currentTotal, String processingUrl) {
                super.onProgressing(current, total, currentHandled, currentTotal, processingUrl);
                if (background) {
                    helper().show(currentHandled, currentTotal);
                } else {
                    if (null != onProgressListener) {
                        onProgressListener.onProgressing(currentHandled, currentTotal);
                    }
                }
            }

            @Override
            public void onSuccess(int current, int total, String successUrl) {
                super.onSuccess(current, total, successUrl);
                if (background) {
                    helper().showComplete(successUrl);
                } else {
                    if (null != onProgressListener) {
                        onProgressListener.onSuccess(successUrl);
                    }
                }
            }

            @Override
            public void onFailure(int current, int total, String failureUrl) {
                super.onFailure(current, total, failureUrl);
                if (background) {
                    helper().showRetry(DownloadingService.this, url);
                } else {
                    if (null != onProgressListener) {
                        onProgressListener.onFailure();
                    }
                }
            }

            @Override
            public void onStop(int current, int total) {
                super.onStop(current, total);
                if (null != onProgressListener) {
                    onProgressListener.onStop();
                }
                HttpHelper.helper().removeCallback(Integer.toHexString(hashCode()));
            }
        }, Integer.toHexString(hashCode())).setLocalDirectory(App.TEMP_DIR).clearTask().addTask(url).setIgnoreExist(false).download();
    }

    private static OnProgressListener onProgressListener;

    /**
     * 设置下载监听
     */
    public static void setOnProgressListener(OnProgressListener l) {
        onProgressListener = l;
    }

    public interface OnProgressListener {
        /**
         * 开始下载
         */
        void onStart();

        /**
         * 下载进度
         */
        void onProgressing(int current, int total);

        /**
         * 下载成功
         */
        void onSuccess(String url);

        /**
         * 下载失败
         */
        void onFailure();

        /**
         * 停止下载
         */
        void onStop();
    }
}
