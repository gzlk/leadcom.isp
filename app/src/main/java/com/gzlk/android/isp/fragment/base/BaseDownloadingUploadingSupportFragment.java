package com.gzlk.android.isp.fragment.base;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.hlk.hlklib.lib.inject.ViewId;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.FileRequest;
import com.litesuits.http.response.Response;

import java.io.File;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * <b>功能描述：</b>支持上传和下载方法的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 09:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 09:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseDownloadingUploadingSupportFragment extends BaseNothingLoadingSupportFragment {

    /**
     * 进度框
     */
    private ProgressDialog progressDialog = null;
    private TextView handlingTextView;

    /**
     * 显示图片处理对话框
     */
    protected void showImageHandlingDialog() {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(Activity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
        View progressDialogView = View.inflate(Activity(), R.layout.popup_dialog_image_handling, null);
        handlingTextView = (TextView) progressDialogView.findViewById(R.id.ui_dialog_image_handling_text);
        progressDialog.setContentView(progressDialogView);
    }

    /**
     * 显示图片处理对话框并指定要显示的文字
     */
    protected void showImageHandlingDialog(int resId) {
        showImageHandlingDialog();
        handlingTextView.setText(resId);
    }

    protected void showImageHandlingDialog(String text) {
        showImageHandlingDialog();
        handlingTextView.setText(text);
    }

    /**
     * 隐藏图片处理对话框
     */
    protected void hideImageHandlingDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }

    /**
     * 上传文件
     */
    protected void uploadFile(String path) {

    }

    protected void uploadFile(String path, boolean bool) {
    }

    @ViewId(R.id.ui_tool_horizontal_progressbar)
    public MaterialProgressBar materialHorizontalProgressBar;

    private void showHorizontalProgress() {
        if (null != materialHorizontalProgressBar) {
            materialHorizontalProgressBar.setMax(100);
            materialHorizontalProgressBar.setProgress(0);
            materialHorizontalProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideHorizontalProgress() {
        if (null != materialHorizontalProgressBar && materialHorizontalProgressBar.getVisibility() == View.VISIBLE) {
            materialHorizontalProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 下载文件到本地
     */
    protected void downloadFile(String url, final String local) {
        FileRequest fileRequest = new FileRequest(url, local);
        fileRequest.setHttpListener(new OnHttpListener<File>(true, false) {
            @Override
            public void onSucceed(File data, Response<File> response) {
                super.onSucceed(data, response);
                ToastHelper.make(Activity()).showMsg(StringHelper.getString(R.string.ui_base_text_downloading_completed, local));
            }

            @Override
            public void onFailed() {
                super.onFailed();
            }

            @Override
            public void onStart(AbstractRequest<File> request) {
                super.onStart(request);
                showHorizontalProgress();
            }

            @Override
            public void onLoading(AbstractRequest<File> request, long total, long len) {
                super.onLoading(request, total, len);
                log(format("loading %d/%d", len, total));
                if (null != materialHorizontalProgressBar) {
                    if (materialHorizontalProgressBar.getMax() != total) {
                        materialHorizontalProgressBar.setMax((int) total);
                    }
                    materialHorizontalProgressBar.setProgress((int) len);
                }
            }

            @Override
            public void onEnd(Response<File> response) {
                super.onEnd(response);
                hideHorizontalProgress();
            }
        });
        httpRequest(fileRequest);
    }
}
