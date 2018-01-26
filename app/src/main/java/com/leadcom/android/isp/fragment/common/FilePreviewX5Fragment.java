package com.leadcom.android.isp.fragment.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;
import com.leadcom.android.isp.view.SuperFileView2;

import java.io.File;

/**
 * <b>功能描述：</b>采用疼熏X5内核的文件浏览服务<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/24 13:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class FilePreviewX5Fragment extends BaseDownloadingUploadingSupportFragment {

    private static final String PARAM_TITLE = "fpx5_title";
    private static final String PARAM_EXT = "fpx5_ext";
    private static final String PARAM_MINUTE = "fpx5_minute";
    private static final String PARAM_DOWNLOADED = "fpx5_downloaded";
    private static String localReal = "";

    public static FilePreviewX5Fragment newInstance(String params) {
        FilePreviewX5Fragment fpx5 = new FilePreviewX5Fragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // url 地址，或者本地地址
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 标题栏文字，或者文件名
        bundle.putString(PARAM_TITLE, strings[1]);
        // 文档的后缀名
        bundle.putString(PARAM_EXT, strings[2]);
        // 如果时word文档的话，标记是否为会议记录
        bundle.putBoolean(PARAM_MINUTE, Boolean.valueOf(strings[3]));
        fpx5.setArguments(bundle);
        return fpx5;
    }

    public static void open(Context ctx, int req, String url, String title, String ext, boolean isMinute) {
        localReal = "";
        String params = format("%s,%s,%s,%s", url, title, ext, isMinute);
        BaseActivity.openActivity(ctx, FilePreviewX5Fragment.class.getName(), params, req, true, false);
    }

    @ViewId(R.id.ui_viewer_x5_root)
    private SuperFileView2 mSuperFileView;

    private String mTitle, mExt;
    private boolean mMinute, mDownloaded;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        // 标题
        mTitle = bundle.getString(PARAM_TITLE, "");
        // 文档后缀名
        mExt = bundle.getString(PARAM_EXT, "");
        // 是否会议记录，如果是，需要分享出去
        mMinute = bundle.getBoolean(PARAM_MINUTE, false);
        // 是否已下载完毕
        mDownloaded = bundle.getBoolean(PARAM_DOWNLOADED, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TITLE, mTitle);
        bundle.putString(PARAM_EXT, mExt);
        bundle.putBoolean(PARAM_MINUTE, mMinute);
        bundle.putBoolean(PARAM_DOWNLOADED, mDownloaded);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_viewer_x5_common;
    }

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
                    String localReal = local + "." + mExt;
                    File file = new File(localReal);
                    needDown = !file.exists();
                } else needDown = mQueryId.charAt(0) != '/';

                if (needDown) {
                    showImageHandlingDialog(R.string.ui_base_text_loading);
                    // 先下载然后再预览
                    downloadFile(mQueryId, App.ARCHIVE_DIR);
                } else {
                    onFileDownloadingComplete(mQueryId, mQueryId, true);
                }
            }
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {
        if (null != mSuperFileView) {
            mSuperFileView.onStopDisplay();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void onFileDownloadingComplete(String url, String local, boolean success) {
        mDownloaded = true;
        hideImageHandlingDialog();
        if (success) {
            if (mMinute) {
                // 会议纪要时，需要共享出去
                resetRightEvent();
            } else {
                resetCollectEvent();
            }
            localReal = local + "." + mExt;
            File target = new File(localReal);
            if (!target.exists()) {
                // 重命名
                File source = new File(local);
                source.renameTo(target);
            }

            mSuperFileView.displayFile(new File(localReal));
        }
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
            if (!isEmpty(mExt) && !targetPath.contains(mExt)) {
                targetPath += "." + mExt;
            }
            try {
                //FileUtils.fileCopy(localReal, targetPath);
                ToastHelper.make().showMsg("文件已保存到：" + downloadPath);
            } catch (Exception e) {
                e.printStackTrace();
                ToastHelper.make().showMsg("文件保存失败：" + e.getMessage());
            }
        }
    }

}
