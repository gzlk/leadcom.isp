package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.organization.ContactViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.SubMember;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>组织成员拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/29 10:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/29 10:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationContactPickFragment extends BaseOrganizationFragment {

    private static final String PARAM_USER_IDS = "ocpf_members";
    private static final String PARAM_SELECT_ALL = "ocpf_select_all";
    private static final String PARAM_FORCE_LOCK = "ocpf_force_to_lock";

    public static OrganizationContactPickFragment newInstance(String params) {
        OrganizationContactPickFragment ocp = new OrganizationContactPickFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 是否锁定传过来的已存在项目
        bundle.putBoolean(PARAM_FORCE_LOCK, Boolean.valueOf(strings[1]));
        // 已选中的成员列表
        bundle.putString(PARAM_USER_IDS, replaceJson(strings[2], true));
        ocp.setArguments(bundle);
        return ocp;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isSelectAll = bundle.getBoolean(PARAM_SELECT_ALL, false);
        isLockable = bundle.getBoolean(PARAM_FORCE_LOCK, false);
        String json = bundle.getString(PARAM_USER_IDS, "[]");
        existsUsers = Json.gson().fromJson(json, new TypeToken<ArrayList<SubMember>>() {
        }.getType());
        if (null == existsUsers) {
            existsUsers = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SELECT_ALL, isSelectAll);
        bundle.putBoolean(PARAM_FORCE_LOCK, isLockable);
        bundle.putString(PARAM_USER_IDS, Json.gson().toJson(existsUsers));
    }

    // UI
    @ViewId(R.id.ui_tool_view_select_all_icon)
    private CustomTextView selectAllIcon;

    private boolean isSelectAll = false, isLockable = false;
    private ContactAdapter mAdapter;
    /**
     * 已选中的用户列表
     */
    private ArrayList<SubMember> existsUsers;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_organization_member;
    }

    @Override
    public void doingInResume() {
        enableSwipe(false);
        setSupportLoadingMore(false);
        setCustomTitle(R.string.ui_organization_contact_picker_fragment_title);
        setRightText(R.string.ui_base_text_finish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultMembers();
            }
        });
        initializeAdapter();
    }

    private void resultMembers() {
        // 返回选中的用户id和姓名
        ArrayList<SubMember> members = new ArrayList<>();
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Member member = mAdapter.get(i);
            if (member.isSelected()) {
                SubMember mbr = new SubMember();
                mbr.setUserId(member.getUserId());
                mbr.setUserName(member.getUserName());
                members.add(mbr);
            }
        }
        resultData(Json.gson().toJson(members, new TypeToken<ArrayList<SubMember>>() {
        }.getType()));
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
        remotePageNumber = 1;
        displayLoading(true);
        fetchingRemoteMembers(mOrganizationId, "");
    }

    @Override
    protected void onLoadingMore() {
        displayLoading(true);
        fetchingRemoteMembers(mOrganizationId, "");
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_tool_view_select_all_root})
    private void click(View view) {
        isSelectAll = !isSelectAll;
        selectAllIcon.setTextColor(getColor(isSelectAll ? R.color.colorPrimary : R.color.textColorHintLight));
        resetSelectAll();
    }

    private void resetSelectAll() {
        int size = mAdapter.getItemCount();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Member member = mAdapter.get(i);
                // 如果是非全选，则需要判断已存在的记录，不要让其也一起取消选择
                if (!isSelectAll && member.isLocalDeleted() && isLockable) {
                    member.setSelected(true);
                } else {
                    member.setSelected(isSelectAll);
                }
                mAdapter.update(member);
            }
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setLoadingText(R.string.ui_organization_contact_loading_text);
            mAdapter = new ContactAdapter();
            mRecyclerView.setAdapter(mAdapter);
            displayLoading(true);
            // 查找本地该组织名下所有成员
            fetchingRemoteMembers(mOrganizationId, "");
        }
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                SubMember sub = new SubMember();
                sub.setUserId(member.getUserId());
                member.setSelected(existsUsers.contains(sub));
                if (member.isSelected() && isLockable) {
                    // 如果成员已经在之前的选定列表里，则做好防删除标记
                    member.setLocalDeleted(true);
                }
                if (!member.isSelected()) {
                    // 如果不在初始选中的列表里，则根据全选状态来设置选中与否
                    member.setSelected(isSelectAll);
                }
            }
            mAdapter.update(list, false);
            mAdapter.sort();
        }
        displayLoading(false);
        stopRefreshing();
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Member member = mAdapter.get(index);
            if (member.isSelected() && member.isLocalDeleted() && isLockable) {
                // 如果队员是选中状态，且是前页传过来的本来已存在的成员，且当前允许锁定，则提示不能取消对其的选择状态
                ToastHelper.make().showMsg(R.string.ui_organization_contact_picker_cannot_unpick_exists);
            } else {
                member.setSelected(!member.isSelected());
            }
            mAdapter.notifyItemChanged(index);
        }
    };

    private class ContactAdapter extends RecyclerViewAdapter<ContactViewHolder, Member> {

        @Override
        public ContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            ContactViewHolder holder = new ContactViewHolder(itemView, OrganizationContactPickFragment.this);
            holder.showPicker(true);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
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
            return item1.getUserName().compareTo(item2.getUserName());
        }
    }
}
