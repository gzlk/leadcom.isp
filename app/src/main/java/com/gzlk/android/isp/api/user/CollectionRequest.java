package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
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

    public static CollectionRequest request() {
        return new CollectionRequest();
    }

    private static class SingleCollection extends Output<Collection> {
    }

    private static class MultipleCollection extends Query<Collection> {
    }

    private static final String COL = "/user/userCol";

    @Override
    protected String url(String action) {
        return COL + action;
    }

    @Override
    protected Class<Collection> getType() {
        return Collection.class;
    }

    @Override
    public CollectionRequest setOnSingleRequestListener(OnSingleRequestListener<Collection> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public CollectionRequest setOnMultipleRequestListener(OnMultipleRequestListener<Collection> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void add(int type, String content, @NonNull String creatorId, String creatorName) {
        // {type,content,creatorId,creatorName,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("type", type)
                    .put("content", checkNull(content))
                    .put("creatorId", checkNull(creatorId))
                    .put("creatorName", checkNull(creatorName))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleCollection.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void delete(String collectionId) {
        // colId
        httpRequest(getRequest(SingleCollection.class, format("%s?colId=%s", url(DELETE), collectionId), "", HttpMethods.Post));
    }

    public void update(String collectionId, String content) {
        // {_id,content,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", collectionId)
                    .put("content", content)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleCollection.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public void find(String collectionId) {
        // colId
        httpRequest(getRequest(SingleCollection.class, format("%s?colId=%s", url(FIND), collectionId), "", HttpMethods.Get));
    }

    public void list(String accessToken, int pageNumber) {
        // accessToken
        httpRequest(getRequest(MultipleCollection.class,
                format("%s?pageSize=%d&pageNumber=%d&accessToken=%s", url(LIST), PAGE_SIZE, pageNumber, accessToken), "", HttpMethods.Get));
    }

    public void search(String accessToken, String info) {
        // info,accessToken
        httpRequest(getRequest(MultipleCollection.class, format("%s?info=%s&accessToken=%s", url(SEARCH), accessToken, info), "", HttpMethods.Get));
    }
}
