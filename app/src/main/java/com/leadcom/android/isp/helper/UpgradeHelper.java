package com.leadcom.android.isp.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.common.UpdateRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.listener.OnTaskFailureListener;
import com.leadcom.android.isp.model.common.SystemUpdate;

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

    public static UpgradeHelper helper(Context context) {
        return new UpgradeHelper(context);
    }

    private UpgradeHelper(Context context) {
        this.context = context;
    }

    private String getString(int res) {
        return StringHelper.getString(res);
    }

    private String getString(int res, Object... objects) {
        return StringHelper.getString(res, objects);
    }

    private String lstVersion;
    private String localPath;
    private Context context;

    /**
     * 检测服务器上的最新客户端版本并提示用户更新
     */
    public void checkVersion() {
        UpdateRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<SystemUpdate>() {
            @Override
            public void onResponse(SystemUpdate systemUpdate, boolean success, String message) {
                super.onResponse(systemUpdate, success, message);
                if (success) {
                    String ver = systemUpdate.getVersion();
                    //warningUpdatable("http://file.ws.126.net/3g/client/netease_newsreader_android.apk","2.0.1");
                    if (!StringHelper.isEmpty(ver) && ver.compareTo(BuildConfig.VERSION_NAME) > 0) {
                        String url = systemUpdate.getResourceURI();
                        if (StringHelper.isEmpty(url) || !Utils.isUrl(url)) {
                            SimpleDialogHelper.init((AppCompatActivity) context).show(R.string.ui_system_updatable_url_invalid);
                        } else {
                            warningUpdatable(url, ver, systemUpdate.getForceUpdate());
                        }
                    }
                }
            }
        }).getClientVersion();
    }

    private void warningUpdatable(final String url, String version, String forceVersion) {
        lstVersion = version;
        String thisVersion = App.app().version();
        boolean isForce = thisVersion.compareTo(forceVersion) < 0;
        String text = getString(R.string.ui_system_updatable, getString(R.string.app_name_default), lstVersion, (isForce ? getString(R.string.ui_system_updatable_force) : ""));
        String confirm = getString(isForce ? R.string.ui_base_text_upgrade_now : R.string.ui_base_text_upgrade);
        String cancel = (isForce ? "" : getString(R.string.ui_base_text_no_need));
        SimpleDialogHelper.init((AppCompatActivity) context).show(text, confirm, cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 打开下载对话框，并开始下载（下载对话框可以隐藏）
                //showUpgradeDownloadingDialog();
                String app = getString(R.string.app_name_default);
                String title = getString(R.string.ui_system_updating_title, app);
                String description = getString(R.string.ui_system_updating_description);
                startDownload(url, title, description);
                return true;
            }
        }, null);
    }

    private void startDownload(String url, String title, String description) {
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
