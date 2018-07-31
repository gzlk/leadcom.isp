package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.holder.organization.ContactViewHolder;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.MemberNature;

import java.util.List;


/**
 * <b>功能描述：</b>根据成员属性id查询成员列表<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/30 13:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class NatureMemberListFragment extends BaseSwipeRefreshSupportFragment {

    public static NatureMemberListFragment newInstance(Bundle bundle) {
        NatureMemberListFragment nmlf = new NatureMemberListFragment();
        nmlf.setArguments(bundle);
        return nmlf;
    }

    public static void open(BaseFragment fragment, String groupId, MemberNature nature) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putSerializable(PARAM_JSON, nature);
        fragment.openActivity(NatureMemberListFragment.class.getName(), bundle, true, false);
    }

    private MemberNature mNature;
    private MemberAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mNature = (MemberNature) bundle.getSerializable(PARAM_JSON);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putSerializable(PARAM_JSON, mNature);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setNothingText(R.string.ui_group_member_nature_member_nothing);
        setCustomTitle(format("%s(%d)", mNature.getName(), mNature.getMemberNum()));
        isLoadingComplete(true);
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

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void loadingNatureUsers() {
        setLoadingText(R.string.ui_group_member_nature_member_loading);
        displayLoading(true);
        MemberRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Member>() {
            @Override
            public void onResponse(List<Member> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    mAdapter.update(list);
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 0);
                stopRefreshing();
            }
        }).listByMemberNature(mQueryId, mNature.getId(), mNature.getType());
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MemberAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingNatureUsers();
        }
    }

    private class MemberAdapter extends RecyclerViewAdapter<ContactViewHolder, Member> {

        @Override
        public ContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            return new ContactViewHolder(itemView, NatureMemberListFragment.this);
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.tool_view_organization_contact;
        }

        @Override
        public void onBindHolderOfView(ContactViewHolder holder, int position, @Nullable Member item) {
            holder.showContent(item, "");
        }

        @Override
        protected int comparator(Member item1, Member item2) {
            return 0;
        }
    }
}
