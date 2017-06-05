package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.InvitationRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.ContactViewHolder;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.model.organization.Member;

import java.util.List;

/**
 * <b>功能描述：</b>组织通讯录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/12 22:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/12 22:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationContactFragment extends BaseOrganizationFragment {

    public static OrganizationContactFragment newInstance(String params) {
        OrganizationContactFragment ocf = new OrganizationContactFragment();
        Bundle bundle = new Bundle();
        String[] strings = splitParameters(params);
        // 此时传入的是组织的id，需要显示此组织的成员列表
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 小组的id，要把组织的用户邀请入这个小组
        bundle.putString(PARAM_SQUAD_ID, strings[1]);
        ocf.setArguments(bundle);
        return ocf;
    }

    private ContactAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_squad_contact_menu_1);
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingRemoteMembers(mOrganizationId, mSquadId);
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ContactAdapter();
            mRecyclerView.setAdapter(mAdapter);
            // 查找本地该组织名下所有成员
            loadingLocalMembers(mOrganizationId, "");
        }
    }

    @Override
    protected void onLoadingLocalMembersComplete(String organizationId, String squadId, List<Member> list) {
        if (null != list && list.size() > 0) {
            mAdapter.add(list, false);
        }
        fetchingRemoteMembers(organizationId, squadId);
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            mAdapter.add(list, false);
            mAdapter.sort();
        }
        stopRefreshing();
    }

    private BaseViewHolder.OnHandlerBoundDataListener<Member> onHandlerBoundDataListener = new BaseViewHolder.OnHandlerBoundDataListener<Member>() {
        @Override
        public Member onHandlerBoundData(BaseViewHolder holder) {
            int index = holder.getAdapterPosition();
            addMemberToSquad(mAdapter.get(index), index);
            return null;
        }
    };

    private void addMemberToSquad(Member member, final int index) {
        // 将不在小组内的组织成员添加到小组
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    mAdapter.get(index).setSelected(true);
                    mAdapter.notifyItemChanged(index);
                    ToastHelper.make().showMsg(message);
                }
            }
        }).inviteToSquad(mSquadId, member.getUserId(), "");
    }

    private class ContactAdapter extends RecyclerViewAdapter<ContactViewHolder, Member> {

        @Override
        public ContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            ContactViewHolder holder = new ContactViewHolder(itemView, OrganizationContactFragment.this);
            holder.showButton(true);
            holder.setSquadId(mSquadId);
            holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            // 这里不需要滑动删除啥的了，所以省了一层layout
            return R.layout.tool_view_organization_contact;
        }

        @Override
        public void onBindHolderOfView(ContactViewHolder holder, int position, @Nullable Member item) {
            holder.showContent(item, "");
        }

        @Override
        protected int comparator(Member item1, Member item2) {
            return item1.getSpell().compareTo(item2.getSpell());
        }
    }
}
