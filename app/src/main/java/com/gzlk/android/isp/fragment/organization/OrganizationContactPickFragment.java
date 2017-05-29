package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.holder.ContactViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.organization.Member;

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

    public static OrganizationContactPickFragment newInstance(String params) {
        OrganizationContactPickFragment ocp = new OrganizationContactPickFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putString(PARAM_MEMBERS, strings[1].replace("@", ","));
        ocp.setArguments(bundle);
        return ocp;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        String json = bundle.getString(PARAM_MEMBERS, "[]");
        exists = Json.gson().fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_MEMBERS, Json.gson().toJson(exists));
    }

    private ContactAdapter mAdapter;
    private ArrayList<String> exists;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

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
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Member member = mAdapter.get(i);
            if (member.isSelected()) {
                if (!list.contains(member.getUserId())) {
                    list.add(member.getUserId());
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

    }

    @Override
    protected void onLoadingMore() {

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

    private boolean exists(String id) {
        return !(null == exists || exists.size() < 1) && exists.contains(id);
    }

    @Override
    protected void onLoadingLocalMembersComplete(String organizationId, String squadId, List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                member.setSelected(exists(member.getUserId()));
            }
            mAdapter.add(list, false);
        }
        fetchingRemoteMembers(organizationId, squadId);
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                member.setSelected(exists(member.getUserId()));
            }
            mAdapter.add(list, false);
            mAdapter.sort();
        }
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
