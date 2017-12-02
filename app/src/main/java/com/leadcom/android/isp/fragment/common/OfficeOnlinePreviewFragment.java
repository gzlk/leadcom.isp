package com.leadcom.android.isp.fragment.common;

import android.content.Context;
import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.apache.poi.WordUtil;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;

import java.io.File;

/**
 * <b>功能描述：</b>Office文档在线预览页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/21 22:37 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/21 22:37 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OfficeOnlinePreviewFragment extends BaseWebViewFragment {

    private static final String PARAM_TITLE = "oopf_title";
    private static final String PARAM_EXT = "oopf_extension";
    private static final String PARAM_MINUTES = "oopf_is_minutes";
    private static final String PARAM_DOWNLOADED = "oopf_is_downloaded";

    public static OfficeOnlinePreviewFragment newInstance(String params) {
        OfficeOnlinePreviewFragment oopf = new OfficeOnlinePreviewFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // url地址
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 标题
        bundle.putString(PARAM_TITLE, strings[1]);
        // 后缀
        bundle.putString(PARAM_EXT, strings[2]);
        // 是否会议纪要文档
        bundle.putBoolean(PARAM_MINUTES, Boolean.valueOf(strings[3]));
        oopf.setArguments(bundle);
        return oopf;
    }

    public static void open(Context context, int req, String url, String title, String extension, boolean isMinute) {
        String params = format("%s,%s,%s,%s", url, title, extension, isMinute);
        BaseActivity.openActivity(context, OfficeOnlinePreviewFragment.class.getName(), params, req, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(PARAM_TITLE, "");
        mExtension = bundle.getString(PARAM_EXT, "");
        mMinutes = bundle.getBoolean(PARAM_MINUTES, false);
        mDownloaded = bundle.getBoolean(PARAM_DOWNLOADED, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TITLE, mTitle);
        bundle.putString(PARAM_EXT, mExtension);
        bundle.putBoolean(PARAM_MINUTES, mMinutes);
        bundle.putBoolean(PARAM_DOWNLOADED, mDownloaded);
    }

    private String mTitle, mExtension;
    private boolean mMinutes, mDownloaded = false;

    @Override
    public void doingInResume() {
        setCustomTitle(mTitle);
        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_home_inner_web_view_invalid_url);
        } else {
            if (!mDownloaded) {
                boolean needDown;
                String local = "";
                if (mQueryId.contains(FilePreviewHelper.NIM)) {
                    local = HttpHelper.helper().getLocalFilePath(mQueryId, App.ARCHIVE_DIR);
                    String localReal = local + "." + mExtension;
                    File file = new File(localReal);
                    needDown = !file.exists();
                } else {
                    // 非网易云文件，直接尝试下载
                    needDown = true;
                }
                if (needDown) {
                    showImageHandlingDialog(R.string.ui_base_text_loading);
                    // 先下载然后再预览
                    downloadFile(mQueryId, App.ARCHIVE_DIR);
                } else {
                    onFileDownloadingComplete(mQueryId, local, true);
                }
            }
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected String loadingUrl() {
        log("preview online office: " + mQueryId);
        return mQueryId;
    }

    private void resetRightEvent() {
        setRightText(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultData(mQueryId);
            }
        });
    }

    private void resetCollectEvent() {
        setRightText(R.string.ui_base_text_favorite);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (Utils.isUrl(mQueryId)) {
                    // 收藏在线文档
                    tryCollectOffice();
                }
            }
        });
    }

    private void tryCollectOffice() {
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).add(mQueryId);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void onFileDownloadingComplete(String url, String local, boolean success) {
        mDownloaded = true;
        hideImageHandlingDialog();
        if (success) {
            if (mMinutes) {
                // 会议纪要时，需要共享出去
                resetRightEvent();
            } else {
                resetCollectEvent();
            }
            String localReal = local + "." + mExtension;
            File target = new File(localReal);
            if (!target.exists()) {
                // 重命名
                File source = new File(local);
                source.renameTo(target);
            }

            if (Attachment.isWord(mExtension)) {
                // 下载完毕，打开word预览
                WordUtil word = new WordUtil(localReal);
                log(word.htmlPath);
                loadingUrl("file:///" + word.htmlPath);
            } else {
                // 下载完毕，使用本地第三方app打开office文档
                finish();
                FilePreviewHelper.previewMimeFile(Activity(), localReal, mExtension);
            }
        }
    }
}
