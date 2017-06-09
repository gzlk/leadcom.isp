package com.gzlk.android.isp.fragment.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.gzlk.android.isp.holder.common.SearchableViewHolder;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>活动管理页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/30 10:16 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/30 10:16 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityManagementFragment extends BaseViewPagerSupportFragment {

    public static ActivityManagementFragment newInstance(String params) {
        ActivityManagementFragment amf = new ActivityManagementFragment();
        Bundle bundle = new Bundle();
        // 组织id
        bundle.putString(PARAM_QUERY_ID, params);
        amf.setArguments(bundle);
        return amf;
    }

    @ViewId(R.id.ui_ui_custom_title_left_text)
    private TextView leftText;
    @ViewId(R.id.ui_tool_view_activity_management_title_button1)
    private CorneredButton button1;
    @ViewId(R.id.ui_tool_view_activity_management_title_button2)
    private CorneredButton button2;
    @ViewId(R.id.ui_tool_view_activity_management_title_button3)
    private CorneredButton button3;
    @ViewId(R.id.ui_tool_view_activity_management_title_button4)
    private CorneredButton button4;

    private SearchableViewHolder searchableViewHolder;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_management_mine;
    }

    @Override
    public void doingInResume() {
        super.doingInResume();
        leftText.setText(null);
        initializeHolder();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() < 1) {
            // 已参加的
            String param = format("%s,%d", mQueryId, MyActivitiesFragment.TYPE_JOINED);
            mFragments.add(MyActivitiesFragment.newInstance(param));
            // 我创建的
            param = format("%s,%d", mQueryId, MyActivitiesFragment.TYPE_CREATED);
            mFragments.add(MyActivitiesFragment.newInstance(param));
            //param = format("%s,%d", mQueryId, MyActivitiesFragment.TYPE_NO_JOIN);
            //mFragments.add(MyActivitiesFragment.newInstance(param));
            // 未参加的
            mFragments.add(UnApprovedInviteFragment.newInstance(mQueryId));
            // 已结束的
            param = format("%s,%d", mQueryId, MyActivitiesFragment.TYPE_ENDED);
            mFragments.add(MyActivitiesFragment.newInstance(param));
        }
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

        button4.setNormalColor(position == 3 ? color1 : Color.WHITE);
        button4.setTextColor(position == 3 ? Color.WHITE : color2);

        for (int i = 0, len = mFragments.size(); i < len; i++) {
            BaseTransparentSupportFragment my = mFragments.get(i);
            my.setViewPagerDisplayedCurrent(position == i);
        }
    }

    @Click({R.id.ui_tool_view_activity_management_title_button1,
            R.id.ui_tool_view_activity_management_title_button2,
            R.id.ui_tool_view_activity_management_title_button3,
            R.id.ui_tool_view_activity_management_title_button4,
            R.id.ui_ui_custom_title_left_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_tool_view_activity_management_title_button1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_view_activity_management_title_button2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_view_activity_management_title_button3:
                setDisplayPage(2);
                break;
            case R.id.ui_tool_view_activity_management_title_button4:
                setDisplayPage(3);
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
            ((MyActivitiesFragment) mFragments.get(getDisplayedPage())).onSearching(text);
        }
    };
}
