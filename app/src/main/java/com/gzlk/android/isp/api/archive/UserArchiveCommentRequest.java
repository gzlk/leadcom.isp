package com.gzlk.android.isp.api.archive;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.archive.Comment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>档案评论相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/01 20:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/01 20:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserArchiveCommentRequest extends Request<Comment> {

    public static UserArchiveCommentRequest request() {
        return new UserArchiveCommentRequest();
    }

    private static class SingleComment extends Output<Comment> {
    }

    private static class MultiComment extends Query<Comment> {
    }

    private static final String DOC_CMT = "/user/userDocCmt";

    @Override
    protected String url(String action) {
        return DOC_CMT + action;
    }

    @Override
    public UserArchiveCommentRequest setOnSingleRequestListener(OnSingleRequestListener<Comment> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UserArchiveCommentRequest setOnMultipleRequestListener(OnMultipleRequestListener<Comment> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 新增档案的评论
     */
    public void add(String documentId, String content) {
        // {userDocId,content,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("userDocId", documentId)
                    .put("content", checkNull(content))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleComment.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除评论
     */
    public void delete(String documentId, String commentId) {
        //userDocId,userDocCmtId
        String params = format("userDocId=%s&userDocCmtId=%s", documentId, commentId);
        httpRequest(getRequest(SingleComment.class, format("%s?%s", url(DELETE), params), "", HttpMethods.Post));
    }

    /**
     * 查询单个档案的单个评论
     */
    public void find(@NonNull String commentId) {
        httpRequest(getRequest(SingleComment.class, format("%s?userDocCmtId=%s", url(FIND), commentId), "", HttpMethods.Get));
    }

    /**
     * 查找档案的评论列表
     */
    public void list(@NonNull String documentId, int pageSize, int pageNumber) {
        //userDocId,pageSize,pageNumber
        httpRequest(getRequest(MultiComment.class,
                format("%s?userDocId=%s&pageSize=%d&pageNumber=%d", url(LIST), documentId, pageSize, pageNumber),
                "", HttpMethods.Get));
    }
}
