package com.gzlk.android.isp.fragment.common;

import android.os.Bundle;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
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
        setRightText(R.string.ui_base_text_favorite);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {

            }
        });
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
            loadLocalPdfDocument(mQueryId);
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
                loadLocalPdfDocument(local);
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
