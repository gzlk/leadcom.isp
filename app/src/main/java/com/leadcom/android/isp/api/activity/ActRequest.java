package com.leadcom.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.Label;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Organization;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 23:41 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 23:41 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActRequest extends Request<Activity> {

    public static ActRequest request() {
        return new ActRequest();
    }

    private static class SingleActivity extends SingleQuery<Activity> {
    }

    private static class MultipleActivity extends PaginationQuery<Activity> {
    }

    private static final String END = "/end";
    private static final String IS_JOINED = "/joinPublicAct";

    @Override
    protected String url(String action) {
        return format("/activity%s", action);
    }

    @Override
    protected Class<Activity> getType() {
        return Activity.class;
    }

    @Override
    public ActRequest setOnSingleRequestListener(OnSingleRequestListener<Activity> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ActRequest setOnMultipleRequestListener(OnMultipleRequestListener<Activity> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    // 保存活动的group信息
    private Dao<Organization> orgDao = new Dao<>(Organization.class);
    // 附件保存dao
    private Dao<Attachment> attDao = new Dao<>(Attachment.class);

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
    protected void save(Activity activity) {
        if (null != activity) {
            orgDao.save(activity.getGroup());
            saveAttachment(activity.getAttachList(), activity.getId());
            if (null != activity.getActMember()) {
                activity.setActMemberId(activity.getActMember().getId());
                Member.save(activity.getActMember());
            }
        }
        super.save(activity);
    }

    @Override
    protected void save(List<Activity> list) {
        if (null != list && list.size() > 0) {
            for (Activity activity : list) {
                orgDao.save(activity.getGroup());
                saveAttachment(activity.getAttachList(), activity.getId());
                if (null != activity.getActMember()) {
                    activity.setActMemberId(activity.getActMember().getId());
                    Member.save(activity.getActMember());
                }
            }
        }
        super.save(list);
    }

    /**
     * 新增活动
     *
     * @param title       活动标题
     * @param intro       活动简介
     * @param authPublic  开放方式
     * @param site        活动地点
     * @param beginDate   活动开始时间
     * @param groupId     活动所属的组织id
     * @param cover       封面
     * @param labels      活动的标签
     * @param attachments 附件列表
     */
    public void add(@NonNull String title, String intro, int authPublic, String site,
                    String beginDate, @NonNull String groupId, String cover, ArrayList<String> labels, ArrayList<Attachment> attachments) {
        // {title,[label],groupId,beginDate,site,authPublic,cover,intro,[userIdList],[attachList]
        JSONObject object = new JSONObject();
        try {
            object.put("title", title)
                    .put("intro", checkNull(intro))
                    .put("authPublic", authPublic)
                    .put("groupId", groupId)
                    .put("cover", checkNull(cover))
                    .put("beginDate", beginDate)
                    .put("site", site)
                    .put("label", new JSONArray(labels))
                    .put("attachList", new JSONArray(Attachment.getJson(attachments)))
                    .put("fileIds", Attachment.getFieldIds(attachments));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivity.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 修改活动
     *
     * @param activityId  活动的id
     * @param title       活动标题
     * @param intro       活动描述
     * @param authPublic  开放方式
     * @param site        活动地点
     * @param beginDate   活动开始时间
     * @param cover       宣传图
     * @param labels      活动的标签
     * @param attachments 附件列表
     */
    public void update(@NonNull String activityId, @NonNull String title, String intro, int authPublic, String site,
                       String beginDate, String cover, ArrayList<String> labels, ArrayList<Attachment> attachments) {
        // {id,title,cover,intro}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", activityId)
                    .put("title", title)
                    .put("intro", checkNull(intro))
                    .put("authPublic", authPublic)
                    .put("cover", checkNull(cover))
                    .put("beginDate", beginDate)
                    .put("site", site)
                    .put("label", new JSONArray(labels))
                    .put("attUrlArray", new JSONArray(Attachment.getJson(attachments)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivity.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 更新活动中通知人员列表
     */
    public void update(String activityId, ArrayList<String> ids, ArrayList<String> names) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", activityId)
                    .put("userIdList", new JSONArray(ids));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivity.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public static final int TYPE_TITLE = 1;
    public static final int TYPE_COVER = 2;

    /**
     * 更活动的指定值
     */
    public void update(@NonNull String activityId, int updateType, String value) {

        JSONObject object = new JSONObject();
        try {
            object.put("_id", activityId);
            switch (updateType) {
                case TYPE_TITLE:
                    object.put("title", value);
                    break;
                case TYPE_COVER:
                    object.put("cover", checkNull(value));
                    break;
            }
//                    .put("content", checkNull(content))
//                    .put("openStatus", openStatus)
//                    .put("beginDate", beginDate)
//                    .put("site", address)
            //object.put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivity.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * (创建者或后台管理员)删除活动
     */
    public void delete(@NonNull String activityId) {
        // id=""
        httpRequest(getRequest(SingleActivity.class, format("%s?id=%s", url(DELETE), activityId), "", HttpMethods.Get));
    }

    /**
     * 退出指定活动
     */
    public void exit(@NonNull String activityId) {
        // id="",accessToken=""
        httpRequest(getRequest(SingleActivity.class, format("%s?id=%s", url(EXIT), activityId), "", HttpMethods.Get));
    }

    private void findInCache(String activityId) {
        Activity activity = dao.query(activityId);
        if (null == activity) {
            findFromRemote(activityId, ACT_OPE_MEMBERS);
        } else {
            fireOnSingleRequestListener(activity);
        }
    }

    public static final int ACT_OPE_NORMAL = 1;
    public static final int ACT_OPE_MEMBERS = 2;

    public void findFromRemote(String activityId, int ope) {
        httpRequest(getRequest(SingleActivity.class, format("%s?id=%s&ope=%d", url(FIND), activityId, ope), "", HttpMethods.Get));
    }

    /**
     * 查询单个活动
     *
     * @param activityId 活动的id
     */
    public void find(String activityId) {
        find(activityId, true);
    }

    /**
     * 通过tid反查
     */
    public void findByTid(String tid) {
        Activity act = Activity.getByTid(tid);
        if (null == act) {
            findTid(tid);
        } else {
            fireOnSingleRequestListener(act);
        }
    }

    /**
     * 通过网络异步查询tid
     */
    public void findTid(String tid) {
        httpRequest(getRequest(SingleActivity.class, format("%s/tid?tid=%s", url(FIND), tid), "", HttpMethods.Get));
    }

    /**
     * 查询单个活动
     *
     * @param activityId 活动的id
     * @param fromRemote true=强制从远程服务器上拉取
     */
    public void find(@NonNull String activityId, boolean fromRemote) {
        // id=""
        if (fromRemote) {
            findFromRemote(activityId, ACT_OPE_MEMBERS);
        } else {
            findInCache(activityId);
        }
    }

    private void loadingLocal(String groupId) {
        QueryBuilder<Activity> builder = new QueryBuilder<>(Activity.class)
                .whereEquals(Organization.Field.GroupId, groupId)
                .orderBy(Model.Field.CreateDate);
        List<Activity> list = dao.query(builder);
        if (null == list || list.size() < 1) {
            listFromRemote(groupId);
        } else {
            fireOnMultipleRequestListener(list, true, list.size(), 0);
        }
    }

    private void listFromRemote(String groupId) {
        String params = format("groupId=%s", groupId);
        httpRequest(getRequest(MultipleActivity.class, format("%s?%s", url(LIST), params), "", HttpMethods.Get));
    }

    /**
     * 列举所有我创建的活动列表
     */
    public static final int LIST_CREATED = 1;
    /**
     * 列举所有我参加过的活动列表
     */
    public static final int LIST_JOINED = 2;
    /**
     * 列举所有我参加且已结束的活动列表
     */
    public static final int LIST_ENDED = 3;

    /**
     * 查看某组织内的活动列表(只显示当前用户被授权范围内的记录)
     *
     * @param groupId    活动所属的组织ID
     * @param ope        操作类型(1.我发起的活动,2.我参与的活动,3.已结束的活动)
     * @param info       模糊搜索(活动标题)
     * @param pageNumber 页码
     */
    public void list(@NonNull String groupId, int ope, String info, int pageNumber, ArrayList<String> groupIds) {
        // groupId,ope,info,pageSize,pageNumber,[groupIds]
        String json = Utils.listToString(groupIds);
        String param = format("%s?groupId=%s&ope=%d&info=%s&pageNumber=%d&groupIds=%s", url(LIST), groupId, ope, info, pageNumber, json);
        httpRequest(getRequest(MultipleActivity.class, param, "", HttpMethods.Get));
    }

    /**
     * 查询活动首页（把查询待处理的活动邀请和查询我参加的活动合二为一）
     *
     * @param groupId    组织id
     * @param pageNumber 页码
     */
    public void listFront(@NonNull String groupId, int pageNumber, ArrayList<String> groupIds) {
        // groupId,pageSize,pageNumber,[groupIds]
        String json = Utils.listToString(groupIds);
        String param = format("%s/front?groupId=%s&pageNumber=%d&groupIds=%s", url(LIST), groupId, pageNumber, json);
        httpRequest(getRequest(MultipleActivity.class, param, "", HttpMethods.Get));
    }

    /**
     * 查询所有公开的活动
     */
    public void listPublic(int pageNumber) {
        String param = format("%s/public?groupId=%s&pageNumber=%d", url(LIST), pageNumber);
        httpRequest(getRequest(MultipleActivity.class, param, "", HttpMethods.Get));
    }

    /**
     * 结束活动
     */
    public void end(@NonNull String activityId) {
        String params = format("id=%s", activityId);
        httpRequest(getRequest(SingleActivity.class, format("%s?%s", url(END), params), "", HttpMethods.Get));
    }

    /**
     * 判断是否已经加入公共活动（首页推荐的）
     */
    public void isJoinPublicAct(String activityId) {
        String params = format("actId=%s", activityId);
        httpRequest(getRequest(SingleActivity.class, format("%s?%s", url(IS_JOINED), params), "", HttpMethods.Get));
    }

    /**
     * 查询所有公开的活动列表
     */
    public void allOpenActivities(int pageNumber) {
        httpRequest(getRequest(MultipleActivity.class, format("%s/allOpenActivities?pageNumber=%d", url(LIST), pageNumber), "", HttpMethods.Get));
    }
}
