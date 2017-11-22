package com.gzlk.android.isp.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.MainActivity;
import com.gzlk.android.isp.nim.file.FilePreviewHelper;
import com.gzlk.android.isp.service.DownloadingService;

/**
 * <b>功能描述：</b>系统通知<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 17:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 17:59 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NotificationHelper {

    private static final int ID = 0x00FF00FF;
    private static final int ID1 = 0x00FF00FE;

    public static NotificationHelper helper(Context context) {
        return new NotificationHelper(context);
    }

    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private Context context;

    private NotificationHelper(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);
    }

    public void show(String title, String text, Intent extras) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ID,
                MainActivity.getIntent(context, extras),
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher);
        manager.notify(ID, builder.build());
    }

    public NotificationHelper progress() {
        builder.setContentTitle(StringHelper.getString(R.string.ui_system_updating_background_title_downloading, StringHelper.getString(R.string.app_name_default)))
                .setContentText(StringHelper.getString(R.string.ui_system_updating))
                .setContentIntent(null)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        return this;
    }

    public void show(int progress, int total) {
        builder.setProgress(total, progress, false);
        manager.notify(ID1, builder.build());
    }

    private Intent getViewIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(FilePreviewHelper.getUriFromFile(path), "application/vnd.android.package-archive");
        return intent;
    }

    public void showComplete(String localPath) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, getViewIntent(localPath), 0);
        builder.setContentText(StringHelper.getString(R.string.ui_system_updating_background_content_complete))
                .setContentTitle(StringHelper.getString(R.string.ui_system_updating_background_title_complete))
                .setOngoing(false)
                .setProgress(0, 0, false).setContentIntent(pendingIntent);
        manager.notify(ID1, builder.build());
    }

    public void showRetry(Context context) {
        Intent intent = new Intent(DownloadingService.RETRY);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentText(StringHelper.getString(R.string.ui_system_updating_background_content_failure))
                .setContentTitle(StringHelper.getString(R.string.ui_system_updating_background_title_failure))
                .setContentIntent(pendingIntent)
                .setProgress(0, 0, false).setOngoing(false);
        manager.notify(ID1, builder.build());
    }
}
