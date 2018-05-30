package com.leadcom.android.isp.fragment.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.apache.poi.ExcelUtils;
import com.leadcom.android.isp.apache.poi.FileUtils;
import com.leadcom.android.isp.apache.poi.WordUtils;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.FilePreviewHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.user.Collection;

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

    /**
     * 是否是显示已经收藏了的内容
     */
    public static boolean isCollected = false;
    private static final String PARAM_TITLE = "oopf_title";
    private static final String PARAM_EXT = "oopf_extension";
    private static final String PARAM_MINUTES = "oopf_is_minutes";
    private static final String PARAM_DOWNLOADED = "oopf_is_downloaded";
    private static String localReal = "";

    public static OfficeOnlinePreviewFragment newInstance(Bundle bundle) {
        OfficeOnlinePreviewFragment oopf = new OfficeOnlinePreviewFragment();
        oopf.setArguments(bundle);
        return oopf;
    }

    public static void open(Context context, int req, String url, String title, String extension, boolean isMinute) {
        localReal = "";
        Bundle bundle = new Bundle();
        // url地址
        bundle.putString(PARAM_QUERY_ID, url);
        // 标题
        bundle.putString(PARAM_TITLE, title);
        // 后缀
        bundle.putString(PARAM_EXT, extension);
        // 是否会议纪要文档
        bundle.putBoolean(PARAM_MINUTES, isMinute);
        BaseActivity.openActivity(context, OfficeOnlinePreviewFragment.class.getName(), bundle, req, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(PARAM_TITLE, "");
        if (StringHelper.isEmpty(mTitle, true)) {
            mTitle = "";
        }
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

    @Override
    public void onDestroy() {
        localReal = "";
        isCollected = false;
        super.onDestroy();
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
                if (FilePreviewHelper.isNimFile(mQueryId)) {
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
        if (isCollected) return;
        setRightIcon(R.string.ui_icon_more);
        //setRightText(R.string.ui_base_text_favorite);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (Utils.isUrl(mQueryId)) {
                    // 显示更多对话框
                    showDialog();
                    // 收藏在线文档
                    //tryCollectOffice();
                }
            }
        });
    }

    private View dialogView;
    private CorneredButton saveButton;

    private void showDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = View.inflate(Activity(), R.layout.popup_dialog_moment_details, null);
                    dialogView.findViewById(R.id.ui_dialog_moment_details_button_privacy).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.ui_dialog_moment_details_button_share).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.ui_dialog_moment_details_button_delete).setVisibility(View.GONE);
                    saveButton = dialogView.findViewById(R.id.ui_dialog_moment_details_button_save);
                }
                return dialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                saveButton.setText(R.string.ui_base_text_save_to);
            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_moment_details_button_favorite, R.id.ui_dialog_moment_details_button_save};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_dialog_moment_details_button_favorite:
                        tryCollectOffice();
                        break;
                    case R.id.ui_dialog_moment_details_button_save:
                        tryCopy();
                        break;
                }
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true).show();
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

    private void tryCopy() {
        if (!isEmpty(localReal)) {
            String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            String targetPath = downloadPath + "/" + mTitle;
            if (!isEmpty(mExtension) && !targetPath.contains(mExtension)) {
                targetPath += "." + mExtension;
            }
            try {
                FileUtils.fileCopy(localReal, targetPath);
                ToastHelper.make().showMsg("文件已保存到：" + downloadPath);
            } catch (Exception e) {
                e.printStackTrace();
                ToastHelper.make().showMsg("文件保存失败：" + e.getMessage());
            }
        }
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
            localReal = local + "." + mExtension;
            File target = new File(localReal);
            if (!target.exists()) {
                // 重命名
                File source = new File(local);
                source.renameTo(target);
            }

            if (Attachment.isWord(mExtension)) {
                // 下载完毕，打开 word 预览
                WordUtils word = new WordUtils(localReal);
                log(word.htmlPath);
                loadingUrl("file:///" + word.htmlPath);
            } else if (Attachment.isExcel(mExtension)) {
                // 下载完毕，打开 excel 预览
                try {
                    ExcelUtils excel = new ExcelUtils(localReal);
                    log(excel.htmlPath);
                    loadingUrl("file:///" + excel.htmlPath);
                } catch (Exception ignore) {
                    previewFile();
                }
//            } else if (Attachment.isPowerPoint(mExtension)) {
//                PptUtil ppt = new PptUtil(localReal);
//                log(ppt.htmlPath);
//                loadingUrl("file:///" + ppt.htmlPath);
            } else {
                // 下载完毕，使用本地第三方app打开office文档
                previewFile();
            }
        }
    }

    private void previewFile() {
        finish();
        FilePreviewHelper.previewMimeFile(Activity(), localReal, mExtension);
    }
}
