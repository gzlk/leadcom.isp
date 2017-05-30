package com.gzlk.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.archive.ArchiveSecurityViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.common.Seclusion;
import com.gzlk.android.isp.model.archive.Security;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>用户隐私设置<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/27 23:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/27 23:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserPrivacyFragment extends BaseSwipeRefreshSupportFragment {

    public static UserPrivacyFragment newInstance(String params) {
        UserPrivacyFragment upf = new UserPrivacyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        upf.setArguments(bundle);
        return upf;
    }

    private String[] items;
    private SecurityAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_security;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_security_fragment_title);
        setLeftIcon(0);
        setLeftText(R.string.ui_base_text_cancel);
        setRightIcon(0);
        setRightText(R.string.ui_base_text_finish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 完成隐私设置
                resultPrivacy();
            }
        });
        initializeAdapter();
    }

    private void resultPrivacy() {
        Seclusion seclusion = PrivacyFragment.getSeclusion(mQueryId);
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            Security security = mAdapter.get(i);
            if (security.isSelected()) {
                seclusion.setStatus(security.getIndex());
                break;
            }
        }
        resultData(PrivacyFragment.getSeclusion(seclusion));
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

    private ArrayList<Security> securities = new ArrayList<>();

    private void resetSecurityItems() {
        securities.clear();
        Seclusion seclusion = PrivacyFragment.getSeclusion(mQueryId);
        for (String string : items) {
            Security security = new Security(string);
            security.setSelected(security.getIndex() == seclusion.getStatus());
            securities.add(security);
        }
    }

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_security_setting_items);
            resetSecurityItems();
        }
        if (null == mAdapter) {
            mAdapter = new SecurityAdapter();
            mRecyclerView.setAdapter(mAdapter);
            resetStaticItems();
        }
    }

    private void resetStaticItems() {
        // 空列表
        for (Security security : securities) {
            if (security.isUserVisible()) {
                mAdapter.add(security);
            }
        }
    }

    private void resetSingleSelected(int index) {
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Security security = mAdapter.get(i);
            security.setSelected(security.getIndex() == index);
            security.setLocalDeleted(security.isSelected());
            mAdapter.notifyItemChanged(i);
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            resetSingleSelected(index);
        }
    };

    private class SecurityAdapter extends RecyclerViewAdapter<ArchiveSecurityViewHolder, Security> {

        @Override
        public ArchiveSecurityViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveSecurityViewHolder holder = new ArchiveSecurityViewHolder(itemView, UserPrivacyFragment.this);
            holder.addOnViewHolderClickListener(viewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_security_item;
        }

        @Override
        public void onBindHolderOfView(ArchiveSecurityViewHolder holder, int position, @Nullable Security item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Security item1, Security item2) {
            return 0;
        }
    }
}
