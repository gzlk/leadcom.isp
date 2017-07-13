package com.gzlk.android.isp.fragment.common;

import android.os.Bundle;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.HttpHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.nim.file.FilePreviewHelper;

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
        oopf.setArguments(bundle);
        return oopf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(PARAM_TITLE, "");
        mExtension = bundle.getString(PARAM_EXT, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TITLE, mTitle);
        bundle.putString(PARAM_EXT, mExtension);
    }

    private String mTitle, mExtension;

    @Override
    public void doingInResume() {
        setCustomTitle(mTitle);
        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_home_inner_web_view_invalid_url);
        } else {
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

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected String loadingUrl() {
        log("preview online office: " + mQueryId);
        return mQueryId;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void onFileDownloadingComplete(String url, String local, boolean success) {
        hideImageHandlingDialog();
        if (success) {
            finish();
            String localReal = local + "." + mExtension;
            File target = new File(localReal);
            if (!target.exists()) {
                // 重命名
                File source = new File(local);
                source.renameTo(target);
            }
            // 下载完毕，使用本地第三方app打开office文档
            FilePreviewHelper.previewMimeFile(Activity(), localReal, mExtension);
        }
    }
}
