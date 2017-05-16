package com.gzlk.android.isp.api.org;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.organization.archive.Archive;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/15 15:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/15 15:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveRequest extends Request<Archive> {

    public static ArchiveRequest request() {
        return new ArchiveRequest();
    }

    /**
     * 用户档案
     */
    public static final int USER = 0;
    /**
     * 组织档案
     */
    public static final int GROUP = 1;

    static class SingleArchive extends Output<Archive> {
    }

    static class MultipleArchive extends Query<Archive> {
    }

    private static final String DOC = "/group/groDoc";
    private static final String APPROVING = "/toBeAppr" + LIST;
    private static final String APPROVED = "/approved" + LIST;

    @Override
    protected String url(String action) {
        return format("%s%s", DOC, action);
    }

    @Override
    public ArchiveRequest setOnRequestListener(OnRequestListener<Archive> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public ArchiveRequest setOnRequestListListener(OnRequestListListener<Archive> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    /**
     * 新增组织档案（待审核）
     *
     * @param groupId    组织id
     * @param source     档案来源
     * @param type       档案类型(1.普通,2.个人,3.活动)
     * @param title      档案标题
     * @param content    档案内容(html)
     * @param markdown   档案内容(markdown)
     * @param image      图片地址(json数组)
     * @param attach     附件地址(json数组)
     * @param attachName 附件名(json数组)
     */
    public void add(@NonNull String groupId, String source, @NonNull String type, @NonNull String title, String content, String markdown,
                    ArrayList<String> image, ArrayList<String> attach, ArrayList<String> attachName) {
        //{groupId,source,type,title,content,markdown,[image],[attach],[attachName],userId,userName,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("source", source)
                    .put("type", type)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("markdown", checkNull(markdown))
                    .put("image", new JSONArray(image))
                    .put("attach", new JSONArray(attach))
                    .put("attachName", new JSONArray(attachName))
                    .put("userId", Cache.cache().userId)
                    .put("userName", checkNull(Cache.cache().userName))
                    .put("accessToken", Cache.cache().userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleArchive.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除组织档案（待审核和已审核）
     */
    public void delete(String archiveId) {
        JSONObject object = new JSONObject();
        try {
            object.put("groDocId", archiveId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleArchive.class, url(DELETE), object.toString(), HttpMethods.Post));
    }

    /**
     * 修改组织档案（待审核和已审核）
     *
     * @param archiveId  档案id
     * @param title      档案标题
     * @param content    档案内容(html)
     * @param markdown   档案内容(markdown)
     * @param image      图片地址(json数组)
     * @param attach     附件地址(json数组)
     * @param attachName 附件名(json数组)
     */
    public void update(String archiveId, @NonNull String title, String content, String markdown,
                       ArrayList<String> image, ArrayList<String> attach, ArrayList<String> attachName) {
        //{_id,title,content,markdown,[image],[attach],[attachName]}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", archiveId)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("markdown", checkNull(markdown))
                    .put("image", new JSONArray(image))
                    .put("attach", new JSONArray(attach))
                    .put("attachName", new JSONArray(attachName));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleArchive.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查询单份组织档案
     */
    public void find(String archiveId) {
        httpRequest(getRequest(SingleArchive.class, format("%s?groDocId=%s", url(FIND), archiveId), "", HttpMethods.Get));
    }

    /**
     * 查询组织档案列表
     *
     * @param organizationId 组织id
     * @param summarySize    摘要字数限制
     * @param summaryRow     摘要行数限制
     * @param pageSize       页大小
     * @param pageNumber     页码
     */
    public void list(String organizationId, int summarySize, int summaryRow, int pageSize, int pageNumber) {
        //groupId,abstrSize,abstrRow,pageSize,pageNumber
        String param = format("?groupId=%s&abstrSize=%d&abstrRow=%d&pageSize=%d&pageNumber=%d",
                organizationId, summarySize, summaryRow, pageSize, pageNumber);
        httpRequest(getRequest(MultipleArchive.class, format("%s%s", url(LIST), param), "", HttpMethods.Get));
    }

    /**
     * 查询组织档案列表（待审核，默认按创建时间逆序排列）
     *
     * @param organizationId 组织id
     * @param pageSize       页大小
     * @param pageNumber     页码
     * @param type1          1.个人档案，2.组织档案，3.活动附件
     * @param type2          1.图片，2.视频,3.文件
     */
    public void listApproving(String organizationId, int pageSize, int pageNumber, int type1, int type2) {
        //groupId,pageSize,pageNumber,docType1,docType2
        list(APPROVING, organizationId, pageSize, pageNumber, type1, type2);
    }

    private void list(String action, String organizationId, int pageSize, int pageNumber, int type1, int type2) {
        String param = format("?groupId=%d&pageSize=%d&pageNumber=%d&docType1=%d&docType2=&d",
                organizationId, pageSize, pageNumber, type1, type2);
        httpRequest(getRequest(MultipleArchive.class, format("%s%s", url(action), param), "", HttpMethods.Get));
    }

    /**
     * 查询组织档案列表（已审核，默认按创建时间逆序排列,只显示当前用户授权范围内的记录）
     *
     * @param organizationId 组织id
     * @param pageSize       页大小
     * @param pageNumber     页码
     * @param type1          1.个人档案，2.组织档案，3.活动附件
     * @param type2          1.图片，2.视频,3.文件
     */
    public void listApproved(String organizationId, int pageSize, int pageNumber, int type1, int type2) {
        //groupId,pageSize,pageNumber,docType1,docType2
        list(APPROVED, organizationId, pageSize, pageNumber, type1, type2);
    }

    /**
     * 搜索组织档案
     *
     * @param organizationId 组织的id
     * @param info           档案的名称
     */
    public void search(String organizationId, String info) {
        //groupId,info
        httpRequest(getRequest(MultipleArchive.class, format("%s?groupId=%s&info=%s", url(SEARCH), organizationId, info), "", HttpMethods.Get));
    }

    /**
     * 批准档案入群
     *
     * @param archiveId 申请的id
     * @param message   附加消息
     */
    public void approve(String archiveId, String message) {
        //{id:"",msg:"",accessToken:""}
        approve("/group/appToBeDoc/approve", archiveId, message);
    }

    private void approve(String action, String archiveId, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", archiveId)
                    .put("msg", message)
                    .put("accessToken", Cache.cache().userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleArchive.class, action, object.toString(), HttpMethods.Post));
    }

    /**
     * 否决档案入群
     */
    public void reject(String archiveId, String message) {
        approve("/group/appToBeDoc/reject", archiveId, message);
    }
}
