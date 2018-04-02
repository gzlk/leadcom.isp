package com.leadcom.android.isp.fragment.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;

/**
 * <b>功能描述：</b>主页Home<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/09 15:20 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/09 15:20 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HomeFragment extends BaseViewPagerSupportFragment {

    @ViewId(R.id.ui_main_tool_bar_container)
    private View toolBar;
    @ViewId(R.id.ui_main_home_top_channel_1_line)
    private View topLine1;
    @ViewId(R.id.ui_main_home_top_channel_2_line)
    private View topLine2;
    @ViewId(R.id.ui_main_home_top_channel_3_line)
    private View topLine3;
    @ViewId(R.id.ui_main_home_top_channel_1)
    private TextView channel1;
    @ViewId(R.id.ui_main_home_top_channel_2)
    private TextView channel2;
    @ViewId(R.id.ui_main_home_top_channel_3)
    private TextView channel3;
    @ViewId(R.id.ui_main_tool_bar_line)
    private View toolbarLine;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tryPaddingContent(toolBar, false);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_home;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() < 1) {
            // 推荐
            mFragments.add(HomeFeaturedFragment.newInstance(format("%d", HomeFeaturedFragment.TYPE_ARCHIVE)));
            // 关注
            mFragments.add(IndividualFragment.newInstance(String.valueOf(IndividualFragment.TYPE_ARCHIVE_HOME)));
            // 动态
            mFragments.add(IndividualFragment.newInstance(String.valueOf(IndividualFragment.TYPE_MOMENT)));
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        toolbarLine.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        int color1 = getColor(R.color.textColor), color2 = getColor(R.color.textColorHint);
        topLine1.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        channel1.setTextColor(position == 0 ? color1 : color2);

        topLine2.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);
        channel2.setTextColor(position == 1 ? color1 : color2);

        topLine3.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
        channel3.setTextColor(position == 2 ? color1 : color2);
    }

    @Click({R.id.ui_main_home_top_channel_1, R.id.ui_main_home_top_channel_2, R.id.ui_main_home_top_channel_3})
    private void topClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_home_top_channel_1:
                setDisplayPage(0);
                break;
            case R.id.ui_main_home_top_channel_2:
                setDisplayPage(1);
                break;
            case R.id.ui_main_home_top_channel_3:
                setDisplayPage(2);
                break;
        }
    }
}
