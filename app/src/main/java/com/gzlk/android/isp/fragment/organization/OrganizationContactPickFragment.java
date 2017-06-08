package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.holder.organization.ContactViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.organization.Member;
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

    private static final String PARAM_MEMBERS = "ocpf_members";
    private static final String PARAM_SELECT_ALL = "ocpf_select_all";

    public static OrganizationContactPickFragment newInstance(String params) {
        OrganizationContactPickFragment ocp = new OrganizationContactPickFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 已选中的成员列表
        bundle.putString(PARAM_MEMBERS, replaceJson(strings[1], true));
        ocp.setArguments(bundle);
        return ocp;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isSelectAll = bundle.getBoolean(PARAM_SELECT_ALL, false);
        String json = bundle.getString(PARAM_MEMBERS, "[]");
        exists = Json.gson().fromJson(json, new TypeToken<ArrayList<Member>>() {
        }.getType());
        if (null == exists) {
            exists = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SELECT_ALL, isSelectAll);
        bundle.putString(PARAM_MEMBERS, Json.gson().toJson(exists));
    }

    // UI
    @ViewId(R.id.ui_tool_view_select_all_icon)
    private CustomTextView selectAllIcon;

    private boolean isSelectAll = false;
    private ContactAdapter mAdapter;
    private ArrayList<Member> exists;

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
        ArrayList<Member> list = new ArrayList<>();
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Member member = mAdapter.get(i);
            if (member.isSelected()) {
                if (!list.contains(member)) {
                    list.add(member);
                }
            }
        }
        resultData(Json.gson().toJson(list));
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
                member.setSelected(isSelectAll);
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
                member.setSelected(exists.contains(member));
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
            member.setSelected(!member.isSelected());
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
