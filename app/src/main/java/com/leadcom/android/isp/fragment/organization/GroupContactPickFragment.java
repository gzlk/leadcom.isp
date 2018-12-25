package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.organization.ContactViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.SubMember;

import java.util.ArrayList;
import java.util.Iterator;
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

public class GroupContactPickFragment extends GroupBaseFragment {

    private static final String PARAM_SELECT_ALL = "ocpf_select_all";
    private static final String PARAM_FORCE_LOCK = "ocpf_force_to_lock";
    private static final String PARAM_SINGLE_PICK = "ocpf_single_pick";
    private static final String PARAM_IS_SINGLE_UI = "ocpf_is_single_UI";

    public static GroupContactPickFragment newInstance(Bundle bundle) {
        GroupContactPickFragment ocp = new GroupContactPickFragment();
        ocp.setArguments(bundle);
        return ocp;
    }

    public static Bundle getBundle(String groupId, boolean lockExist, boolean singlePick, boolean isSingle, ArrayList<SubMember> jsonExitMembers) {
        Bundle bundle = new Bundle();
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, groupId);
        // 是否锁定传过来的已存在项目
        bundle.putBoolean(PARAM_FORCE_LOCK, lockExist);
        // 是否单选
        bundle.putBoolean(PARAM_SINGLE_PICK, singlePick);
        // 已选中的成员列表
        bundle.putSerializable(PARAM_JSON, jsonExitMembers);
        // 是否独立打开的页面
        bundle.putBoolean(PARAM_IS_SINGLE_UI, isSingle);
        return bundle;
    }

    public static void open(BaseFragment fragment, String groupId, boolean lockExist, boolean singlePick, ArrayList<SubMember> jsonExitMembers) {
        open(fragment, REQUEST_MEMBER, groupId, lockExist, singlePick, jsonExitMembers);
    }

    public static void open(BaseFragment fragment, int request, String groupId, boolean lockExist, boolean singlePick, ArrayList<SubMember> jsonExitMembers) {
        Bundle bundle = getBundle(groupId, lockExist, singlePick, true, jsonExitMembers);
        fragment.openActivity(GroupContactPickFragment.class.getName(), bundle, request, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isSelectAll = bundle.getBoolean(PARAM_SELECT_ALL, false);
        isLockable = bundle.getBoolean(PARAM_FORCE_LOCK, false);
        isSinglePick = bundle.getBoolean(PARAM_SINGLE_PICK, false);
        isSingleUI = bundle.getBoolean(PARAM_IS_SINGLE_UI, true);
        //String json = bundle.getString(PARAM_JSON, "[]");
        existsUsers = (ArrayList<SubMember>) bundle.getSerializable(PARAM_JSON);
        if (null == existsUsers) {
            existsUsers = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SELECT_ALL, isSelectAll);
        bundle.putBoolean(PARAM_FORCE_LOCK, isLockable);
        bundle.putBoolean(PARAM_SINGLE_PICK, isSinglePick);
        bundle.putBoolean(PARAM_IS_SINGLE_UI, isSingleUI);
        bundle.putString(PARAM_JSON, Json.gson().toJson(existsUsers));
    }

    // UI
    @ViewId(R.id.ui_tool_view_select_all_root)
    private View selectAllRoot;
    @ViewId(R.id.ui_tool_view_select_all_title)
    private TextView selectAllTitle;
    @ViewId(R.id.ui_tool_view_select_all_icon)
    private CustomTextView selectAllIcon;

    private boolean isSelectAll = false, isLockable = false, isSinglePick = false, isSingleUI = true;
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
        selectAllRoot.setVisibility(isSinglePick ? View.GONE : View.VISIBLE);
        if (isSingleUI) {
            setCustomTitle(R.string.ui_organization_contact_picker_fragment_title);
            setRightText(R.string.ui_base_text_finish);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    resultMembers();
                }
            });
        } else {
            selectAllTitle.setText(Html.fromHtml(StringHelper.getString(R.string.ui_group_activity_editor_participator_select_all_3)));
        }
        initializeAdapter();
    }

    private void resultMembers() {
        resultData(SubMember.toJson(getSelectedItems()));
    }

    public ArrayList<SubMember> getSelectedItems() {
        ArrayList<SubMember> members = new ArrayList<>();
        if (null != mAdapter) {
            Iterator<Member> iterator = mAdapter.iterator();
            while (iterator.hasNext()) {
                Member member = iterator.next();
                if (member.isSelected()) {
                    members.add(new SubMember(member));
                }
            }
        }
        return members;
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
        fetchingMembers();
    }

    @Override
    protected void onLoadingMore() {
        displayLoading(true);
        fetchingMembers();
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
        Iterator<Member> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();
            // 如果是非全选，则需要判断已存在的记录，不要让其也一起取消选择
            if (!isSelectAll && member.isLocalDeleted() && isLockable) {
                member.setSelected(true);
            } else {
                member.setSelected(isSelectAll);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setLoadingText(R.string.ui_organization_contact_loading_text);
            mAdapter = new ContactAdapter();
            mAdapter.setOnDataHandingListener(handingListener);
            mRecyclerView.setAdapter(mAdapter);
            displayLoading(true);
            fetchingMembers();
        }
    }

    private RecyclerViewAdapter.OnDataHandingListener handingListener = new RecyclerViewAdapter.OnDataHandingListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onProgress(int currentPage, int maxPage, int maxCount) {

        }

        @Override
        public void onComplete() {
            displayLoading(false);
            stopRefreshing();
            displayNothing(mAdapter.getItemCount() < 0);
        }
    };

    private void fetchingMembers() {
        // 查找本地该组织名下所有成员
        if (isEmpty(mOrganizationId)) {
            // 新版我的通讯录选择器
            fetchingMyContacts();
        } else {
            fetchingRemoteMembers(mOrganizationId, "");
        }
    }

    private boolean isExist(SubMember member) {
        for (SubMember sub : existsUsers) {
            if (sub.getUserId().equals(member.getUserId()) || sub.getUserName().equals(member.getUserName()))
                return true;
        }
        return false;
    }

    private void fetchingMyContacts() {
        MemberRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Member>() {
            @Override
            public void onResponse(List<Member> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                remotePageNumber += count < pageSize ? 0 : 1;
                isLoadingComplete(count < pageSize);
                onFetchingRemoteMembersComplete(list);
            }
        }).listAllGroup();
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                SubMember sub = new SubMember();
                sub.setUserId(member.getUserId());
                member.setSelected(isExist(sub));
                if (member.isSelected() && isLockable) {
                    // 如果成员已经在之前的选定列表里，则做好防删除标记
                    member.setLocalDeleted(true);
                }
                member.setSelectable(true);
                if (!member.isSelected()) {
                    // 如果不在初始选中的列表里，则根据全选状态来设置选中与否
                    member.setSelected(isSelectAll);
                }
                //mAdapter.update(member);
            }
            //mAdapter.update(list, false);
            //mAdapter.sort();
        }
        mAdapter.setData(null == list ? new ArrayList<Member>() : list);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Member member = mAdapter.get(index);
            if (member.isSelected() && member.isLocalDeleted() && isLockable) {
                // 如果队员是选中状态，且是前页传过来的本来已存在的成员，且当前允许锁定，则提示不能取消对其的选择状态
                ToastHelper.helper().showMsg(R.string.ui_organization_contact_picker_cannot_unpick_exists);
            } else {
                member.setSelected(!member.isSelected());
            }
            mAdapter.notifyItemChanged(index);
            isSelectAll = isAllSelected();
            selectAllIcon.setTextColor(getColor(isSelectAll ? R.color.colorPrimary : R.color.textColorHintLight));
            if (isSinglePick && member.isSelected()) {
                unSelectOthers(member.getUserId());
            }
        }
    };

    private void unSelectOthers(String userId) {
        Iterator<Member> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();
            if (member.isSelected() && !member.getUserId().equals(userId)) {
                member.setSelected(false);
                mAdapter.update(member);
            }
        }
    }

    private boolean isAllSelected() {
        Iterator<Member> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();
            if (!member.isSelected()) {
                return false;
            }
        }
        return true;
    }

    private class ContactAdapter extends RecyclerViewAdapter<ContactViewHolder, Member> {

        @Override
        public ContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            ContactViewHolder holder = new ContactViewHolder(itemView, GroupContactPickFragment.this);
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
