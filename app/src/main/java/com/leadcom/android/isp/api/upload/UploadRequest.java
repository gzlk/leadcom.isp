package com.leadcom.android.isp.api.upload;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.listener.OnUploadingListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnHttpListener;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.multi.FilePart;
import com.litesuits.http.request.content.multi.MultipartBody;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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
    private static final String UPLOAD_EX = "/upload/uploadFileExe";

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

    private JsonRequest<Upload> request(ArrayList<String> files) {
        final long timeStart = Utils.timestamp();
        MultipartBody body = new MultipartBody();
        int i = 0;
        for (String file : files) {
            body.addPart(new FilePart(format("file%d", i), new File(file)));
            i++;
        }
        final int size = i;
        String path = format("%s%s", URL, UPLOAD_EX);

        OnHttpListener<Upload> listener = new OnHttpListener<Upload>(true, true) {
            @Override
            public void onSucceed(Upload data, Response<Upload> response) {
                super.onSucceed(data, response);
                long timeEnd = Utils.timestamp();
                if (data.success()) {
                    try {
                        JSONObject object = new JSONObject(response.getRawString());
                        JSONObject result = object.optJSONObject("result");
                        ArrayList<Upload> list = new ArrayList<>();
                        long total = 0;
                        if (null != result) {
                            Iterator<String> keys = result.keys();
                            StringBuilder log = new StringBuilder();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                JSONObject obj = result.optJSONObject(key);
                                Upload upload = new Upload(obj);
                                total += upload.getFileSize();
                                list.add(upload);
                                log.append(format("\nupload %s(%s) to %s", upload.getOrgName(), Utils.formatSize(upload.getFileSize()), upload.getFilePath())).append("\r");
                            }
                            log(log.toString());
                        }
                        log(format("uploading %d files(total size: %s), %s, used time: %dms", size, Utils.formatSize(total), data.success(), timeEnd - timeStart));
                        if (null != onMultipleRequestListener) {
                            onMultipleRequestListener.onResponse(list, data.success(), 0, 0, 0, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    log(format("upload response fail %s", data.getMsg()));
                    ToastHelper.helper().showMsg(data.getMsg());
                    if (null != onMultipleRequestListener) {
                        onMultipleRequestListener.failedCode = -1;
                        onMultipleRequestListener.failedMessage = data.getMsg();
                    }
                    fireFailedListenerEvents(data.getMsg());
                }
            }

            @Override
            public void onFailed(int code, String message) {
                super.onFailed(code, message);
                if (null != onMultipleRequestListener) {
                    onMultipleRequestListener.failedCode = code;
                    onMultipleRequestListener.failedMessage = message;
                }
                fireFailedListenerEvents(message);
            }

            @Override
            public void onUploading(AbstractRequest<Upload> request, long total, long len) {
                super.onUploading(request, total, len);
//                if (null != onUploadingListener) {
//                    onUploadingListener.onUploading(file, total, len);
//                }
            }
        };
        JsonRequest<Upload> request = new JsonRequest<Upload>(path, Upload.class)
                .setHttpListener(listener)
                // 上传时默认仅使用网络模式，不使用缓存
                .setCacheMode(CacheMode.NetOnly)
                .addHeader("accessToken", accessToken)
                .addHeader("terminalType", "android")
                .setHttpBody(body, HttpMethods.Post);
        if (App.app().needSetConnectionCloseHeader()) {
            request.addHeader("Connection", "close");
            log("Add \"Connection: close\" header.");
        }
        return request;
    }

    /**
     * 上传单个文件
     */
    public void upload(final String file) {
        ArrayList<String> list = new ArrayList<>();
        list.add(file);
        upload(list);
    }

    public void upload(ArrayList<String> files) {
        http.getConfig()
                .setConnectTimeout(300000)
                .setSocketTimeout(300000);
        http.executeAsync(request(files));
    }
}
