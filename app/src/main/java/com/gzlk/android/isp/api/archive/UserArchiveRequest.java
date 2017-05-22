package com.gzlk.android.isp.api.archive;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.user.UserArchive;
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

public class UserArchiveRequest extends Request<UserArchive> {

    public static UserArchiveRequest request() {
        return new UserArchiveRequest();
    }

    private static class SingleDocument extends Output<UserArchive> {
    }

    private static class MultipleDocument extends Query<UserArchive> {
    }

    @Override
    protected String url(String action) {
        return DOC + action;
    }

    @Override
    public UserArchiveRequest setOnSingleRequestListener(OnSingleRequestListener<UserArchive> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UserArchiveRequest setOnMultipleRequestListener(OnMultipleRequestListener<UserArchive> listListener) {
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
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleDocument.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void delete(@NonNull String documentId) {
        httpRequest(getRequest(SingleDocument.class, format("%s?userDocId=%s", url(DELETE), documentId), "", HttpMethods.Post));
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
    public void list(int pageNumber) {
        // abstrSize,abstrRow,pageSize,pageNumber,accessToken
        httpRequest(getRequest(MultipleDocument.class,
                format("%s?%s&pageSize=%d&pageNumber=%d&accessToken=%s",
                        url(LIST), SUMMARY, PAGE_SIZE, pageNumber, Cache.cache().accessToken),
                "", HttpMethods.Get));
    }

    /**
     * 根据档案的标题模糊查询，返回一个结果集合
     */
    public void search(String title) {
        httpRequest(getRequest(MultipleDocument.class, format("%s?info=%s", url(SEARCH), title), "", HttpMethods.Get));
    }
}
