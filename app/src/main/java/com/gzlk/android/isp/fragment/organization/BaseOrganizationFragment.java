package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;

import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.MemberRequest;
import com.gzlk.android.isp.api.org.OrgRequest;
import com.gzlk.android.isp.api.org.SquadRequest;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.organization.Squad;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>组织相关fragment的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/14 16:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/14 16:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseOrganizationFragment extends BaseSwipeRefreshSupportFragment {

    protected static final String PARAM_SQUAD_ID = "bof_squad_id";
    protected String mOrganizationId, mSquadId;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mOrganizationId = mQueryId;
        mSquadId = bundle.getString(PARAM_SQUAD_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_SQUAD_ID, mSquadId);
    }

    /**
     * 查询当前用户加入的组织列表
     */
    protected void fetchingJoinedRemoteOrganizations() {
        OrgRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Organization>() {
            @Override
            public void onResponse(List<Organization> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                onFetchingJoinedRemoteOrganizationsComplete(list);
            }
        }).list();
    }

    /**
     * 查询我加入的组织列表返回了
     */
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
    }

    /**
     * 查询组织的详细信息
     */
    protected void fetchingRemoteOrganization(final String organizationId) {
        if (StringHelper.isEmpty(organizationId)) return;
        OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                onFetchingRemoteOrganizationComplete(organization);
            }
        }).find(organizationId);
    }

    /**
     * 查询组织的详细信息返回了
     */
    protected void onFetchingRemoteOrganizationComplete(Organization organization) {
    }

    /**
     * 查询指定组织下属的小组列表
     */
    protected void fetchingRemoteSquads(String organizationId) {
        SquadRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Squad>() {
            @Override
            public void onResponse(List<Squad> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                onFetchingRemoteSquadsComplete(list);
            }
        }).list(organizationId);
    }

    /**
     * 查询组织的小组列表返回了
     */
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
    }

    /**
     * 添加小组到指定的组织
     */
    protected void addNewSquadToOrganization(String orgId, String squadName, final String squadIntroduction) {
        SquadRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Squad>() {
            @Override
            public void onResponse(Squad squad, boolean success, String message) {
                super.onResponse(squad, success, message);
                onAddNewSquadToOrganizationComplete(squad);
            }
        }).add(orgId, squadName, squadIntroduction);
    }

    /**
     * 查添加小组返回了
     */
    protected void onAddNewSquadToOrganizationComplete(Squad squad) {
    }

    /**
     * 查询指定的小组详情
     */
    protected void fetchingRemoteSquad(final String squadId) {
        if (StringHelper.isEmpty(squadId)) return;
        SquadRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Squad>() {
            @Override
            public void onResponse(Squad squad, boolean success, String message) {
                super.onResponse(squad, success, message);
                onFetchingRemoteSquadComplete(squad);
            }
        }).find(squadId);
    }

    /**
     * 查询小组详情返回了
     */
    protected void onFetchingRemoteSquadComplete(Squad squad) {
    }

    /**
     * 查询指定组织或小组的成员列表。查询小组时，必须要指定组织id
     */
    protected void fetchingRemoteMembers(String organizationId, String squadId) {
        MemberRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Member>() {
            @Override
            public void onResponse(List<Member> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                onFetchingRemoteMembersComplete(list);
            }
        }).list(organizationId, squadId, remotePageNumber);
    }

    /**
     * 查询组织或小组的成员表返回了
     */
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
    }

    private QueryBuilder<Member> memberBuilder(String organizationId, String squadId) {
        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Organization.Field.GroupId, organizationId);
        if (StringHelper.isEmpty(squadId)) {
            query = query.whereAppendAnd().whereAppend(Organization.Field.SquadId + " IS NULL");
        } else {
            query = query.whereAppendAnd().whereEquals(Organization.Field.SquadId, squadId);
        }
        return query;
    }

    /**
     * 判断用户id是否为组织成员或小组成员
     */
    protected boolean isMember(String userId, String organizationId, String squadId) {

        if (StringHelper.isEmpty(userId)) return false;

        QueryBuilder<Member> query = memberBuilder(organizationId, squadId)
                .whereAppendAnd().whereEquals(Model.Field.UserId, userId);
        List<Member> members = new Dao<>(Member.class).query(query);
        return null != members && members.size() > 0;
    }

    /**
     * 查询本地成员列表，按照成员名字排序
     */
    protected void loadingLocalMembers(String organizationId, String squadId) {
        QueryBuilder<Member> query = memberBuilder(organizationId, squadId).orderBy(Model.Field.UserName);
        onLoadingLocalMembersComplete(organizationId, squadId, new Dao<>(Member.class).query(query));
    }

    /**
     * 查询本地成员返回了
     */
    protected void onLoadingLocalMembersComplete(String organizationId, String squadId, List<Member> list) {
    }
}
