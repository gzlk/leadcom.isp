package com.gzlk.android.isp.api.archive;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
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

    private static class SingleArchive extends SingleQuery<Archive> {
    }

    private static class MultipleArchive extends PaginationQuery<Archive> {
    }

//    private static class SpecialArchive extends Special<Archive> {
//    }

    private static final String USER = "/user/userDoc";
    private static final String GROUP = "/group/groDoc";

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

    // 附件保存dao
    private Dao<Attachment> attDao = new Dao<>(Attachment.class);

    private void saveAttachment(Archive archive) {
        saveAttachment(archive.getOffice(), archive.getId());
        saveAttachment(archive.getImage(), archive.getId());
        saveAttachment(archive.getVideo(), archive.getId());
        saveAttachment(archive.getAttach(), archive.getId());
    }

    private void saveAttachment(ArrayList<Attachment> list, String archiveId) {
        if (null != list && list.size() > 0) {
            for (Attachment attachment : list) {
                attachment.setArchiveId(archiveId);
                attachment.setType(Attachment.Type.ARCHIVE);
                attachment.resetInformation();
            }
            attDao.save(list);
        }
    }

    @Override
    protected void save(List<Archive> list) {
        if (null != list && list.size() > 0) {
            for (Archive archive : list) {
                archive.resetAdditional(archive.getAddition());
                saveAttachment(archive);
            }
        }
        super.save(list);
    }

    @Override
    protected void save(Archive archive) {
        if (null != archive) {
            archive.resetAdditional(archive.getAddition());
            saveAttachment(archive);
        }
        super.save(archive);
    }

    /**
     * 新增图文档案
     */
    public void add(String groupId, String title, int type, String content, String markdown, int authPublic) {
        // groupId,type,title,authPublic
        // {groupId,type,title,happenDate,label,[authUser],content,markdown,[office],[image],[video],[attach]},authPublic,intro,cover
        // {title,type,happenDate,authPublic,[label],content,markdown,[office],[image],[video],[attach],intro,cover,[authUser],[authGro]}
        boolean isIndividual = isEmpty(groupId);
        JSONObject object = new JSONObject();
        try {
            object.put("title", title)
                    .put("type", type)
                    .put("authPublic", authPublic)
                    .put("content", content)
                    .put("markdown", markdown);
            if (!isIndividual) {
                object.put("groupId", groupId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, isIndividual ? url(ADD) : group(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 新增个人档案
     *
     * @param cover        封面
     * @param title        档案标题
     * @param introduction 档案简介
     * @param authPublic   公开范围("0":私密,"1":公开)
     * @param happenDate   发生时间
     * @param labels       标签
     * @param office       文档({"name":"","url":"","pdf":""},{})
     * @param image        图片([{"name":"","url":""},{}])
     * @param video        视频([{"name":"","url":""},{}])
     * @param attach       附件地址([{"name":"","url":""},{}])
     */
    public void add(String cover, @NonNull String title, String introduction, int authPublic, String happenDate, ArrayList<String> labels,
                    ArrayList<Attachment> office, ArrayList<Attachment> image, ArrayList<Attachment> video, ArrayList<Attachment> attach) {
        // {title,happenDate,authPublic,tag,content,markdown,[office],[image],[video],[attach],accessToken}

        // 新建、更新档案时，手机端不再往服务器上传content/markdown字段(2017.05.31 00:04:43)
        JSONObject object = new JSONObject();
        try {
            object.put("title", title)
                    .put("cover", cover)
                    .put("type", 1)
                    .put("intro", checkNull(introduction))
                    .put("authPublic", authPublic)
                    .put("happenDate", happenDate)
                    .put("label", new JSONArray(labels))
                    .put("office", new JSONArray(Attachment.getJson(office)))
                    .put("image", new JSONArray(Attachment.getJson(image)))
                    .put("video", new JSONArray(Attachment.getJson(video)))
                    .put("attach", new JSONArray(Attachment.getJson(attach)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 新增组织档案（待审核）
     *
     * @param groupId      组织id
     * @param type         档案类型(1.普通,2.个人,3.活动){@link Archive.ArchiveType}
     * @param cover        封面
     * @param title        档案标题
     * @param introduction 档案简介
     * @param happenDate   发生时间
     * @param labels       档案标签(Json数组)
     * @param authUser     授权的指定用户ID(Json数组)
     * @param authPublic   授权范围(1.公开,2.组织内可见,3.指定个人可见)
     * @param office       文档({"name":"","url":"","pdf":""},{})
     * @param image        图片([{"name":"","url":""},{}])
     * @param video        视频([{"name":"","url":""},{}])
     * @param attach       附件地址([{"name":"","url":""},{}])
     */
    public void add(@NonNull String groupId, int type, String cover, @NonNull String title, String introduction, String happenDate,
                    ArrayList<String> labels, ArrayList<String> authUser, int authPublic,
                    ArrayList<Attachment> office, ArrayList<Attachment> image, ArrayList<Attachment> video, ArrayList<Attachment> attach) {
        // {groupId,type,title,happenDate,tag,[authUser],content,markdown,[office],[image],[video],[attach],accessToken}

        // 新建、更新档案时，手机端不再往服务器上传content/markdown字段(2017.05.31 00:04:43)
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("type", type)
                    .put("cover", cover)
                    .put("title", title)
                    .put("intro", checkNull(introduction))
                    .put("happenDate", happenDate)
                    .put("label", new JSONArray(labels))
                    .put("authUser", new JSONArray(authUser))
                    .put("authPublic", authPublic)
                    .put("office", new JSONArray(Attachment.getJson(office)))
                    .put("image", new JSONArray(Attachment.getJson(image)))
                    .put("video", new JSONArray(Attachment.getJson(video)))
                    .put("attach", new JSONArray(Attachment.getJson(attach)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, group(ADD), object.toString(), HttpMethods.Post));
    }

    private String getArchiveId(int type) {
        switch (type) {
            case Archive.Type.GROUP:
                return "groDocId";
            default:
                return "userDocId";
        }
    }

    /**
     * 删除档案
     *
     * @param type      档案类型{@link Archive.Type}
     * @param archiveId 档案id
     */
    public void delete(int type, @NonNull String archiveId) {
        String params = format("%s=%s", getArchiveId(type), archiveId);
        httpRequest(getRequest(SingleArchive.class, format("%s?%s", url(type, DELETE), params), "", HttpMethods.Get));
    }

    /**
     * 更新用户档案
     *
     * @param archiveId  档案id
     * @param cover      封面
     * @param title      档案标题
     * @param intro      档案内容(html)
     * @param labels     标签
     * @param authPublic 公开范围("0":私密,"1":公开)
     * @param happenDate 发生时间
     * @param office     文档({"name":"","url":"","pdf":""},{})
     * @param image      图片([{"name":"","url":""},{}])
     * @param video      视频([{"name":"","url":""},{}])
     * @param attach     附件地址([{"name":"","url":""},{}])
     */
    public void update(String archiveId, String cover, String title, String intro, int authPublic, String happenDate, ArrayList<String> labels,
                       ArrayList<Attachment> office, ArrayList<Attachment> image, ArrayList<Attachment> video, ArrayList<Attachment> attach) {
        // {_id,title,happenDate,authPublic,tag,content,markdown,[office],[image],[video],[attach],accessToken}

        // 新建、更新档案时，手机端不再往服务器上传content/markdown字段(2017.05.31 00:04:43)
        JSONObject object = new JSONObject();
        try {
            object.put("_id", archiveId)
                    .put("cover", cover)
                    .put("title", title)
                    .put("intro", checkNull(intro))
                    .put("authPublic", authPublic)
                    .put("happenDate", happenDate)
                    .put("label", new JSONArray(labels))
                    .put("office", new JSONArray(Attachment.getJson(office)))
                    .put("image", new JSONArray(Attachment.getJson(image)))
                    .put("video", new JSONArray(Attachment.getJson(video)))
                    .put("attach", new JSONArray(Attachment.getJson(attach)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, url(Archive.Type.USER, UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 更改组织档案
     *
     * @param archiveId  档案id
     * @param cover      封面
     * @param title      档案标题
     * @param intro      档案内容(html)
     * @param labels     标签
     * @param authUser   授权的指定用户ID(Json数组)
     * @param authPublic 公开范围("0":私密,"1":公开)
     * @param happenDate 发生时间
     * @param office     文档({"name":"","url":"","pdf":""},{})
     * @param image      图片([{"name":"","url":""},{}])
     * @param video      视频([{"name":"","url":""},{}])
     * @param attach     附件地址([{"name":"","url":""},{}])
     */
    public void update(String archiveId, String cover, @NonNull String title, String intro,
                       ArrayList<String> labels, ArrayList<String> authUser, int authPublic, String happenDate,
                       ArrayList<Attachment> office, ArrayList<Attachment> image, ArrayList<Attachment> video, ArrayList<Attachment> attach) {
        // {_id,title,happenDate,tag,[authUser],content,markdown,[office],[image],[video],[attach],accessToken}

        // 新建、更新档案时，手机端不再往服务器上传content/markdown字段(2017.05.31 00:04:43)
        JSONObject object = new JSONObject();
        try {
            object.put("_id", archiveId)
                    .put("cover", cover)
                    .put("title", title)
                    .put("intro", checkNull(intro))
                    .put("label", new JSONArray(labels))
                    .put("authUser", new JSONArray(authUser))
                    .put("authPublic", authPublic)
                    .put("happenDate", happenDate)
                    .put("office", new JSONArray(Attachment.getJson(office)))
                    .put("image", new JSONArray(Attachment.getJson(image)))
                    .put("video", new JSONArray(Attachment.getJson(video)))
                    .put("attach", new JSONArray(Attachment.getJson(attach)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleArchive.class, url(Archive.Type.GROUP, UPDATE), object.toString(), HttpMethods.Post));

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
        String params = format("%s=%s", getArchiveId(type), archiveId);
        httpRequest(getRequest(SingleArchive.class, format("%s?%s", url(type, FIND), params), "", HttpMethods.Get));
    }

    /**
     * 搜索个人档案
     *
     * @param info 档案的标题
     */
    public void search(String info) {
        httpRequest(getRequest(MultipleArchive.class, format("%s?info=%s", url(SEARCH), info), "", HttpMethods.Get));
    }

    /**
     * 搜索组织档案
     *
     * @param organizationId 组织的id
     * @param info           档案的名称
     */
    public void search(String organizationId, String info, int pageNumber) {
        //groupId,info
        httpRequest(getRequest(MultipleArchive.class, format("%s?groupId=%s&info=%s&pageNumber=%d", group(SEARCH), organizationId, info, pageNumber), "", HttpMethods.Get));
    }

    /**
     * 查询指定用户的个人档案列表(只显示当前用户授权范围内的记录)
     *
     * @param pageNumber 页码
     * @param userId     用户的id
     */
    public void list(int pageNumber, String userId) {
        // abstrSize,abstrRow,pageNumber,accessToken
        String param = format("%s&pageNumber=%d&userId=%s", SUMMARY, pageNumber, userId);
        httpRequest(getRequest(MultipleArchive.class, format("%s?%s", url(LIST), param), "", HttpMethods.Get));
    }

    /**
     * 查询组织档案列表
     *
     * @param organizationId 组织id
     * @param pageNumber     页码
     */
    public void list(String organizationId, int pageNumber) {
        //groupId,abstrSize,abstrRow,pageNumber
        String param = format("?%s&groupId=%s&pageNumber=%d", SUMMARY, organizationId, pageNumber);
        httpRequest(getRequest(MultipleArchive.class, format("%s%s", group(LIST), param), "", HttpMethods.Get));
    }

    /**
     * 批准档案入群
     *
     * @param requestId 申请的id
     * @param message   附加消息
     */
    public void approve(String requestId, String message) {
        //{id:"",msg:"",accessToken:""}
        approve("/group/appToBeDoc/approve", requestId, message);
    }

    private void approve(String action, String requestId, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", requestId)
                    .put("msg", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleArchive.class, action, object.toString(), HttpMethods.Post));
    }

    /**
     * 否决档案入群
     *
     * @param requestId 申请的id
     * @param message   附加消息
     */
    public void reject(String requestId, String message) {
        approve("/group/appToBeDoc/reject", requestId, message);
    }

    /**
     * 存档组织档案(活动结束后生成的档案)
     *
     * @param archiveId 档案id
     * @param status    审核状态(1.未审核[不要],2.通过,3.不通过[暂时不要]) {@link Archive.ArchiveStatus}
     */
    public void archive(String archiveId, int status) {
        // groDocArchiveId,status,accessToken
        String params = format("/group/groDocArchive/archive?groDocArchiveId=%s&status=%d", archiveId, status);
        httpRequest(getRequest(SingleArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 查找单个需存档的活动文档记录
     */
    public void archiveFind(String archiveId) {
        httpRequest(getRequest(SingleArchive.class, format("/group/groDocArchive/find?groDocArchiveId=%s", archiveId), "", HttpMethods.Get));
    }

    /**
     * 查询组织档案列表，此列表不保存到本地缓存
     */
    public void archiveList(String groupId, int pageNumber) {
        //directlySave = false;
        // groupId,pageSize,pageNumber
        String params = format("/group/groDocArchive/list?groupId=%s&pageNumber=%d", groupId, pageNumber);
        httpRequest(getRequest(MultipleArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 搜索组织档案(通过文件名模糊搜索)，此返回列表不保存到本地缓存
     */
    public void archiveSearch(String groupId, String fileName, int pageNumber) {
        //directlySave = false;
        String params = format("/group/groDocArchive/list?groupId=%s&pageNumber=%d&info=%s", groupId, pageNumber, fileName);
        httpRequest(getRequest(MultipleArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 审核组织档案
     *
     * @param archiveId 档案id
     * @param status    审核状态(1.未审核[不要],2.通过,3.不通过[暂时不要]) {@link Archive.ArchiveStatus}
     */
    public void approve(String archiveId, int status) {
        // groDocApproveId,status,accessToken
        String params = format("/group/groDocApprove/approve?groDocApproveId=%s&status=%d", archiveId, status);
        httpRequest(getRequest(SingleArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 查询单份未审核组织档案
     */
    public void approveFind(String archiveId) {
        // groDocApproveId
        String params = format("/group/groDocApprove/find?groDocApproveId=%s", archiveId);
        httpRequest(getRequest(SingleArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 查询未审核组织档案列表
     */
    public void approveList(String groupId, int pageNumber) {
        // groupId,pageSize,pageNumber
        String params = format("/group/groDocApprove/list?groupId=%s&pageNumber=%d", groupId, pageNumber);
        httpRequest(getRequest(MultipleArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 查询公开的组织档案列表
     */
    public void listPublic(int pageNumber) {
        // abstrSize,abstrRow,pageSize,pageNumber
        String params = format("/group/groDoc/listPublic?%s&pageNumber=%d", SUMMARY, pageNumber);
        httpRequest(getRequest(MultipleArchive.class, params, "", HttpMethods.Get));
    }
}
