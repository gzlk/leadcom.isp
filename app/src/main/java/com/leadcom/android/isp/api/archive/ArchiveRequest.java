package com.leadcom.android.isp.api.archive;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchivePushTarget;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.SubMember;
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

    private static class ListArchive extends PageQuery<Archive> {
    }

    private static class BoolArchive extends BoolQuery<Archive> {
    }

//    private static class SpecialArchive extends Special<Archive> {
//    }

    private static final String USER = "/user/userDoc";
    private static final String GROUP = "/group/groDoc";
    private static final String DRAFT = "/docDraft";

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

    private String draft(String action) {
        return format("%s%s", DRAFT, action);
    }

    public String url(int type, String action) {
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
     * 更改公开范围
     */
    public static final int TYPE_AUTH = 1;
    /**
     * 更改标签
     */
    public static final int TYPE_LABEL = 2;
    /**
     * 更改封面图
     */
    public static final int TYPE_COVER = 3;
    /**
     * 更改档案来源
     */
    public static final int TYPE_SOURCE = 4;

    /**
     * 更新档案内容
     */
    public void update(Archive archive, int type) {
        boolean isIndividual = isEmpty(archive.getGroupId());
        JSONObject object = new JSONObject();
        try {
            object.put("_id", archive.getId());
            switch (type) {
                case TYPE_AUTH:
                    object.put("authPublic", archive.getAuthPublic());
                    break;
                case TYPE_LABEL:
                    object.put("label", new JSONArray(archive.getLabel()));
                    break;
                case TYPE_COVER:
                    object.put("cover", archive.getCover());
                    break;
                case TYPE_SOURCE:
                    object.put("source", archive.getSource());
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(SingleArchive.class, url(isIndividual ? Archive.Type.USER : Archive.Type.GROUP, UPDATE), object.toString(), HttpMethods.Post));
    }

    private String getArchiveId(int type) {
        switch (type) {
            case Archive.Type.GROUP:
                return "docId";
            default:
                return "docId";
        }
    }

    /**
     * 删除档案
     *
     * @param type      档案类型{@link Archive.Type}
     * @param archiveId 档案id
     */
    public void delete(int type, @NonNull String archiveId) {
        executeHttpRequest(getRequest(BoolArchive.class, url(type, DELETE), getDocId(archiveId).toString(), HttpMethods.Post));
    }

    /**
     * 查询单份组织档案
     *
     * @param archiveId 档案id
     */
    public void find(int type, @NonNull String archiveId) {
        executeHttpRequest(getRequest(SingleArchive.class, url(type, FIND), getDocId(archiveId).toString(), HttpMethods.Post));
    }

    /**
     * 查询分享出去的档案的详情
     *
     * @param archiveId   档案id/草稿id
     * @param archiveType 档案类型(1.个人档案,2.组织档案,3.个人档案草稿,4.组织档案草稿)
     */
    public void findShare(String archiveId, int archiveType) {
        String params = format("/system/share/findDoc?docId=%s&docType=%d", archiveId, archiveType);
        executeHttpRequest(getRequest(SingleArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 搜索个人档案
     *
     * @param info 档案的标题
     */
    public void search(String userId, int pageNumber, String info) {
        executeHttpRequest(getRequest(MultipleArchive.class, format("%s?userId=%s&pageNumber=%d&info=%s", url(SEARCH), userId, pageNumber, info), "", HttpMethods.Get));
    }

    /**
     * 搜索组织档案
     *
     * @param groupId     组织的id
     * @param searchTitle 档案标题
     * @param createDate  档案创建日期
     * @param classifyId  档案类别id
     * @param category    档案类型
     * @param pageNumber  页码
     */
    public void search(String groupId, String searchTitle, String createDate, String classifyId, String category, int pageNumber) {
        String params = format("%s?groupId=%s&pageNumber=%d", group(LIST), groupId, pageNumber);
        if (!isEmpty(createDate)) {
            params = format("%s&createDate=%s", params, createDate);
        }
        if (!isEmpty(classifyId)) {
            params = format("%s&docClassifyId=%s", params, classifyId);
        }
        if (!isEmpty(category)) {
            params = format("%s&category=%s", params, category);
        }
        if (!isEmpty(searchTitle)) {
            params = format("%s&title=%s", params, searchTitle);
        }
        executeHttpRequest(getRequest(ListArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 查询指定用户的个人档案列表(只显示当前用户授权范围内的记录)
     *
     * @param pageNumber 页码
     * @param userId     用户的id
     */
    public void list(int pageNumber, String userId) {
        String param = format("pageNumber=%d&userId=%s", pageNumber, userId);
        executeHttpRequest(getRequest(ListArchive.class, format("%s?%s", url(LIST), param), "", HttpMethods.Get));
    }

    /**
     * 查询组织档案列表
     *
     * @param organizationId 组织id
     * @param pageNumber     页码
     */
    public void list(String organizationId, int pageNumber, String searchTitle, String classifyId) {
        String param = format("?groupId=%s&pageNumber=%d", organizationId, pageNumber);
        if (!isEmpty(classifyId)) {
            param = format("%s&docClassifyId=%s", param, classifyId);
        }
        if (!isEmpty(searchTitle)) {
            param = format("%s&title=%s", param, searchTitle);
        }
        executeHttpRequest(getRequest(ListArchive.class, format("%s%s", group(LIST), param), "", HttpMethods.Get));
    }

    /**
     * 首页 - 头条列表
     */
    public void listHomeHeadline() {
        executeHttpRequest(getRequest(ListArchive.class, "/index/focusImage", "", HttpMethods.Get));
    }

    /**
     * 首页 - 推荐列表
     */
    public void listHomeRecommend(int pageNumber, String searchTitle) {
        String params = format("/index/recommend?pageNumber=%d%s", pageNumber,
                (isEmpty(searchTitle) ? "" : format("&title=%s", searchTitle)));
        executeHttpRequest(getRequest(ListArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 首页 - 关注的组织档案列表
     */
    public void listHomeFollowed(int pageNumber) {
        String params = format("/index/attention?pageNumber=%d", pageNumber);
        executeHttpRequest(getRequest(ListArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 推送组织档案
     */
    public void push(ArrayList<String> groupIdList, String archiveId) {
        if (null == groupIdList || groupIdList.size() < 1) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_details_push_no_group);
        } else {
            JSONObject object = getDocId(archiveId);
            try {
                object.put("groupIds", new JSONArray(groupIdList));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            executeHttpRequest(getRequest(BoolArchive.class, group(PUSH), object.toString(), HttpMethods.Post));
        }
    }

    public void push(String archiveId, ArrayList<ArchivePushTarget> targets) {
        if (null == targets || targets.size() < 1) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_details_push_no_group);
        } else {
            JSONObject object = getDocId(archiveId);
            try {
                object.put("targetGroups", new JSONArray(ArchivePushTarget.toJson(targets)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            executeHttpRequest(getRequest(BoolArchive.class, group(PUSH), object.toString(), HttpMethods.Post));
        }
    }

    /**
     * 档案分类
     */
    public void classify(String archiveId, String classifyId) {
        JSONObject object = getDocId(archiveId);
        try {
            object.put("docClassifyId", classifyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(BoolArchive.class, group("/classify"), object.toString(), HttpMethods.Post));
    }

    /**
     * 推荐档案到首页
     */
    public void recommend(String archiveId) {
        executeHttpRequest(getRequest(BoolArchive.class, group("/recommend/do"), getDocId(archiveId).toString(), HttpMethods.Post));
    }

    /**
     * 取消档案的首页推荐
     */
    public void unRecommend(String archiveId) {
        executeHttpRequest(getRequest(BoolArchive.class, group("/recommend/undo"), getDocId(archiveId).toString(), HttpMethods.Post));
    }

    private JSONObject getDocId(String archiveId) {
        JSONObject object = new JSONObject();
        try {
            object.put("docId", archiveId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 设置档案获奖
     */
    public void award(String archiveId) {
        executeHttpRequest(getRequest(BoolArchive.class, group("/award/do"), getDocId(archiveId).toString(), HttpMethods.Post));
    }

    /**
     * 设置档案获奖
     */
    public void unaward(String archiveId) {
        executeHttpRequest(getRequest(BoolArchive.class, group("/award/undo"), getDocId(archiveId).toString(), HttpMethods.Post));
    }

    /**
     * 回复档案
     */
    public void reply(String archiveId, String title, String content) {
        JSONObject object = getDocId(archiveId);
        try {
            object.put("title", title)
                    .put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleArchive.class, group("/reply"), object.toString(), HttpMethods.Post));
    }

    public void save(Archive archive, boolean isDraft, boolean shareDraft) {
        String content = archive.getContent();
        if (!isEmpty(content) && content.contains("<video ")) {
            content = content.replace("<video ", "<video style=\"width: 100%; max-width: 100%;\" ");
            archive.setContent(content);
        }
        boolean isIndividual = isEmpty(archive.getGroupId(), true);
        JSONObject object = new JSONObject();
        try {
            object.put("title", archive.getTitle())// 必要字段
                    .put("cover", checkNull(archive.getCover()))
                    .put("docType", archive.getDocType())// 必要字段
                    .put("authPublic", archive.getAuthPublic())// 必要字段
                    .put("content", archive.getContent())
                    .put("label", new JSONArray(archive.getLabel()))
                    .put("office", new JSONArray(Attachment.getJson(archive.getOffice())))
                    .put("image", new JSONArray(Attachment.getJson(archive.getImage())))
                    .put("video", new JSONArray(Attachment.getJson(archive.getVideo())))
                    .put("attach", new JSONArray(Attachment.getJson(archive.getAttach())))
                    .put("source", archive.getSource());
            if (archive.getDocType() == Archive.ArchiveType.TEMPLATE) {
                // 模板档案需要增加以下字段
                object.put("topic", archive.getTopic())
                        .put("resolution", archive.getResolution())
                        .put("branch", archive.getBranch());
            }
            if (!isIndividual) {
                object.put("groupId", archive.getGroupId())// 必要字段
                        .put("groupName", archive.getGroupName())
                        // 组织档案需要增加以下参数
                        .put("happenDate", archive.getHappenDate())
                        .put("site", checkNull(archive.getSite()))
                        .put("property", checkNull(archive.getProperty()))
                        .put("category", checkNull(archive.getCategory()))
                        .put("docClassifyId", checkNull(archive.getDocClassifyId()))
                        .put("participant", checkNull(archive.getParticipant()))
                        .put("participantIdList", new JSONArray(archive.getParticipantIdList()));
                //if (archive.isTemplateArchive()) {
                //    object.put("happenDate", archive.getHappenDate());
                //}
            }
            if (shareDraft) {
                // 如果是分享草稿，则需要添加这个字段
                object.put("shareUserIds", new JSONArray(archive.getShareUserIds()));
            }
            if (!isEmpty(archive.getId())) {
                object.put("_id", archive.getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String param = format("/archive/save?formalType=%d&ownType=%d", (shareDraft ? 1 : isDraft ? 0 : 2), archive.getOwnType());
        executeHttpRequest(getRequest(SingleArchive.class, param, object.toString(), HttpMethods.Post));
    }

    /**
     * 查询组织档案列表，此列表不保存到本地缓存
     */
    public void listDraft(int pageNumber) {
        String params = format("%s?pageNumber=%d&pageSize=99", draft(SELECT), pageNumber);
        executeHttpRequest(getRequest(ListArchive.class, params, "", HttpMethods.Get));
    }

    /**
     * 删除草稿
     */
    public void deleteDraft(String draftId) {
        JSONObject object = new JSONObject();
        try {
            object.put("docId", draftId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(BoolArchive.class, draft(DELETE), object.toString(), HttpMethods.Post));
    }

    /**
     * 拉取指定某人的履职记录
     *
     * @param ope        1.档案,2.活动
     * @param groupId    组织id
     * @param userId     用户id
     * @param createYear 年份:0.全部,1.今年,2.去年
     * @param classifyId 档案性质
     * @param category   档案类型
     */
    public void listMemberDuty(int ope, String groupId, String userId, int createYear, String classifyId, String category) {
        String url = format("/user/appUserRecord/list?groupId=%s&userId=%s&ope=%d", groupId, userId, ope);
        url = format("%s&createDate=%d", url, createYear);
        if (!isEmpty(classifyId)) {
            url = format("%s&docClassifyId=%s", url, classifyId);
        }
        if (!isEmpty(category)) {
            url = format("%s&category=%s", url, category);
        }
        executeHttpRequest(getRequest(ListArchive.class, url, "", HttpMethods.Get));
    }

    /**
     * 创建一个活动
     */
    public void createActivity(Archive archive) {
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", archive.getGroupId())
                    .put("title", archive.getTitle())
                    .put("happenDate", archive.getHappenDate())
                    .put("site", archive.getSite())
                    .put("participator", archive.getParticipator())
                    .put("recorder", checkNull(archive.getRecorder()))
                    .put("topic", checkNull(archive.getTopic()))
                    .put("content", checkNull(archive.getContent()))
                    .put("groupIdList", new JSONArray(archive.getGroupIdList()))
                    .put("groSquMemberList", new JSONArray(SubMember.toJson(archive.getGroSquMemberList(), new String[]{"userName", "type"})));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleArchive.class, "/group/groActivity/add", object.toString(), HttpMethods.Post));
    }

    /**
     * 拉取组织的活动列表
     */
    public void listActivities(String groupId, int pageNumber) {
        String url = format("/group/groActivityNotice%s?groupId=%s&pageNumber=%d", LIST, groupId, pageNumber);
        executeHttpRequest(getRequest(ListArchive.class, url, "", HttpMethods.Get));
    }
}
