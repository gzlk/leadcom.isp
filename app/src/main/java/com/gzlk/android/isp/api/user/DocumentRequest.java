package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.user.document.Document;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private static class SingleDocument extends Output<Document> {
    }

    private static class MultipleDocument extends Query<Document> {
    }

    @Override
    protected String url(String action) {
        return DOC + action;
    }

    @Override
    public DocumentRequest setOnSingleRequestListener(OnSingleRequestListener<Document> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public DocumentRequest setOnMultipleRequestListener(OnMultipleRequestListener<Document> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    private static final String DOC = "/user/userDoc";

    /**
     * 新增个人档案
     *
     * @param type       档案类型(1.普通,2.个人,3.活动)
     * @param title      档案标题
     * @param content    档案内容(html)
     * @param markdown   档案内容(markdown)
     * @param image      图片地址(json数组)
     * @param attach     附件地址(json数组)
     * @param attachName 附件名(json数组)
     */
    public void add(@NonNull String type, @NonNull String title, String content, String markdown,
                    ArrayList<String> image, ArrayList<String> attach, ArrayList<String> attachName) {
        // {title,type,content,markdown,[image],[attach],[attachName],userId,userName,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("type", type)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("markdown", checkNull(markdown))
                    .put("image", new JSONArray(image))
                    .put("attach", new JSONArray(attach))
                    .put("attachName", new JSONArray(attachName))
                    .put("userId", Cache.cache().userId)
                    .put("userName", checkNull(Cache.cache().userName))
                    .put("accessToken", Cache.cache().accessToken);
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
    public void update(String archiveId, @NonNull String type, @NonNull String title, String content, String markdown,
                       ArrayList<String> image, ArrayList<String> attach, ArrayList<String> attachName) {
        // {_id,type,title,content,markdown,[image],[attach],[attachName],accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("_id", archiveId)
                    .put("type", type)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("markdown", checkNull(markdown))
                    .put("image", new JSONArray(image))
                    .put("attach", new JSONArray(attach))
                    .put("attachName", new JSONArray(attachName))
                    .put("accessToken", Cache.cache().accessToken);
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
    public void list(int pageSize, int pageNumber) {
        httpRequest(getRequest(MultipleDocument.class,
                format("%s?userId=%s&pageSize=%d&pageNumber=%d", url(LIST), Cache.cache().userId, pageSize, pageNumber),
                "", HttpMethods.Get));
    }

    /**
     * 根据档案的标题模糊查询，返回一个结果集合
     */
    public void search(String title) {
        httpRequest(getRequest(MultipleDocument.class, format("%s?info=%s", url(SEARCH), title), "", HttpMethods.Get));
    }
}
