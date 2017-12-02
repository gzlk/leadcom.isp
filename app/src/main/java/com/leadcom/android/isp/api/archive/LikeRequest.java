package com.leadcom.android.isp.api.archive;

import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.archive.ArchiveLike;
import com.leadcom.android.isp.model.archive.Comment;
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

public class LikeRequest extends Request<ArchiveLike> {

    public static LikeRequest request() {
        return new LikeRequest();
    }

    private static class SingleLike extends SingleQuery<ArchiveLike> {
    }

    private static class MultiLike extends PaginationQuery<ArchiveLike> {
    }

    private static class BoolLike extends BoolQuery<ArchiveLike> {
    }

    private static final String USER = "/user/userDocLike";
    private static final String GROUP = "/group/groDocLike";
    private static final String MOMENT = "/user/userMmtLike";

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

    @Override
    protected Class<ArchiveLike> getType() {
        return ArchiveLike.class;
    }

    @Override
    public LikeRequest setOnSingleRequestListener(OnSingleRequestListener<ArchiveLike> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public LikeRequest setOnMultipleRequestListener(OnMultipleRequestListener<ArchiveLike> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 点赞
     *
     * @param type 点赞{@link Comment.Type}
     */
    public void add(int type, String archiveId) {
        // {momentId,accessToken}
        // {userDocId,accessToken}
        // {groDocId,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put(CommentRequest.getArchiveId(type), archiveId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleLike.class, url(type, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 取消某个档案的赞
     *
     * @param type 个人或组织档案点赞{@link Comment.Type}
     */
    public void delete(int type, String archiveId) {
        // momentId,accessToken
        // userDocId,accessToken
        // groDocId,accessToken
        String params = format("%s=%s", CommentRequest.getArchiveId(type), archiveId);
        httpRequest(getRequest(SingleLike.class, format("%s?%s", url(type, DELETE), params), "", HttpMethods.Get));
    }

    /**
     * 查询某个档案的赞
     *
     * @param type 个人或组织档案点赞{@link Comment.Type}
     */
    public void list(int type, String archiveId, int pageNumber) {
        // momentId
        // userDocId
        // groDocId
        httpRequest(getRequest(MultiLike.class,
                format("%s?%s=%s&pageNumber=%d", url(type, LIST), CommentRequest.getArchiveId(type), archiveId, pageNumber),
                "", HttpMethods.Get));
    }

    /**
     * 判断用户是否已点赞
     *
     * @param type 个人或组织档案点赞{@link Comment.Type}
     */
    public void isExist(int type, String archiveId) {
        // momentId,accessToken
        // userDocId,accessToken
        // groDocId,accessToken
        httpRequest(getRequest(BoolLike.class,
                format("%s?%s=%s",
                        url(type, "/isExist"),
                        CommentRequest.getArchiveId(type),
                        archiveId), "", HttpMethods.Get));
    }
}
