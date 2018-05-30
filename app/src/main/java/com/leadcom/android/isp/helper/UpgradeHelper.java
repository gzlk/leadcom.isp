package com.leadcom.android.isp.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.listener.OnTaskFailureListener;

import java.io.File;

/**
 * <b>功能描述：</b>App更新<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/02/05 20:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class UpgradeHelper {

    public static UpgradeHelper helper(Context context, String lstVersion) {
        return new UpgradeHelper(context, lstVersion);
    }

    private String lstVersion;
    private String localPath;
    private Context context;

    private UpgradeHelper(Context context, String lstVersion) {
        this.lstVersion = lstVersion;
        this.context = context;
    }

    public void startDownload(String url, String title, String description) {
        localPath = HttpHelper.helper().getLocalFilePath(url, App.TEMP_DIR);
        DownloadingHelper.helper().init(context).setShowNotification(true).setOnTaskCompleteListener(new OnTaskCompleteListener() {
            @Override
            public void onComplete() {
                // 下载完毕
                prepareInstall(localPath);
            }
        }).setOnTaskFailureListener(new OnTaskFailureListener() {
            @Override
            public void onFailure() {
                // 下载失败
            }
        }).download(url, localPath, "apk", title, description);
    }

    private void prepareInstall(String path) {
        // 检测本地是否有相同的安装文件，有则不需要再次下载
        File file = new File(path);
        if (file.exists()) {
            PackageManager manager = App.app().getPackageManager();
            PackageInfo info = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            if (null != info) {
                String versionName = info.versionName;
                if (versionName.equals(lstVersion)) {
                    // 直接安装
                    FilePreviewHelper.previewFile(context, path, "newVersion", "apk");
                }
            }
        }
    }
}
