package com.gzlk.android.isp.fragment.organization.archive;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.gzlk.android.isp.holder.common.SearchableViewHolder;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>组织档案管理首页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/24 21:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/24 21:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ManagementFragment extends BaseViewPagerSupportFragment {

    public static ManagementFragment newInstance(String params) {
        ManagementFragment mf = new ManagementFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        mf.setArguments(bundle);
        return mf;
    }

    @ViewId(R.id.ui_tool_view_archive_management_title_button1)
    private CorneredButton button1;
    @ViewId(R.id.ui_tool_view_archive_management_title_button2)
    private CorneredButton button2;
    @ViewId(R.id.ui_tool_view_archive_management_title_button3)
    private CorneredButton button3;

    private SearchableViewHolder searchableViewHolder;

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_management;
    }

    @Override
    public void doingInResume() {
        super.doingInResume();
        initializeHolder();
    }

    @Override
    protected void initializeFragments() {
        // 组织的所有档案
        String param = format("%s,%d", mQueryId, ArchiveListFragment.TYPE_ALL);
        mFragments.add(ArchiveListFragment.newInstance(param));
        // 未存档组织档案
        param = format("%s,%d", mQueryId, ArchiveListFragment.TYPE_ARCHIVING);
        mFragments.add(ArchiveListFragment.newInstance(param));
        // 未审核组织档案
        param = format("%s,%d", mQueryId, ArchiveListFragment.TYPE_APPROVING);
        mFragments.add(ArchiveListFragment.newInstance(param));
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.colorPrimary);
        int color2 = getColor(R.color.textColorHintLight);

        button1.setNormalColor(position == 0 ? color1 : Color.WHITE);
        button1.setTextColor(position == 0 ? Color.WHITE : color2);

        button2.setNormalColor(position == 1 ? color1 : Color.WHITE);
        button2.setTextColor(position == 1 ? Color.WHITE : color2);

        button3.setNormalColor(position == 2 ? color1 : Color.WHITE);
        button3.setTextColor(position == 2 ? Color.WHITE : color2);

        for (int i = 0, len = mFragments.size(); i < len; i++) {
            ArchiveListFragment my = (ArchiveListFragment) mFragments.get(i);
            my.setViewPagerDisplayedCurrent(position == i);
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_view_archive_management_title_button1,
            R.id.ui_tool_view_archive_management_title_button2,
            R.id.ui_tool_view_archive_management_title_button3,
            R.id.ui_ui_custom_title_left_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_tool_view_archive_management_title_button1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_view_archive_management_title_button2:
                // 打开未存档页面
                setDisplayPage(1);
                break;
            case R.id.ui_tool_view_archive_management_title_button3:
                // 打开未审核页面
                setDisplayPage(2);
                //openActivity(ApprovingFragment.class.getName(), mQueryId, true, false);
                break;
        }
    }

    private void initializeHolder() {
        if (null == searchableViewHolder) {
            searchableViewHolder = new SearchableViewHolder(mRootView, this);
            searchableViewHolder.setOnSearchingListener(onSearchingListener);
        }
    }

    private SearchableViewHolder.OnSearchingListener onSearchingListener = new SearchableViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            ((ArchiveListFragment) mFragments.get(getDisplayedPage())).onSearching(text);
        }
    };
}
