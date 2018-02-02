package com.leadcom.android.isp.fragment.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.apache.poi.FileUtils;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.user.Collection;
import com.hlk.hlklib.lib.inject.ViewId;

import java.io.File;

/**
 * <b>功能描述：</b>PDF文件预览<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/14 13:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/14 13:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PdfViewerFragment extends BaseDownloadingUploadingSupportFragment {

    private static final String PARAM_NAME = "pdf_viewer_param_name";
    private static String localReal = "";

    public static PdfViewerFragment newInstance(String params) {
        PdfViewerFragment pvf = new PdfViewerFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putString(PARAM_NAME, strings[1]);
        pvf.setArguments(bundle);
        firstEnter = true;
        return pvf;
    }

    public static void open(Context context, String path, String fileName) {
        localReal = "";
        String params = format("%s,%s", path, fileName);
        BaseActivity.openActivity(context, PdfViewerFragment.class.getName(), params, true, false);
    }


    private String displayName = "";
    private static boolean firstEnter = true;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        displayName = bundle.getString(PARAM_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, displayName);
    }

    @Override
    public void onDestroy() {
        localReal = "";
        super.onDestroy();
    }

    // UI
    @ViewId(R.id.ui_pdf_viewer)
    private PDFView pdfView;

    @Override
    public int getLayout() {
        return R.layout.fragment_viewer_pdf;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(isEmpty(displayName) ? "" : displayName);
        if (firstEnter) {
            firstEnter = false;
            loadingPdf();
        }
        setRightIcon(R.string.ui_icon_more);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (Utils.isUrl(localReal) || Utils.isLocalPath(localReal)) {
                    showDialog();
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
                        tryCollectPdf();
                        break;
                    case R.id.ui_dialog_moment_details_button_save:
                        tryCopy();
                        break;
                }
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true).show();
    }

    private void tryCollectPdf() {
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
            String targetPath = downloadPath + "/" + displayName;
            if (!targetPath.contains(".pdf")) {
                targetPath += ".pdf";
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

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void loadingPdf() {
        if (mQueryId.charAt(0) == '/') {
            // 本地文件直接加载
            onFileDownloadingComplete(mQueryId, mQueryId, true);
        } else {
            if (null != materialHorizontalProgressBar) {
                materialHorizontalProgressBar.setVisibility(View.VISIBLE);
            }
            // 先下载然后再预览
            downloadFile(mQueryId, App.ARCHIVE_DIR);
        }
    }

    @Override
    protected void onFileDownloadingComplete(String url, String local, boolean success) {
        if (success) {
            if (!isEmpty(local)) {
                localReal = local;
                loadLocalPdfDocument(localReal);
            }
        }
        if (null != materialHorizontalProgressBar) {
            materialHorizontalProgressBar.setVisibility(View.GONE);
        }
    }

    private void loadLocalPdfDocument(String filePath) {
        pdfView.fromFile(new File(filePath)).load();
    }
}
