package com.gzlk.android.isp.api.user;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.user.document.DocumentLike;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>档案点赞相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/01 21:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/01 21:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocLikeRequest extends Request<DocumentLike> {

    private static DocLikeRequest request;

    public static DocLikeRequest request() {
        if (null == request) {
            request = new DocLikeRequest();
        }
        return request;
    }

    static class SingleLike extends Output<DocumentLike> {
    }

    static class MultiLike extends Query<DocumentLike> {
    }

    private static final String LIKE = "/user/userDocLike";

    @Override
    protected String url(String action) {
        return LIKE + action;
    }

    @Override
    public DocLikeRequest setOnSingleRequestListener(OnSingleRequestListener<DocumentLike> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public DocLikeRequest setOnMultipleRequestListener(OnMultipleRequestListener<DocumentLike> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 为某个档案点赞
     */
    public void add(String documentId) {
        //{userDocId,userId}
        JSONObject object = new JSONObject();
        try {
            object.put("userDocId", documentId)
                    .put("userId", Cache.cache().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleLike.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 取消某个档案的赞
     */
    public void delete(String documentId) {
        JSONObject object = new JSONObject();
        try {
            object.put("userDocId", documentId)
                    .put("userId", Cache.cache().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleLike.class, url(DELETE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查询某个档案的赞
     */
    public void list(String documentId) {
        httpRequest(getRequest(MultiLike.class, format("%s?userDocId=%s", url(LIST), documentId), "", HttpMethods.Get));
    }

    /**
     * 判断用户是否已点赞
     */
    public void isExist(String documentId) {
        httpRequest(getRequest(SingleLike.class, format("%s?userDocId=%s&userId=%s", url("/isExist"), documentId, Cache.cache().userId), "", HttpMethods.Get));
    }
}
