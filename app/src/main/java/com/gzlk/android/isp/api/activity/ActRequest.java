package com.gzlk.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.Label;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Organization;
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

    private static class SingleActivity extends Output<Activity> {
    }

    private static class MultipleActivity extends Query<Activity> {
    }

    private static final String JOINED = "/list/joined";
    private static final String CREATED = "/list/created";
    private static final String ENDED = "/list/ended";
    private static final String REFRESH = "/list/refresh";
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
    // 标签dao
    private Dao<Label> labelDao = new Dao<>(Label.class);

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

    private void saveLabels(ArrayList<Label> list) {
        if (null != list && list.size() > 0) {
            labelDao.save(list);
        }
    }

    @Override
    protected void save(Activity activity) {
        if (null != activity) {
            orgDao.save(activity.getGroup());
            saveAttachment(activity.getAttUrlArray(), activity.getId());
        }
        super.save(activity);
    }

    @Override
    protected void save(List<Activity> list) {
        if (null != list && list.size() > 0) {
            for (Activity activity : list) {
                orgDao.save(activity.getGroup());
                saveAttachment(activity.getAttUrlArray(), activity.getId());
            }
        }
        super.save(list);
    }

    /**
     * 新增活动
     *
     * @param title       活动标题
     * @param content     活动描述
     * @param openStatus  开放方式
     * @param address     活动地点
     * @param beginDate   活动开始时间
     * @param groupId     活动所属的组织id
     * @param logo        宣传图
     * @param labels      活动的标签
     * @param attachments 附件列表
     */
    public void add(@NonNull String title, String content, int openStatus, String address,
                    String beginDate, @NonNull String groupId, String logo,ArrayList<String> labels, ArrayList<Attachment> attachments) {
        // {title:"",[label]:"",content:"",openStatus："",groupId:"",accessToken:"",memberIdArray:"",beginDate:"",site:"",attUrlArray:""}
        JSONObject object = new JSONObject();
        try {
            object.put("title", title)
                    .put("content", checkNull(content))
                    .put("openStatus", openStatus)
                    .put("groupId", groupId)
                    .put("img", checkNull(logo))
                    .put("beginDate", beginDate)
                    .put("site", address)
                    //.put("accessToken", Cache.cache().accessToken)
                    //.put("memberIdArray", new JSONArray(members))
                    //.put("memberNameArray", new JSONArray(names))
                    .put("label", new JSONArray(labels))
                    .put("attUrlArray", new JSONArray(Attachment.getJson(attachments)));
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
     * @param content     活动描述
     * @param openStatus  开放方式
     * @param address     活动地点
     * @param beginDate   活动开始时间
     * @param logo        宣传图
     * @param labels      活动的标签
     * @param attachments 附件列表
     */
    public void update(@NonNull String activityId, @NonNull String title, String content, int openStatus, String address,
                       String beginDate, String logo, ArrayList<String> labels, ArrayList<Attachment> attachments) {
        // {id:"",title:"",content:"",accessToken:"",memberIdArray:""}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", activityId)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("openStatus", openStatus)
                    .put("img", checkNull(logo))
                    .put("beginDate", beginDate)
                    .put("site", address)
                    //.put("accessToken", Cache.cache().accessToken)
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
                    //.put("accessToken", Cache.cache().accessToken)
                    .put("memberIdArray", new JSONArray(ids))
                    .put("memberNameArray", new JSONArray(names));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivity.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public static final int TYPE_TITLE = 1;
    public static final int TYPE_IMAGE = 2;

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
                case TYPE_IMAGE:
                    object.put("img", checkNull(value));
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
            findFromRemote(activityId);
        } else {
            fireOnSingleRequestListener(activity);
        }
    }

    private void findFromRemote(String activityId) {
        httpRequest(getRequest(SingleActivity.class, format("%s?id=%s", url(FIND), activityId), "", HttpMethods.Get));
    }

    /**
     * 查询单个活动
     *
     * @param activityId 活动的id
     */
    public void find(String activityId) {
        find(activityId, false);
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
            findFromRemote(activityId);
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
     * 查看某组织内的活动列表(只显示当前用户被授权范围内的记录)
     *
     * @param groupId 组织id
     */
    public void list(String groupId) {
        list(groupId, false);
    }

    /**
     * 查看某组织内的活动列表(只显示当前用户被授权范围内的记录)
     *
     * @param groupId    组织id
     * @param fromRemote true=强制从远程服务器上拉取
     */
    public void list(@NonNull String groupId, boolean fromRemote) {
        // groupId="",accessToken=""
        if (fromRemote) {
            listFromRemote(groupId);
        } else {
            loadingLocal(groupId);
        }
    }

    /**
     * 查询我参加的活动(非实时的缓存数据)
     */
    public void joined(@NonNull String groupId) {
        String param = format("%s?groupId=%s", url(JOINED), groupId);
        httpRequest(getRequest(MultipleActivity.class, param, "", HttpMethods.Get));
    }

    private void loadingCreated(String groupId) {
        QueryBuilder<Activity> builder = new QueryBuilder<>(Activity.class)
                .whereEquals(Organization.Field.GroupId, groupId)
                .whereAppendAnd()
                .whereEquals(Archive.Field.CreatorId, Cache.cache().userId)
                .orderBy(Model.Field.CreateDate);
        List<Activity> temp = dao.query(builder);
        fireOnMultipleRequestListener(temp, true, null == temp ? 0 : temp.size(), 1);
    }

    /**
     * 查询我发起的活动(非实时的缓存数据)
     */
    public void created(@NonNull String groupId, boolean fromRemote) {
        if (fromRemote) {
            String param = format("%s?groupId=%s", url(CREATED), groupId);
            httpRequest(getRequest(MultipleActivity.class, param, "", HttpMethods.Get));
        } else {
            loadingCreated(groupId);
        }
    }

    /**
     * 查询我参与的已结束的活动
     */
    public void ended(@NonNull String groupId) {
        String param = format("%s?groupId=%s", url(ENDED), groupId);
        httpRequest(getRequest(MultipleActivity.class, param, "", HttpMethods.Get));
    }

    /**
     * 刷新我参加的和我发起的活动(手机端在同意加入活动时调用)
     */
    @Deprecated
    public void refresh() {
        httpRequest(getRequest(MultipleActivity.class, format("%s", url(REFRESH)), "", HttpMethods.Get));
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
}
