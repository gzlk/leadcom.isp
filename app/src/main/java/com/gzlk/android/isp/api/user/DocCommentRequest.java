package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.user.document.DocumentComment;
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

public class DocCommentRequest extends Request<DocumentComment> {

    private static DocCommentRequest request;

    public static DocCommentRequest request() {
        if (null == request) {
            request = new DocCommentRequest();
        }
        return request;
    }

    static class SingleComment extends Output<DocumentComment> {
    }

    static class MultiComment extends Query<DocumentComment> {
    }

    private static final String DOC_CMT = "/user/userDocCmt";

    @Override
    protected String url(String action) {
        return DOC_CMT + action;
    }

    @Override
    public DocCommentRequest setOnRequestListener(OnRequestListener<DocumentComment> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public DocCommentRequest setOnRequestListListener(OnRequestListListener<DocumentComment> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    /**
     * 新增档案的评论
     */
    public void add(String documentId, String content) {
        //{userDocId,content,userId,userName}
        JSONObject object = new JSONObject();
        try {
            object.put("userDocId", documentId)
                    .put("content", checkNull(content))
                    .put("userId", Cache.cache().userId)
                    .put("userName", checkNull(Cache.cache().userName));
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
        JSONObject object = new JSONObject();
        try {
            object.put("userDocId", documentId)
                    .put("userDocCmtId", commentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleComment.class, url(DELETE), object.toString(), HttpMethods.Post));
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
