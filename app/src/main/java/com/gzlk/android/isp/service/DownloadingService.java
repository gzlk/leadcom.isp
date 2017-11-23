package com.gzlk.android.isp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.gzlk.android.isp.BuildConfig;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.HttpHelper;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.NotificationHelper;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.nim.file.FilePreviewHelper;

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
    public static Intent getRetryIntent(Context context, String url) {
        Intent intent = new Intent(context, DownloadingService.class);
        intent.setAction(RETRY);
        intent.putExtra(PARAM_URL, url);
        return intent;
    }

    private boolean background = false;
    private boolean isDownloading = false;
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
                String url = "";
                switch (action) {
                    case START:
                        url = intent.getStringExtra(PARAM_URL);
                        break;
                    case STOP:
                        stopSelf();
                        onProgressListener = null;
                        break;
                    case BACK:
                        background = true;
                        break;
                    case RETRY:
                        // 任务栏点击之后开始的是后台下载
                        background = true;
                        url = intent.getStringExtra(PARAM_URL);
                        break;
                }
                if (!isEmpty(url)) {
                    PreferenceHelper.save(getString(R.string.pf_static_downloading_url, BuildConfig.BUILD_TYPE), url);
                    downloading(url);
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

    private String callbackTag() {
        return Integer.toHexString(hashCode());
    }

    private void downloading(final String url) {
        if (isEmpty(url)) {
            throw new IllegalArgumentException("download url is empty.");
        }
        if (!isDownloading) {
            isDownloading = true;
        } else {
            log("downloading was started.");
            return;
        }
        log("downloading: " + url);
        // 非主线程运行的callback
        HttpHelper.helper().addCallback(new HttpHelper.HttpHelperCallback() {
            @Override
            public void onStart(int current, int total, String startedUrl) {
                super.onStart(current, total, startedUrl);
                log(StringHelper.format("downloading start(background: %s)...", background));
                //callStart();
                if (null != onProgressListener) {
                    onProgressListener.onStart();
                }
            }

            int lastProgress = 0;

            @Override
            public void onProgressing(int current, int total, int currentHandled, int currentTotal, String processingUrl) {
                super.onProgressing(current, total, currentHandled, currentTotal, processingUrl);
                //log(StringHelper.format("downloading(background: %s) %d of %d, percentage: %f", background, currentHandled, currentTotal, (currentHandled * 1.0 / currentTotal * 100)));
                if (background) {
                    int progress = (int) (currentHandled * 1.0 / currentTotal * 100);
                    if (lastProgress != progress) {
                        lastProgress = progress;
                        helper().show(currentHandled, currentTotal);
                    }
                } else {
                    //callProgress(currentHandled, currentTotal);
                    if (null != onProgressListener) {
                        onProgressListener.onProgressing(currentHandled, currentTotal);
                    }
                }
            }

            @Override
            public void onSuccess(int current, int total, String successUrl) {
                super.onSuccess(current, total, successUrl);
                log(StringHelper.format("downloading success(background: %s)...", background));
                if (background) {
                    helper().showComplete(successUrl);
                    FilePreviewHelper.previewFile(DownloadingService.this, successUrl, "new_version", "apk");
                } else {
//                    callSuccess(successUrl);
                    if (null != onProgressListener) {
                        onProgressListener.onSuccess(successUrl);
                    }
                }
                isDownloading = false;
            }

            @Override
            public void onFailure(int current, int total, String failureUrl) {
                super.onFailure(current, total, failureUrl);
                log(StringHelper.format("downloading failure(background: %s)...", background));
                if (background) {
                    helper().showRetry(DownloadingService.this, url);
                } else {
//                    callFailure();
                    if (null != onProgressListener) {
                        onProgressListener.onFailure();
                    }
                }
                isDownloading = false;
            }

            @Override
            public void onStop(int current, int total) {
                super.onStop(current, total);
                isDownloading = false;
                log(StringHelper.format("downloading stop(background: %s)...", background));
                if (!background) {
//                    callStop();
                    if (null != onProgressListener) {
                        onProgressListener.onStop();
                    }
                }
                HttpHelper.helper().removeCallback(callbackTag());
                background = false;
                // 下载结束之后停止服务
                stopSelf();
            }
        }, callbackTag()).setLocalDirectory(App.TEMP_DIR).clearTask().addTask(url).setIgnoreExist(false).download();
    }

    private void callStart() {
        Message msg = eventHandler.obtainMessage(evtStart);
        eventHandler.sendMessage(msg);
    }

    private void callSuccess(String url) {
        Message msg = eventHandler.obtainMessage(evtSuccess);
        msg.obj = url;
        eventHandler.sendMessage(msg);
    }

    private void callFailure() {
        Message msg = eventHandler.obtainMessage(evtFailure);
        eventHandler.sendMessage(msg);
    }

    private void callProgress(int current, int total) {
        Message msg = eventHandler.obtainMessage(evtProgress);
        msg.obj = new Object[]{current, total};
        eventHandler.sendMessage(msg);
    }

    private void callStop() {
        Message msg = eventHandler.obtainMessage(evtStop);
        eventHandler.sendMessage(msg);
    }

    private EventHandler eventHandler = new EventHandler();
    private static final int evtStart = 1;
    private static final int evtSuccess = 2;
    private static final int evtFailure = 3;
    private static final int evtProgress = 4;
    private static final int evtStop = 5;

    private static class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (null == onProgressListener) {
                return;
            }
            switch (msg.what) {
                case evtStart:
                    onProgressListener.onStart();
                    break;
                case evtSuccess:
                    onProgressListener.onSuccess((String) msg.obj);
                    break;
                case evtFailure:
                    onProgressListener.onFailure();
                    break;
                case evtProgress:
                    Object[] objects = (Object[]) msg.obj;
                    onProgressListener.onProgressing((int) objects[0], (int) objects[1]);
                    break;
                case evtStop:
                    onProgressListener.onStop();
                    break;
            }
        }
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
