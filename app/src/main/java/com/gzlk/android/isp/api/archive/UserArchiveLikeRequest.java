package com.gzlk.android.isp.api.archive;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>个人档案点赞相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/01 21:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/01 21:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserArchiveLikeRequest extends Request<ArchiveLike> {

    public static UserArchiveLikeRequest request() {
        return new UserArchiveLikeRequest();
    }

    private static class SingleLike extends Output<ArchiveLike> {
    }

    private static class MultiLike extends Query<ArchiveLike> {
    }

    private static final String LIKE = "/user/userDocLike";

    @Override
    protected String url(String action) {
        return LIKE + action;
    }

    @Override
    public UserArchiveLikeRequest setOnSingleRequestListener(OnSingleRequestListener<ArchiveLike> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UserArchiveLikeRequest setOnMultipleRequestListener(OnMultipleRequestListener<ArchiveLike> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 为某个档案点赞
     */
    public void add(String documentId) {
        // {userDocId,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("userDocId", documentId)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleLike.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 取消某个档案的赞
     */
    public void delete(String documentId) {
        // userDocId,accessToken
        String params = format("userDocId=%s&accessToken=%s", documentId, Cache.cache().accessToken);
        httpRequest(getRequest(SingleLike.class, format("%s?%s", url(DELETE), params), "", HttpMethods.Post));
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
        httpRequest(getRequest(SingleLike.class, format("%s?userDocId=%s&accessToken=%s",
                url("/isExist"), documentId, Cache.cache().accessToken), "", HttpMethods.Get));
    }
}
