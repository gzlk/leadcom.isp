package com.leadcom.android.isp.api.activity;

import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.activity.ActArchive;
import com.leadcom.android.isp.model.common.Attachment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>活动文档存档、审核相关接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/31 17:53 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/31 17:53 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActArchiveRequest extends Request<ActArchive> {

    public static ActArchiveRequest request() {
        return new ActArchiveRequest();
    }

    private static class SingleActivityArchive extends SingleQuery<ActArchive> {
    }

    private static class MultipleActivityArchive extends PaginationQuery<ActArchive> {
    }

    private static final String DOC = "/activity/actDoc";
    private static final String CALLBACK = "/uploadCallback";

    @Override
    protected String url(String action) {
        return format("%s%s", DOC, action);
    }

    @Override
    protected Class<ActArchive> getType() {
        return ActArchive.class;
    }

    @Override
    public ActArchiveRequest setOnSingleRequestListener(OnSingleRequestListener<ActArchive> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ActArchiveRequest setOnMultipleRequestListener(OnMultipleRequestListener<ActArchive> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 文件上传之后的回调
     */
    public void uploadCallback(Attachment attachment) {
        // actId,type,name,url,pdf,accessToken
        JSONObject object = new JSONObject();
        try {
            object.put("actId", attachment.getArchiveId())
                    .put("type", attachment.getAttachmentType())
                    .put("name", attachment.getName())
                    .put("url", attachment.getUrl())
                    .put("pdf", attachment.getPdf());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivityArchive.class, url(CALLBACK), object.toString(), HttpMethods.Post));
    }

    /**
     * 更改活动中文件的存档属性
     *
     * @param fileId 存档文件的id
     * @param status 存档状态：审核状态(1.未审核,2.已通过,3.未通过[暂时不需要]) {@link com.leadcom.android.isp.model.common.Attachment.AttachmentStatus}
     * @see com.leadcom.android.isp.model.common.Attachment.AttachmentStatus
     */
    public void update(String fileId, String activityId, int status) {
        // _id,status,accessToken
        JSONObject object = new JSONObject();
        try {
            object.put("_id", fileId)
                    .put("actId", activityId)
                    .put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivityArchive.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 搜索活动中的文件列表
     *
     * @param activityId     活动ID
     * @param attachmentType 筛选文件类型(不传[所有],1.文档,2.图片,3.视频) {@link com.leadcom.android.isp.model.common.Attachment.AttachmentType}
     * @see com.leadcom.android.isp.model.common.Attachment.AttachmentType
     */
    public void list(String activityId, int attachmentType, int pageNumber) {
        // actId,type
        String param = format("actId=%s%s&pageNumber=%d", activityId, (0 == attachmentType ? "" : format("&type=%d", attachmentType)), pageNumber);
        httpRequest(getRequest(MultipleActivityArchive.class, format("%s?%s", url(LIST), param), "", HttpMethods.Get));
    }

    /**
     * 在活动中搜索文件列表
     *
     * @param activityId     活动的id
     * @param info           模糊搜索内容
     * @param attachmentType 文件类型，为0时搜索全部文件名{@link com.leadcom.android.isp.model.common.Attachment.AttachmentType}
     * @see com.leadcom.android.isp.model.common.Attachment.AttachmentType
     */
    public void search(String activityId, String info, int attachmentType) {
        // actId,info,type
        String param = format("actId=%s&info=%s%s", activityId, info, (0 == attachmentType ? "" : format("&type=%d", attachmentType)));
        httpRequest(getRequest(MultipleActivityArchive.class, format("%s?%s", url(LIST), param), "", HttpMethods.Get));
    }
}
