package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.organization.archive.ArchiveComment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>组织档案评论<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/15 16:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/15 16:39 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveCommentRequest extends Request<ArchiveComment> {

    private static class SingleComment extends Output<ArchiveComment> {
    }

    private static class MultipleComment extends Query<ArchiveComment> {
    }

    private static final String CMT = "/group/groDocCmt";

    @Override
    protected String url(String action) {
        return format("%s%s", CMT, action);
    }

    @Override
    public ArchiveCommentRequest setOnRequestListener(OnRequestListener<ArchiveComment> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public ArchiveCommentRequest setOnRequestListListener(OnRequestListListener<ArchiveComment> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    /**
     * 对档案发表评论
     */
    public void add(String archiveId, String content) {
        //{groDocId,content,userId,userName}

        JSONObject object = new JSONObject();
        try {
            object.put("groDocId", archiveId)
                    .put("content", content)
                    .put("userId", Cache.cache().userId)
                    .put("userName", checkNull(Cache.cache().userName));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleComment.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除一个评论
     */
    public void delete(String archiveId, String commentId) {
        //groDocId,groDocCmtId

        JSONObject object = new JSONObject();
        try {
            object.put("groDocId", archiveId)
                    .put("groDocCmtId", commentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleComment.class, url(DELETE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查找单个评论
     */
    public void find(String commentId) {
        httpRequest(getRequest(SingleComment.class, format("%s?groDocCmtId=%s", url(FIND), commentId), "", HttpMethods.Get));
    }

    /**
     * 查找单个档案的所有评论
     */
    public void list(String archiveId) {
        httpRequest(getRequest(MultipleComment.class, format("%s?groDocId=%s", url(LIST), archiveId), "", HttpMethods.Get));
    }
}
