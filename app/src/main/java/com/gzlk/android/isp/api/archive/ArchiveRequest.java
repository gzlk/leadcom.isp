package com.gzlk.android.isp.api.archive;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.Special;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.archive.Archive;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>档案相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 09:37 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 09:37 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveRequest extends Request<Archive> {

    public static ArchiveRequest request() {
        return new ArchiveRequest();
    }

    private static class SingleArchive extends Output<Archive> {
    }

    private static class MultipleArchive extends Query<Archive> {
    }

    private static class SpecialArchive extends Special<Archive> {
    }

    private static final String USER = "/user/userDoc";
    private static final String GROUP = "/group/groDoc";

    private static final String APPROVING = "/toBeAppr" + LIST;
    private static final String APPROVED = "/approved" + LIST;

    @Override
    protected String url(String action) {
        return format("%s%s", USER, action);
    }

    @Override
    protected Class<Archive> getType() {
        return Archive.class;
    }

    private String group(String action) {
        return format("%s%s", GROUP, action);
    }

    private String url(int type, String action) {
        return type == Archive.Type.USER ? url(action) : group(action);
    }

    @Override
    public ArchiveRequest setOnSingleRequestListener(OnSingleRequestListener<Archive> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ArchiveRequest setOnMultipleRequestListener(OnMultipleRequestListener<Archive> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    @Override
    protected void save(List<Archive> list) {
        if (null != list && list.size() > 0) {
            for (Archive archive : list) {
                archive.resetAdditional(archive.getAddition());
            }
        }
        super.save(list);
    }

    @Override
    protected void save(Archive archive) {
        if (null != archive) {
            archive.resetAdditional(archive.getAddition());
        }
        super.save(archive);
    }

    /**
     * 新增个人档案
     *
     * @param title      档案标题
     * @param content    档案内容(html)
     * @param markdown   档案内容(markdown)
     * @param image      图片地址(json数组)
     * @param attach     附件地址(json数组)
     * @param attachName 附件名(json数组)
     */
    public void add(@NonNull String title, String content, String markdown,
                    ArrayList<String> image, ArrayList<String> attach, ArrayList<String> attachName) {
        // {title,content,markdown,[image],[attach],[attachName],userId,userName,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("title", title)
                    .put("content", checkNull(content))
                    .put("markdown", checkNull(markdown))
                    .put("image", new JSONArray(image))
                    .put("attach", new JSONArray(attach))
                    .put("attachName", new JSONArray(attachName))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 新增组织档案（待审核）
     *
     * @param groupId    组织id
     * @param type       档案类型(1.普通,2.个人,3.活动){@link Archive.ArchiveType}
     * @param title      档案标题
     * @param content    档案内容(html)
     * @param markdown   档案内容(markdown)
     * @param image      图片地址(json数组)
     * @param attach     附件地址(json数组)
     * @param attachName 附件名(json数组)
     */
    public void add(@NonNull String groupId, int type, @NonNull String title, String content, String markdown,
                    ArrayList<String> image, ArrayList<String> attach, ArrayList<String> attachName) {
        //{groupId,type,title,content,markdown,[image],[attach],[attachName],userId,userName,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("type", type)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("markdown", checkNull(markdown))
                    .put("image", new JSONArray(image))
                    .put("attach", new JSONArray(attach))
                    .put("attachName", new JSONArray(attachName))
                    .put("userId", Cache.cache().userId)
                    .put("userName", checkNull(Cache.cache().userName))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, group(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除档案
     *
     * @param type      档案类型{@link Archive.Type}
     * @param archiveId 档案id
     */
    public void delete(int type, @NonNull String archiveId) {
        String params = format("%s=%s", (type == Archive.Type.USER ? "userDocId" : "groDocId"), archiveId);
        httpRequest(getRequest(SingleArchive.class, format("%s?%s", url(type, DELETE), params), "", HttpMethods.Post));
    }

    /**
     * 更改档案的内容
     *
     * @param archiveId  档案id
     * @param type       档案类型{@link Archive.Type}
     * @param title      档案标题
     * @param content    档案内容(html)
     * @param markdown   档案内容(markdown)
     * @param image      图片地址(json数组)
     * @param attach     附件地址(json数组)
     * @param attachName 附件名(json数组)
     */
    public void update(String archiveId, int type, @NonNull String title, String content, String markdown,
                       ArrayList<String> image, ArrayList<String> attach, ArrayList<String> attachName) {
        // {_id,type,title,content,markdown,[image],[attach],[attachName],accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("_id", archiveId)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("markdown", checkNull(markdown))
                    .put("image", new JSONArray(image))
                    .put("attach", new JSONArray(attach))
                    .put("attachName", new JSONArray(attachName))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, url(type, UPDATE), object.toString(), HttpMethods.Post));

    }

    /**
     * 查询单份组织档案
     *
     * @param type      档案类型{@link Archive.Type}
     * @param archiveId 档案id
     */
    public void find(int type, @NonNull String archiveId, boolean fromLocal) {
        if (fromLocal) {
            findInCache(archiveId, type);
        } else {
            findFromRemote(archiveId, type);
        }
    }

    private void findInCache(String archiveId, int type) {
        Archive archive = dao.query(archiveId);
        if (null != archive) {
            fireOnSingleRequestListener(archive);
        } else {
            findFromRemote(archiveId, type);
        }
    }

    private void findFromRemote(String archiveId, int type) {
        // 调用网络数据
        String params = format("%s=%s", (type == Archive.Type.USER ? "userDocId" : "groDocId"), archiveId);
        httpRequest(getRequest(SingleArchive.class, format("%s?%s", url(type, FIND), params), "", HttpMethods.Get));
    }

    /**
     * 搜索个人档案
     *
     * @param info 档案的名称
     */
    public void search(String info) {
        httpRequest(getRequest(MultipleArchive.class, format("%s?info=%s&accessToken=%s", url(SEARCH), info, Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**
     * 搜索组织档案
     *
     * @param organizationId 组织的id
     * @param info           档案的名称
     */
    public void search(String organizationId, String info) {
        //groupId,info
        httpRequest(getRequest(MultipleArchive.class, format("%s?groupId=%s&info=%s", group(SEARCH), organizationId, info), "", HttpMethods.Get));
    }

    /**
     * 查询个人档案列表(只显示当前用户授权范围内的记录)
     */
    public void list(int pageNumber) {
        // abstrSize,abstrRow,pageSize,pageNumber,accessToken
        httpRequest(getRequest(SpecialArchive.class,
                format("%s?%s&pageSize=%d&pageNumber=%d&accessToken=%s",
                        url(LIST), SUMMARY, PAGE_SIZE, pageNumber, Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**
     * 查询组织档案列表
     *
     * @param organizationId 组织id
     * @param pageNumber     页码
     */
    public void list(String organizationId, int pageNumber) {
        //groupId,abstrSize,abstrRow,pageSize,pageNumber
        String param = format("?groupId=%s&%s&pageSize=%d&pageNumber=%d", organizationId, SUMMARY, PAGE_SIZE, pageNumber);
        httpRequest(getRequest(SpecialArchive.class, format("%s%s", group(LIST), param), "", HttpMethods.Get));
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
        httpRequest(getRequest(MultipleArchive.class, format("%s%s", group(action), param), "", HttpMethods.Get));
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
                    .put("accessToken", Cache.cache().accessToken);
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
