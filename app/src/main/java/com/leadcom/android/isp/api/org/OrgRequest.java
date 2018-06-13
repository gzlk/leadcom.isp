package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Role;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * <b>功能描述：</b>组织相关api集合<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 08:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 08:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrgRequest extends Request<Organization> {

    public static OrgRequest request() {
        return new OrgRequest();
    }

    private static class SingleGroup extends SingleQuery<Organization> {
    }

    private static class MultipleGroup extends PaginationQuery<Organization> {
    }

    private static class BooleanGroup extends BoolQuery<Organization> {
    }

    private static final String ORG = "/group/group";
    private static final String SEARCH_ALL = "/searchAll";
    private static final String FIND_UPPER = "/findUpGroup";

    @Override
    protected String url(String action) {
        return ORG + action;
    }

    @Override
    protected Class<Organization> getType() {
        return Organization.class;
    }

    @Override
    public OrgRequest setOnSingleRequestListener(OnSingleRequestListener<Organization> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public OrgRequest setOnMultipleRequestListener(OnMultipleRequestListener<Organization> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    private Dao<Role> roleDao = new Dao<>(Role.class);
    private Dao<Organization> orgDao = new Dao<>(Organization.class);

    private boolean isFetchingJoinedGroups = false;

    private void saveMyGroupMember(Member member) {
        if (null != member) {
            if (null != member.getGroRole()) {
                Role role = member.getGroRole();
                member.setRoleId(role.getId());
                member.setRoleName(role.getRoleName());
                // 保存角色的权限列表
                role.savePermissionIds();
                roleDao.save(role);
            }
            Member.save(member);
        }
    }

    // 指定id的组织是否是我关注的组织
    private boolean isConcerned(String id) {
        if (isFetchingJoinedGroups) {
            return true;
        }
        Organization org = orgDao.query(id);
        return null != org && org.isConcerned();
    }

    @Override
    protected void save(Organization organization) {
        if (null != organization) {
            saveMyGroupMember(organization.getGroMember());
            //organization.setConcerned(isConcerned(organization.getId()));
        }
        super.save(organization);
    }

    @Override
    protected void save(List<Organization> list) {
        if (null != list && list.size() > 0) {
            for (Organization organization : list) {
                organization.setConcerned(isFetchingJoinedGroups);
                saveMyGroupMember(organization.getGroMember());
            }
        }
        super.save(list);
    }

    /**
     * 新增组织
     *
     * @param groupName    组织名称
     * @param groupLogo    组织图标
     * @param introduction 组织描述(简介)
     */
    public void add(String groupName, String groupLogo, String introduction) {
        //{name,logo,intro,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("name", groupName)
                    .put("logo", checkNull(groupLogo))
                    .put("intro", introduction);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(SingleGroup.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 更新组织的基本信息
     *
     * @param groupId      组织的id
     * @param groupName    组织名称
     * @param groupLogo    组织图标
     * @param introduction 组织描述(简介)
     */
    public void update(String groupId, String groupName, String groupLogo, String introduction) {
        //{_id,name,logo,intro,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", groupId);
            if (!isEmpty(groupLogo)) {
                object.put("logo", checkNull(groupLogo));
            }
            if (!isEmpty(groupName)) {
                // 更改组织名称
                object.put("name", checkNull(groupName));
                object.put("intro", checkNull(introduction));
            } else if (!isEmpty(introduction)) {
                object.put("intro", checkNull(introduction));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(BooleanGroup.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public static final int TYPE_NAME = 1;
    public static final int TYPE_LOGO = 2;
    public static final int TYPE_INTRO = 3;

    /**
     * 更新组织的指定属性
     */
    public void update(String groupId, int type, String value) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", groupId);
            switch (type) {
                case TYPE_INTRO:
                    object.put("intro", checkNull(value));
                    break;
                case TYPE_LOGO:
                    object.put("logo", checkNull(value));
                    break;
                case TYPE_NAME:
                    object.put("name", checkNull(value));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(BooleanGroup.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除组织
     */
    public void delete(String groupId) {
        executeHttpRequest(getRequest(BooleanGroup.class, format("%s?groupId=%s", url(DELETE), groupId), "", HttpMethods.Post));
    }

    /**
     * 查找组织的详细信息
     */
    public void find(String groupId) {
        executeHttpRequest(getRequest(SingleGroup.class, format("%s?groupId=%s", url(FIND), groupId), "", HttpMethods.Get));
    }

    /**
     * 加入的组织列表
     */
    public static final int GROUP_LIST_OPE_JOINED = 0;
    /**
     * 参加的活动相关连的组织列表
     */
    public static final int GROUP_LIST_OPE_ACTIVITY = 1;

    /**
     * 默认查询当前用户授权范围内的组织列表
     *
     * @param ope 0=返回当前用户参加的组织列表，1=返回活动里的组织列表（不一定是当前用户参加的）
     */
    public void list(int ope) {
        // accessToken,pageSize,pageNumber
        isFetchingJoinedGroups = ope == GROUP_LIST_OPE_JOINED;
        executeHttpRequest(getRequest(MultipleGroup.class, format("%s?ope=%d&pageNumber=1&pageSize=999", url(LIST), ope), "", HttpMethods.Get));
    }

    /**
     * 在所有组织中搜索组织名称
     */
    public void searchAll(String groupName, int pageNumber) {
        // info,pageSize,pageNumber
        executeHttpRequest(getRequest(MultipleGroup.class, format("%s?pageNumber=%d&info=%s", url(SEARCH_ALL), pageNumber, groupName), "", HttpMethods.Get));
    }

    /**
     * 在已参加组织中搜索组织名称
     */
    public void search(String groupName, int pageNumber) {
        // info,accessToken,pageSize,pageNumber
        executeHttpRequest(getRequest(MultipleGroup.class,
                format("%s?pageNumber=%d&info=%s", url(SEARCH), pageNumber, groupName),
                "", HttpMethods.Get));
    }

    /**
     * 查询上级组织，返回单个组织
     */
    public void findUpper(String groupId) {
        executeHttpRequest(getRequest(SingleGroup.class, format("%s?upId=%s", url(FIND_UPPER), groupId), "", HttpMethods.Get));
    }

    /**
     * 返回感兴趣的组织列表（不包括当前组织和被关注的组织）(2017-06-26 21:34新增)
     */
    public void listInteresting(String groupId, int pageNumber) {
        executeHttpRequest(getRequest(MultipleGroup.class, format("%s?groupId=%s&pageNumber=%d", url("/listInterest"), groupId, pageNumber), "", HttpMethods.Get));
    }

    /**
     * 返回关注的组织列表
     */
    public void listConcerned(String groupId, int pageNumber) {
        executeHttpRequest(getRequest(MultipleGroup.class, format("%s?groupId=%s&pageNumber=%d", url("/listInterest"), groupId, pageNumber), "", HttpMethods.Get));
    }

    /**
     * 关注为上级组织
     */
    public static final int CONCERN_UPPER = 1;
    /**
     * 关注为友好组织
     */
    public static final int CONCERN_FRIEND = 2;
    /**
     * 取消关注
     */
    public static final int CONCERN_CANCEL = 3;

    /**
     * 关注组织(2017-06-26 21:34新增)
     *
     * @param groupId        当前所在组织
     * @param concernGroupId 被关注的组织
     * @param type           关注类型（1.关注上级组织 2.关注友好组织 3.取消关注）
     */
    public void concern(String groupId, String concernGroupId, int type) {
        // groupId,conGroupId,type
        String url = url("/concern");
        String param = format("%s?groupId=%s&conGroupId=%s&type=%d", url, groupId, concernGroupId, type);
        executeHttpRequest(getRequest(SingleGroup.class, param, "", HttpMethods.Get));
    }
}
