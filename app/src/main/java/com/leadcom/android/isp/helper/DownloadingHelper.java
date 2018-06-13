package com.leadcom.android.isp.helper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.listener.OnTaskFailureListener;

import java.io.File;

/**
 * <b>功能描述：</b>系统下载类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/08 08:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/04/08 08:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DownloadingHelper {

    public static DownloadingHelper helper() {
        return new DownloadingHelper();
    }

    public DownloadingHelper init(Context context) {
        this.context = context;
        receiver = new DownloadingReceiver();
        return this;
    }

    private Context context;
    private boolean showNotification = true;
    private boolean removeNotificationWhenComplete = false;
    private DownloadManager downloadManager;
    private DownloadingReceiver receiver;
    private OnTaskCompleteListener completeListener;
    private OnTaskFailureListener failureListener;

    /**
     * 设置下载完毕回调
     */
    public DownloadingHelper setOnTaskCompleteListener(OnTaskCompleteListener l) {
        completeListener = l;
        return this;
    }

    /**
     * 设置下载失败的回调
     */
    public DownloadingHelper setOnTaskFailureListener(OnTaskFailureListener l) {
        failureListener = l;
        return this;
    }

    /**
     * 设置是否在下载成功之后删除通知栏
     */
    public DownloadingHelper setRemoveNotificationWhenComplete(boolean remove) {
        removeNotificationWhenComplete = remove;
        return this;
    }

    /**
     * 设置是否显示通知栏进度
     */
    public DownloadingHelper setShowNotification(boolean shown) {
        this.showNotification = shown;
        return this;
    }

    private String taskIdName() {
        return StringHelper.getString(R.string.pf_downloading_task_id, BuildConfig.BUILD_TYPE);
    }

    private long getDownloadTask() {
        String val = PreferenceHelper.get(taskIdName(), "0");
        return Long.valueOf(val);
    }

    private void saveDownloadTask(long taskId) {
        PreferenceHelper.save(taskIdName(), String.valueOf(taskId));
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private boolean isTaskRunning(long taskId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(taskId);
        Cursor cursor = downloadManager.query(query);
        boolean running = false;
        try {
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                running = DownloadManager.STATUS_PENDING == status || DownloadManager.STATUS_RUNNING == status || DownloadManager.STATUS_PAUSED == status;
            }
        } finally {
            cursor.close();
        }
        return running;
    }

    /**
     * 检测本地文件是否存在，有则不再需要重新下载
     */
    private boolean isNeedDownload(String localPath) {
        File file = new File(localPath);
        return !file.exists();
    }

    /**
     * 下载指定的url文件
     *
     * @param url         远程文件的url地址
     * @param local       本地存储地址
     * @param extension   文件扩展名，如果已知的话
     * @param title       下载进度标题提醒
     * @param description 下载进度详细描述
     */
    public void download(String url, String local, String extension, String title, String description) {
        if (!isNeedDownload(local)) {
            if (null != completeListener) {
                completeListener.onComplete();
            }
        } else {
            if (null == downloadManager) {
                downloadManager = (DownloadManager) this.context.getSystemService(Context.DOWNLOAD_SERVICE);
            }
            long taskId = getDownloadTask();
            if (taskId > 0 && isTaskRunning(taskId)) {
                ToastHelper.make().showMsg(R.string.ui_system_updating_exist);
                return;
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(title);
            request.setDescription(description);
            // 默认文本方式下载内容
            String mimeType = "text/plain";
            if (!StringHelper.isEmpty(extension, true)) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            request.setMimeType(mimeType);
            // 在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，直到用户点击该Notification或者消除该Notification
            int visibility = DownloadManager.Request.VISIBILITY_HIDDEN;
            if (showNotification) {
                if (removeNotificationWhenComplete) {
                    visibility = DownloadManager.Request.VISIBILITY_VISIBLE;
                } else {
                    visibility = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
                }
            }
            request.setNotificationVisibility(visibility);
            // 可能无法创建Download文件夹，如无sdcard情况，系统会默认将路径设置为/data/data/com.android.providers.downloads/cache/xxx.apk
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                request.setDestinationUri(Uri.parse("file://" + local));
            }
            taskId = downloadManager.enqueue(request);
            saveDownloadTask(taskId);

            // 注册下载进度监听广播
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            context.registerReceiver(receiver, filter);
        }
    }

    // 检测下载情况并决定是开始安装还是下载失败
    private void checkDownloadStatus(long taskId) {
        long oldId = getDownloadTask();
        if (taskId != oldId) {
            return;
        }
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(taskId);
        Cursor cursor = downloadManager.query(query);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        cursor.close();
        if (DownloadManager.STATUS_SUCCESSFUL == status) {
            if (null != completeListener) {
                completeListener.onComplete();
            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_system_updating_failure);
            if (null != failureListener) {
                failureListener.onFailure();
            }
        }

        // 任务下载完成，清除保存的taskId
        saveDownloadTask(0L);
    }

    private void log(String log) {
        LogHelper.log("DownloadHelper", log);
    }

    /**
     * 下载情况消息接收器
     */
    private class DownloadingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = null != intent ? intent.getAction() : null;
            if (!StringHelper.isEmpty(action, true)) {
                assert action != null;
                log(action);
                switch (action) {
                    case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                        // 下载完毕
                        long taskId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        if (taskId > 0) {
                            checkDownloadStatus(taskId);
                        }
                        break;
                    case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                        // 点击了通知栏
                        long[] taskIds = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                        if (null == taskIds || taskIds.length < 1) {
                            openDownloadPage();
                        } else {
                            long existId = getDownloadTask();
                            for (long id : taskIds) {
                                if (id == existId) {
                                    openDownloadPage();
                                    break;
                                }
                            }
                        }
                        break;
                }
            }
        }

        private void openDownloadPage() {
            Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(pageView);
        }
    }
}
