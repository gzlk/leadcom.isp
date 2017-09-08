package com.gzlk.android.isp.fragment.organization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.OrgRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.common.BaseTransparentPropertyFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BasePopupInputSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.organization.SimpleMemberViewHolder;
import com.gzlk.android.isp.holder.individual.UserHeaderBigViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.ToggleableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.common.SimpleClickableItem;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;

import java.util.List;

/**
 * <b>功能描述：</b>机构详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 07:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 07:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationPropertiesFragment extends BaseTransparentPropertyFragment {

    public static OrganizationPropertiesFragment newInstance(String params) {
        OrganizationPropertiesFragment odf = new OrganizationPropertiesFragment();
        Bundle bundle = new Bundle();
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, params);
        odf.setArguments(bundle);
        return odf;
    }

    private DetailsAdapter mAdapter;
    private String[] items;

    @Override
    public void doingInResume() {
        super.doingInResume();
        bottomButton.setVisibility(View.GONE);
        initializeAdapter();
    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingRemoteOrganization();
    }

    @Override
    protected void onLoadingMore() {
        super.onLoadingMore();
    }

    @Override
    protected void onBottomButtonClicked() {

    }

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_organization_details_items);
        }
        if (null == mAdapter) {
            mAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingOrganization();
        }
    }

    private void initializeOrg(Organization org) {
        int index = 0;
        for (String string : items) {
            if (string.startsWith("0|")) {
                mAdapter.update(org);
                index++;
                continue;
            }
            String text;
            switch (index) {
                case 1:
                    // 活动成员
                    List<Member> members = Member.getMembersOfGroupOrSquad(mQueryId, "");
                    int size = null == members ? 0 : members.size();
                    text = format(string, size);
                    break;
                case 2:
                    // 活动标题
                    text = format(string, org.getName());
                    break;
                default:
                    text = string;
                    break;
            }
            SimpleClickableItem item = new SimpleClickableItem(text);
            mAdapter.update(item);
            index++;
        }
    }

    private void fetchingOrganization() {
        Organization org = new Dao<>(Organization.class).query(mQueryId);
        if (null == org) {
            fetchingRemoteOrganization();
        } else {
            initializeOrg(org);
        }
    }

    private void fetchingRemoteOrganization() {
        OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                if (null == organization) {
                    closeWithWarning(R.string.ui_organization_details_not_exists);
                } else {
                    initializeOrg(organization);
                }
                stopRefreshing();
                isLoadingComplete(true);
            }
        }).find(mQueryId);
    }

    private static final int REQUEST_NAME = ACTIVITY_BASE_REQUEST + 10;
    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 1:
                    // 组织成员列表
                    break;
                case 2:
                    // 创建者是当前登录的用户时，可以 修改群名称
                    Organization org = (Organization) mAdapter.get(0);
                    if (null != org && org.getCreatorId().equals(Cache.cache().userId)) {
                        String name = StringHelper.isEmpty(org.getName()) ? "" : org.getName();
                        openActivity(BasePopupInputSupportFragment.class.getName(),
                                StringHelper.getString(R.string.ui_popup_input_name, name), REQUEST_NAME, true, false);
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_NAME) {
            String result = getResultedData(data);
            tryEditOrgInfo(OrgRequest.TYPE_NAME, result);
        }
        super.onActivityResult(requestCode, data);
    }

    private void tryEditOrgInfo(int type, String value) {
        OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                if (success) {
                    if (null != organization) {
                        initializeOrg(organization);
                    } else {
                        fetchingRemoteOrganization();
                    }
                }
            }
        }).update(mQueryId, type, value);
    }

    private class DetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_MEMBER = 1, VT_TOGGLE = 2, VT_NORMAL = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = OrganizationPropertiesFragment.this;
            switch (viewType) {
                case VT_HEADER:
                    UserHeaderBigViewHolder uhbvh = new UserHeaderBigViewHolder(itemView, fragment);
                    uhbvh.addOnViewHolderClickListener(viewHolderClickListener);
                    return uhbvh;
                case VT_MEMBER:
                    SimpleMemberViewHolder holder = new SimpleMemberViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(viewHolderClickListener);
                    return holder;
                case VT_TOGGLE:
                    return new ToggleableViewHolder(itemView, fragment);
                default:
                    SimpleClickableViewHolder scvh = new SimpleClickableViewHolder(itemView, fragment);
                    scvh.addOnViewHolderClickListener(viewHolderClickListener);
                    return scvh;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.holder_view_individual_header_big;
                case VT_MEMBER:
                    return R.layout.holder_view_organization_simple_member;
                case VT_TOGGLE:
                    return R.layout.holder_view_toggle;
                default:
                    return R.layout.holder_view_simple_clickable;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return VT_HEADER;
                case 1:
                    return VT_MEMBER;
                case 5:
                case 6:
                    return VT_TOGGLE;
                default:
                    return VT_NORMAL;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof SimpleMemberViewHolder) {
                ((SimpleMemberViewHolder) holder).showContent((SimpleClickableItem) item);
                ((SimpleMemberViewHolder) holder).showContent((Organization) mAdapter.get(0));
            } else if (holder instanceof SimpleClickableViewHolder) {
                ((SimpleClickableViewHolder) holder).showContent(item);
            } else if (holder instanceof ToggleableViewHolder) {
                ((ToggleableViewHolder) holder).showContent((SimpleClickableItem) item);
            } else if (holder instanceof UserHeaderBigViewHolder) {
                ((UserHeaderBigViewHolder) holder).showContent((Organization) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
