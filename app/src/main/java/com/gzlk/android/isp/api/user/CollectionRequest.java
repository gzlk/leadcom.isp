package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.user.Collection;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>个人收藏相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/04 11:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/04 11:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CollectionRequest extends Request<Collection> {
    private static CollectionRequest request;

    public static CollectionRequest request() {
        if (null == request) {
            request = new CollectionRequest();
        }
        return request;
    }

    static class SingleCollection extends Output<Collection> {
    }

    static class MultipleCollection extends Query<Collection> {
    }

    private static final String COL = "/user/userCol";

    @Override
    protected String url(String action) {
        return COL + action;
    }

    @Override
    public CollectionRequest setOnRequestListener(OnRequestListener<Collection> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public CollectionRequest setOnRequestListListener(OnRequestListListener<Collection> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    public void add(@NonNull String type, String content, @NonNull String creatorId, String creatorName) {
        //{type,content,userId,creatorId,creatorName,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("type", type)
                    .put("content", checkNull(content))
                    .put("userId", Cache.cache().userId)
                    .put("creatorId", checkNull(creatorId))
                    .put("creatorName", checkNull(creatorName))
                    .put("accessToken", Cache.cache().userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleCollection.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void delete(String collectionId) {
        JSONObject object = new JSONObject();
        try {
            object.put("colId", collectionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleCollection.class, url(DELETE), object.toString(), HttpMethods.Post));
    }

    public void update(String collectionId, String content) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", collectionId)
                    .put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleCollection.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public void find(String collectionId) {
        httpRequest(getRequest(SingleCollection.class, format("%s?colId=%s", url(FIND), collectionId), "", HttpMethods.Get));
    }

    public void list(int pageSize, int pageNumber) {
        httpRequest(getRequest(MultipleCollection.class,
                format("%s?userId=%s&pageSize=%d&pageNumber=%d", url(LIST), Cache.cache().userId, pageSize, pageNumber), "", HttpMethods.Get));
    }

    public void search(String userId, String info) {
        httpRequest(getRequest(MultipleCollection.class, format("%s?userId=%s&info=%s", url(SEARCH), userId, info), "", HttpMethods.Get));
    }
}
