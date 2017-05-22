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
 * <b>功能描述：</b>组织档案点赞<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/15 16:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/15 16:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupArchiveLikeRequest extends Request<ArchiveLike> {

    public static GroupArchiveLikeRequest request() {
        return new GroupArchiveLikeRequest();
    }

    private static class SingleLike extends Output<ArchiveLike> {
    }

    private static class MultipleLike extends Query<ArchiveLike> {
    }

    // 组织档案点赞
    private static final String LIKE = "/group/groDocLike";

    @Override
    protected String url(String action) {
        return format("%s%s", LIKE, action);
    }

    @Override
    public GroupArchiveLikeRequest setOnSingleRequestListener(OnSingleRequestListener<ArchiveLike> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public GroupArchiveLikeRequest setOnMultipleRequestListener(OnMultipleRequestListener<ArchiveLike> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 点赞档案
     */
    public void like(String archiveId) {
        //{groDocId,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("groDocId", archiveId)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleLike.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 取消赞
     */
    public void unlike(String archiveId) {
        String params = format("groDocId=%s&accessToken=%s", archiveId, Cache.cache().accessToken);
        httpRequest(getRequest(SingleLike.class, format("%s?%s", url(DELETE), params), "", HttpMethods.Post));
    }

    /**
     * 档案的点赞列表
     */
    public void list(String archiveId) {
        httpRequest(getRequest(MultipleLike.class, format("%s?groDocId=%s", url(LIST), archiveId), "", HttpMethods.Get));
    }

    public void exist(String archiveId) {
        httpRequest(getRequest(SingleLike.class, format("%s?groDocId=%s", url("/isExist"), archiveId), "", HttpMethods.Get));
    }
}
