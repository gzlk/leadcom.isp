package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.gzlk.android.isp.holder.organization.ContactViewHolder;

/**
 * <b>功能描述：</b>小组成员列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/22 08:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/22 08:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SquadContactFragment extends BaseOrganizationFragment {

    private static final String PARAM_NAME = "scf_name";

    public static SquadContactFragment newInstance(String params) {
        SquadContactFragment scf = new SquadContactFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 小组id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 小组名称
        bundle.putString(PARAM_NAME, strings[1]);
        scf.setArguments(bundle);
        return scf;
    }

    private String mName = "";
    private ContactAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mName = bundle.getString(PARAM_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mName);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(mName);
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
        }
    }

    private class ContactAdapter extends RecyclerSwipeAdapter<ContactViewHolder> {

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return 0;
        }
    }
}
