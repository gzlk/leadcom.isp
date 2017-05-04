package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.model.user.document.Document;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>个人档案相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/26 00:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/26 00:10 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocumentRequest extends Request<Document> {

    private static DocumentRequest request;

    public static DocumentRequest request() {
        if (null == request) {
            request = new DocumentRequest();
        }
        return request;
    }

    static class SingleDocument extends Output<Document> {
    }

    static class MultipleDocument extends Query<Document> {
    }

    @Override
    protected String url(String action) {
        return DOC + action;
    }

    @Override
    public DocumentRequest setOnRequestListener(OnRequestListener<Document> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public DocumentRequest setOnRequestListListener(OnRequestListListener<Document> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    private static final String DOC = "/user/userDoc";

    /**
     * 添加档案
     */
    public void add(String title, String content, String type, String userId, String userName, String accessToken) {
        //title,content,type,userId,userName,accessToken

        JSONObject object = new JSONObject();
        try {
            object.put("title", title)
                    .put("content", checkNull(content))
                    .put("type", checkNull(type))
                    .put("userId", userId)
                    .put("userName", checkNull(userName))
                    .put("accessToken", accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleDocument.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void delete(@NonNull String documentId) {

        JSONObject object = new JSONObject();
        try {
            object.put("userDocId", documentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleDocument.class, url(DELETE), object.toString(), HttpMethods.Post));

    }

    /**
     * 更改档案的内容
     */
    @SuppressWarnings("ConstantConditions")
    public void update(String documentId, String title, String content, String type) {
        //{_id,title,content,type,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("_id", documentId);
            object.put("title", title);
            object.put("content", content);
            object.put("type", type);
            object.put("accessToken", App.app().UserToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleDocument.class, url(DELETE), object.toString(), HttpMethods.Post));

    }

    /**
     * 根据档案id查找档案详细属性
     */
    public void find(@NonNull String documentId) {
        httpRequest(getRequest(SingleDocument.class, format("%s?userDocId=%s", url(FIND), documentId), "", HttpMethods.Get));
    }

    /**
     * 查找指定用户的档案列表，返回一个结果集合
     */
    public void list(@NonNull String userId, int pageSize, int pageNumber) {
        httpRequest(getRequest(MultipleDocument.class,
                format("%s?userId=%s&pageSize=%d&pageNumber=%d", url(LIST), userId, pageSize, pageNumber),
                "", HttpMethods.Get));
    }

    /**
     * 根据档案的标题模糊查询，返回一个结果集合
     */
    public void search(String title) {
        httpRequest(getRequest(MultipleDocument.class, format("%s?info=%s", url(SEARCH), title), "", HttpMethods.Get));
    }
}
