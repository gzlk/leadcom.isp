package com.gzlk.android.isp.api;

import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.listener.OnUploadingListener;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnHttpListener;
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

public class UploadRequest extends Request<String> {

    public static UploadRequest request() {
        return new UploadRequest();
    }

    private static class SingleFile extends Upload<String> {
    }

    private static final String UPLOAD = "http://113.108.144.2:8045/lcbase-manage/upload/uploadFile.do";

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<String> getType() {
        return String.class;
    }

    @Override
    public UploadRequest setOnSingleRequestListener(OnSingleRequestListener<String> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UploadRequest setOnMultipleRequestListener(OnMultipleRequestListener<String> listListener) {
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

    private JsonRequest<SingleFile> request(final String file) {
        MultipartBody body = new MultipartBody()
                .addPart(new FilePart("file", new File(file)));
        return new JsonRequest<SingleFile>(UPLOAD, SingleFile.class).setHttpListener(new OnHttpListener<SingleFile>(true, true) {
            @Override
            public void onSucceed(SingleFile data, Response<SingleFile> response) {
                super.onSucceed(data, response);
                if (data.success()) {
                    if (null != onSingleRequestListener) {
                        onSingleRequestListener.onResponse(data.getResult(), data.success(), data.getMsg());
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
            public void onUploading(AbstractRequest<SingleFile> request, long total, long len) {
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
