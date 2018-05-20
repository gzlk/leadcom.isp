package com.leadcom.android.isp.api.archive;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.StringQuery;
import com.leadcom.android.isp.model.archive.Comment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>评论相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/01 20:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/01 20:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CommentRequest extends Request<Comment> {

    public static CommentRequest request() {
        return new CommentRequest();
    }

    private static class SingleComment extends SingleQuery<Comment> {
    }

    private static class StringComment extends StringQuery<Comment> {
    }

    private static class BoolComment extends BoolQuery<Comment> {
    }

    private static class MultiComment extends PaginationQuery<Comment> {
    }

    private static final String USER = "/user/userDoc/comment";
    private static final String GROUP = "/group/groDoc/comment";
    private static final String MOMENT = "/user/userMmtCmt";

    @Override
    protected String url(String action) {
        return USER + action;
    }

    private String url(int type, String action) {
        String api = USER;
        switch (type) {
            case Comment.Type.GROUP:
                api = GROUP;
                break;
            case Comment.Type.MOMENT:
                api = MOMENT;
                break;
        }
        return format("%s%s", api, action);
    }

    private String url(int type) {
        return type == Comment.Type.USER ? USER : GROUP;
    }

    /**
     * 获取档案的id
     * <ul>
     * <li>组织档案：groDocId</li>
     * <li>说说：momentId</li>
     * <li>个人档案：userDocId</li>
     * </ul>
     */
    static String getArchiveId(int type) {
        switch (type) {
            //case Comment.Type.GROUP:
            //    return "groDocId";
            case Comment.Type.MOMENT:
                return "momentId";
            default:
                return "docId";
        }
    }

    /**
     * 获取档案评论的id
     * <ul>
     * <li>组织档案：groDocCmtId</li>
     * <li>说说：momentCmtId</li>
     * <li>个人档案：userDocCmtId</li>
     * </ul>
     */
    private static String getCommentId(int type) {
        switch (type) {
            case Comment.Type.MOMENT:
                return "momentCmtId";
            default:
                return "docCmtId";
        }
    }

    @Override
    protected Class<Comment> getType() {
        return Comment.class;
    }

    @Override
    public CommentRequest setOnSingleRequestListener(OnSingleRequestListener<Comment> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public CommentRequest setOnMultipleRequestListener(OnMultipleRequestListener<Comment> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 新增档案的评论
     */
    public void add(int type, String archiveId, String content, String toUserId) {
        // {momentId,content,toUserId}
        // {userDocId,content,toUserId}
        // {groDocId,content,toUserId}
        JSONObject object = new JSONObject();
        try {
            object.put(getArchiveId(type), archiveId)
                    .put("content", checkNull(content));
            if (!isEmpty(toUserId)) {
                object.put("toUserId", toUserId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(StringComment.class, url(type, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除评论
     */
    public void delete(int type, String archiveId, String commentId) {
        // userDocId,userDocCmtId
        // groDocId,groDocCmtId
        // momentId,momentCmtId
        if (type == Comment.Type.MOMENT) {
            String params = format("%s=%s&%s=%s", getArchiveId(type), archiveId, getCommentId(type), commentId);
            httpRequest(getRequest(SingleComment.class, format("%s?%s", url(type, DELETE), params), "", HttpMethods.Get));
        } else {
            JSONObject object = new JSONObject();
            try {
                object.put(getCommentId(type), commentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            httpRequest(getRequest(BoolComment.class, url(type, DELETE), object.toString(), HttpMethods.Post));
        }
    }

    /**
     * 查找档案的评论列表
     */
    public void list(int type, @NonNull String archiveId, int pageNumber) {
        // userDocId,pageSize,pageNumber
        // groDocId
        // momentId
        httpRequest(getRequest(MultiComment.class,
                format("%s?%s=%s&pageNumber=%d", url(type, LIST), getArchiveId(type), archiveId, pageNumber), "", HttpMethods.Get));
    }
}
