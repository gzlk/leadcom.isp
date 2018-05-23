package com.leadcom.android.isp.fragment.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnUploadingListener;
import com.leadcom.android.isp.api.upload.Upload;
import com.leadcom.android.isp.api.upload.UploadRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.common.Attachment;

import java.util.ArrayList;
import java.util.List;

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

public abstract class BaseDownloadingUploadingSupportFragment extends BaseTransparentSupportFragment {

    private static final String KEY_HANDLED_FILES = "handled_files_";
    private static final String KEY_UPLOADED_FILES = "uploaded_files_";
    private static final String KEY_MAX_SELECTABLE = "max_selectable_size";
    private static final String KEY_DIRECTLY_UPLOAD = "directly_upload";
    private static final String KEY_SHOW_UPLOADING = "show_uploading";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        maxSelectable = bundle.getInt(KEY_MAX_SELECTABLE, defaultMaxSelectable());
        isSupportDirectlyUpload = bundle.getBoolean(KEY_DIRECTLY_UPLOAD, true);
        String string = bundle.getString(KEY_HANDLED_FILES, "[]");
        waitingForUploadFiles = Json.gson().fromJson(string, new TypeToken<List<String>>() {
        }.getType());
        string = bundle.getString(KEY_UPLOADED_FILES, "[]");
        uploadedFiles = Json.gson().fromJson(string, new TypeToken<List<String>>() {
        }.getType());
        needShowUploading = bundle.getBoolean(KEY_SHOW_UPLOADING, true);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(KEY_MAX_SELECTABLE, maxSelectable);
        bundle.putBoolean(KEY_DIRECTLY_UPLOAD, isSupportDirectlyUpload);
        bundle.putString(KEY_HANDLED_FILES, Json.gson().toJson(waitingForUploadFiles));
        bundle.putString(KEY_UPLOADED_FILES, Json.gson().toJson(uploadedFiles));
        bundle.putBoolean(KEY_SHOW_UPLOADING, needShowUploading);
    }

    /**
     * 标记是否直接上传图片，默认直接上传
     */
    protected boolean isSupportDirectlyUpload = true;
    /**
     * 上传的时候是否需要显示上传进度
     */
    protected boolean needShowUploading = true;

    protected int maxSelectable = 0;

    private int defaultMaxSelectable() {
        return StringHelper.getInteger(R.integer.integer_max_image_pick_size);
    }

    protected int getMaxSelectable() {
        if (0 == maxSelectable) {
            maxSelectable = defaultMaxSelectable();
        }
        return maxSelectable;
    }

    /**
     * 已选择且已压缩了的图片缓存列表，或者非图片的源文件列表
     */
    private ArrayList<String> waitingForUploadFiles = new ArrayList<>();
    /**
     * 已上传了的图片列表
     */
    private ArrayList<Attachment> uploadedFiles = new ArrayList<>();

    /**
     * 已上传的图片地址列表
     */
    protected ArrayList<Attachment> getUploadedFiles() {
        return uploadedFiles;
    }

    /**
     * 已压缩后的本地图片地址列表
     */
    protected ArrayList<String> getWaitingForUploadFiles() {
        return waitingForUploadFiles;
    }

    private void onImageUploading(int index, String file, long size, long uploaded) {
        //log(format("index: %d, file: %s, size: %d, uploaded: %d", index, file, size, uploaded));
        if (null != mOnFileUploadingListener) {
            mOnFileUploadingListener.onUploading(getWaitingForUploadFiles().size(), index + 1, file, size, uploaded);
        }
    }

    /**
     * 上传文件
     */
    protected void uploadFiles() {
        if (getWaitingForUploadFiles().size() > 0) {
            showImageHandlingDialog(R.string.ui_base_text_uploading);
            uploading();
        } else {
            log("no file(s) waiting for upload.");
            onUploadingFailed();
        }
    }

    protected void onUploadingFailed() {
    }

    private void uploading() {
        UploadRequest.request().setOnMultipleRequestListener(uploadingSuccess)
                .setOnUploadingListener(uploadingListener).upload(waitingForUploadFiles);
    }

    private OnMultipleRequestListener<Upload> uploadingSuccess = new OnMultipleRequestListener<Upload>() {
        @Override
        public void onResponse(List<Upload> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
            super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
            if (success) {
                if (null != list && list.size() > 0) {
                    for (Upload upload : list) {
                        uploadedFiles.add(new Attachment(upload));
                    }
                }
                // 上传完毕
                hideImageHandlingDialog();

                if (null != mOnFileUploadingListener) {
                    mOnFileUploadingListener.onUploadingComplete(uploadedFiles);
                }
            } else {
                // 上传失败
                hideImageHandlingDialog();
                onUploadingFailed();
            }
        }
    };

    private OnUploadingListener<String> uploadingListener = new OnUploadingListener<String>() {
        @Override
        public void onUploading(String s, long total, long length) {
            super.onUploading(s, total, length);
            // 上传进度
            //onImageUploading(uploadingIndex, s, total, length);
        }
    };

    /**
     * 进度框
     */
    private ProgressDialog progressDialog = null;
    private View progressDialogView;
    private TextView handlingTextView;

    /**
     * 显示图片处理对话框
     */
    protected void showImageHandlingDialog() {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(Activity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialogView = View.inflate(Activity(), R.layout.popup_dialog_image_handling, null);
            handlingTextView = progressDialogView.findViewById(R.id.ui_dialog_image_handling_text);
        }
        if (needShowUploading && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        //clearDirectParent(progressDialogView);
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
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        hideImageHandlingDialog();
        super.onDestroyView();
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

    private void handleMaterialHorizontalProgressBar(int current, int total) {
        if (null != materialHorizontalProgressBar) {
            int per = (int) ((current * 1.0 / total) * 100);
            materialHorizontalProgressBar.setProgress(per);
        }
    }

    /**
     * 下载文件到本地Image目录
     */
    protected void downloadFile(String url) {
        downloadFile(url, App.IMAGE_DIR);
    }

    private String callBackHost() {
        return Integer.toHexString(hashCode());
    }

    private void removeCallback() {
        HttpHelper.helper().removeCallback(callBackHost());
    }

    /**
     * 下载文件到本地指定目录
     */
    protected void downloadFile(final String url, final String dir) {
        HttpHelper.helper().addCallback(new HttpHelper.HttpHelperCallback() {
            @Override
            public void onCancel(int current, int total) {
                log(format("onCancel %d of %d", current, total));
                handleMaterialHorizontalProgressBar(current, total);
                removeCallback();
            }

            @Override
            public void onStart(int current, int total, String startedUrl) {
                showHorizontalProgress();
                log(format("onStart %d of %d", current, total));
                handleMaterialHorizontalProgressBar(current, total);
            }

            @Override
            public void onProgressing(int current, int total, int currentHandled, int currentTotal, String processingUrl) {
                //log(format("onProgressing %d of %d, handled %d of %d(%f)", current, total, currentHandled, currentTotal, (currentHandled * 1.0 / currentTotal)));
                if (null != materialHorizontalProgressBar) {
                    handleMaterialHorizontalProgressBar(current, total);
                    int per = (int) ((currentHandled * 1.0 / currentTotal) * 100);
                    materialHorizontalProgressBar.setSecondaryProgress(per);
                }
            }

            @Override
            public void onSuccess(int current, int total, String successUrl) {
                log(format("onSuccess %d of %d", current, total));
                if (null != materialHorizontalProgressBar) {
                    int per = (int) ((current * 1.0 / total) * 100);
                    materialHorizontalProgressBar.setProgress(per);
                }
                onFileDownloadingComplete(url, successUrl, true);
                removeCallback();
            }

            @Override
            public void onFailure(int current, int total, String failureUrl) {
                log(format("onFailure %d of %d", current, total));
                handleMaterialHorizontalProgressBar(current, total);
                onFileDownloadingComplete(url, failureUrl, false);
                removeCallback();
            }

            @Override
            public void onStop(int current, int total) {
                log(format("onStop %d of %d", current, total));
                hideHorizontalProgress();
                handleMaterialHorizontalProgressBar(current, total);
                removeCallback();
            }
        }, callBackHost()).setLocalDirectory(dir).clearTask().addTask(url).setIgnoreExist(true).download();
//        FileRequest fileRequest = new FileRequest(url, local);
//        fileRequest.setHttpListener(new OnHttpListener<File>(true, false) {
//            @Override
//            public void onSucceed(File data, Response<File> response) {
//                super.onSucceed(data, response);
//                ToastHelper.make(Activity()).showMsg(StringHelper.getString(R.string.ui_base_text_downloading_completed, local));
//            }
//
//            @Override
//            public void onFailed() {
//                super.onFailed();
//            }
//
//            @Override
//            public void onStart(AbstractRequest<File> request) {
//                super.onStart(request);
//                showHorizontalProgress();
//            }
//
//            @Override
//            public void onLoading(AbstractRequest<File> request, long total, long len) {
//                super.onLoading(request, total, len);
//                log(format("loading %d/%d", len, total));
//                if (null != materialHorizontalProgressBar) {
//                    if (materialHorizontalProgressBar.getMax() != total) {
//                        materialHorizontalProgressBar.setMax((int) total);
//                    }
//                    materialHorizontalProgressBar.setProgress((int) len);
//                }
//            }
//
//            @Override
//            public void onEnd(Response<File> response) {
//                super.onEnd(response);
//                hideHorizontalProgress();
//            }
//        });
//        httpRequest(fileRequest);
    }

    /**
     * 指定的文件已经下载完毕，子类需要重载此方法以获取结果
     */
    protected void onFileDownloadingComplete(String url, String local, boolean success) {
    }

    private OnFileUploadingListener mOnFileUploadingListener;

    /**
     * 设置图片上传进度回调
     */
    protected void setOnFileUploadingListener(OnFileUploadingListener l) {
        mOnFileUploadingListener = l;
    }

    /**
     * 文件上传进度回调
     */
    protected interface OnFileUploadingListener {
        /**
         * 上传进度
         */
        void onUploading(int all, int current, String file, long size, long uploaded);

        /**
         * 上传完成
         */
        void onUploadingComplete(ArrayList<Attachment> uploaded);
    }
}
