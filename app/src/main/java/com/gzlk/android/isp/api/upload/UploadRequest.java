package com.gzlk.android.isp.api.upload;

import com.gzlk.android.isp.api.Api;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.listener.OnUploadingListener;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.gzlk.android.isp.model.common.Attachment;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.multi.FilePart;
import com.litesuits.http.request.content.multi.MultipartBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;

import java.io.File;

/**
 * <b>功能描述：</b>上传文件<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 16:41 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 16:41 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UploadRequest extends Request<Upload> {

    public static UploadRequest request() {
        return new UploadRequest();
    }

//    private static class SingleFile extends Api<Upload> {
//    }

    private static final String SINGLE_UPLOAD = "http://113.108.144.2:8045/lcbase-manage/upload/uploadFile.do";
    private static final String MULTI_UPLOAD = "http://113.108.144.2:8045/lcbase-manage/upload/uploadFiles.do";
    private static final String OFFICE_UPLOAD = "http://113.108.144.2:8045/lcbase-manage/upload/uploadFilePDF.do";

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<Upload> getType() {
        return Upload.class;
    }

    @Override
    public UploadRequest setOnSingleRequestListener(OnSingleRequestListener<Upload> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UploadRequest setOnMultipleRequestListener(OnMultipleRequestListener<Upload> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 设置上传进度回调
     */
    public UploadRequest setOnUploadingListener(OnUploadingListener<String> listener) {
        onUploadingListener = listener;
        return this;
    }

    private OnUploadingListener<String> onUploadingListener;

    // 根据文件扩展名是否为office文档更改上传路径
    private String path(String file) {
        //return (Attachment.isOffice(Attachment.getExtension(file))) ? OFFICE_UPLOAD : SINGLE_UPLOAD;
        return SINGLE_UPLOAD;
    }

    private JsonRequest<Api<Upload>> request(final String file) {
        MultipartBody body = new MultipartBody().addPart(new FilePart("file", new File(file)));
        return new JsonRequest<Api<Upload>>(path(file), Upload.class).setHttpListener(new OnHttpListener<Api<Upload>>(true, true) {
            @Override
            public void onSucceed(Api<Upload> data, Response<Api<Upload>> response) {
                super.onSucceed(data, response);
                if (data.success()) {
                    Upload upload = (Upload) data;
                    if (null != onSingleRequestListener) {
                        onSingleRequestListener.onResponse(upload, data.success(), data.getMsg());
                    }
                } else {
                    log(format("upload response fail %s", data.getMsg()));
                    ToastHelper.make().showMsg(data.getMsg());
                    fireFailedListenerEvents(data.getMsg());
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                fireFailedListenerEvents("");
            }

            @Override
            public void onUploading(AbstractRequest<Api<Upload>> request, long total, long len) {
                super.onUploading(request, total, len);
                if (null != onUploadingListener) {
                    onUploadingListener.onUploading(file, total, len);
                }
            }
        }).setHttpBody(body, HttpMethods.Post);
    }

    /**
     * 上传单个文件
     */
    public void upload(final String file) {
        liteHttp.executeAsync(request(file));
    }
}