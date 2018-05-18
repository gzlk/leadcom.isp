package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>获取档案分享内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/07 10:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/12/07 10:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ShareRequest extends Request<ShareInfo> {

    public static ShareRequest request() {
        return new ShareRequest();
    }

    private static class SingleShare extends SingleQuery<ShareInfo> {
    }

    @Override
    protected String url(String action) {
        return format("/system/share%s", action);
    }

    @Override
    protected Class<ShareInfo> getType() {
        return ShareInfo.class;
    }

    @Override
    public ShareRequest setOnSingleRequestListener(OnSingleRequestListener<ShareInfo> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ShareRequest setOnMultipleRequestListener(OnMultipleRequestListener<ShareInfo> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public static final int ARCHIVE_USER = 1;
    public static final int ARCHIVE_GROUP = 2;

    /**
     * 获取分享内容
     *
     * @param archiveId   档案id
     * @param contentType 内容类别：1=档案
     * @param archiveType 档案类别：1=个人档案；2=组织档案
     */
    public void getShareInfo(String archiveId, int contentType, int archiveType) {
        directlySave = false;
        String params = format("%s?id=%s&contentType=%d&docType=%d", url("/getShareInfo"), archiveId, contentType, archiveType);
        httpRequest(getRequest(SingleShare.class, params, "", HttpMethods.Get));
    }

    /**
     * 获取档案草稿分享内容
     * @param type 3=个人档案草稿，4=组织档案草稿
     */
    public void getDraftShareInfo(Archive archive, int type) {
        directlySave = false;
        JSONObject object = new JSONObject();
        try {
            object.put("title", archive.getTitle())// 必要字段
                    .put("cover", checkNull(archive.getCover()))
                    .put("type", type)// 必要字段
                    .put("groupId", archive.getGroupId())// 必要字段
                    .put("content", archive.getContent())
                    //.put("markdown", archive.getMarkdown())
                    .put("office", new JSONArray(Attachment.getJson(archive.getOffice())))
                    .put("image", new JSONArray(Attachment.getJson(archive.getImage())))
                    .put("video", new JSONArray(Attachment.getJson(archive.getVideo())))
                    .put("attach", new JSONArray(Attachment.getJson(archive.getAttach())))
                    .put("fileIds", checkNull(archive.getFileIds()))
                    .put("userId", archive.getUserId())
                    .put("userName", archive.getUserName())
                    .put("createDate", archive.getCreateDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleShare.class, url("/getDraftShareInfo"), object.toString(), HttpMethods.Post));
    }
}
