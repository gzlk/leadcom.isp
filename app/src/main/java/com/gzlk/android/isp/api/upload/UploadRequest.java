package com.gzlk.android.isp.api.upload;

import com.gzlk.android.isp.api.Request;
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

import org.json.JSONObject;

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

    private static final String UPLOAD = "/upload/uploadFile";

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

    private JsonRequest<Upload> request(final String file) {
        MultipartBody body = new MultipartBody().addPart(new FilePart("file", new File(file)));
        String path = format("%s%s", URL, UPLOAD);
        log(format("upload file %s\nto %s", file, path));
        final String fileName = file.substring(file.lastIndexOf('/') + 1);
        OnHttpListener<Upload> listener = new OnHttpListener<Upload>(true, true) {
            @Override
            public void onSucceed(Upload data, Response<Upload> response) {
                super.onSucceed(data, response);
                if (data.success()) {
                    try {
                        JSONObject object = new JSONObject(response.getRawString());
                        data.setResult(object.getJSONObject("result"));
                        data.departData();
                        data.setName(fileName);
                        if (null != onSingleRequestListener) {
                            onSingleRequestListener.onResponse(data, data.success(), data.getMsg());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
            public void onUploading(AbstractRequest<Upload> request, long total, long len) {
                super.onUploading(request, total, len);
                if (null != onUploadingListener) {
                    onUploadingListener.onUploading(file, total, len);
                }
            }
        };
        return new JsonRequest<Upload>(path, Upload.class)
                .setHttpListener(listener)
                .addHeader("accessToken", accessToken)
                .addHeader("terminalType", "android")
                .setHttpBody(body, HttpMethods.Post);
    }

    /**
     * 上传单个文件
     */
    public void upload(final String file) {
        liteHttp.executeAsync(request(file));
    }
}
