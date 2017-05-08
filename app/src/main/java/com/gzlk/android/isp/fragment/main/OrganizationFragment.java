package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.gzlk.android.isp.fragment.organization.ArchiveFragment;
import com.gzlk.android.isp.fragment.organization.LivenessFragment;
import com.gzlk.android.isp.fragment.organization.MemberFragment;
import com.gzlk.android.isp.fragment.organization.StructureFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>主页 - 组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 10:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 10:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationFragment extends BaseViewPagerSupportFragment {

    @ViewId(R.id.ui_organization_top_channel_layout)
    private View channelLayout;
    @ViewId(R.id.ui_tool_organization_top_channel_1)
    private TextView channel1;
    @ViewId(R.id.ui_tool_organization_top_channel_2)
    private TextView channel2;
    @ViewId(R.id.ui_tool_organization_top_channel_3)
    private TextView channel3;
    @ViewId(R.id.ui_tool_organization_top_channel_4)
    private TextView channel4;

    @Override
    public int getLayout() {
        return R.layout.fragment_main_organization;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        tryPaddingContent(channelLayout, true);
        super.doingInResume();
    }

    @Override
    protected void initializeFragments() {
        mFragments.add(new StructureFragment());
        mFragments.add(new MemberFragment());
        mFragments.add(new ArchiveFragment());
        mFragments.add(new LivenessFragment());
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.textColorHintDark);
        int color2 = getColor(R.color.colorPrimary);

        channel1.setTextColor(position == 0 ? color2 : color1);
        channel2.setTextColor(position == 1 ? color2 : color1);
        channel3.setTextColor(position == 2 ? color2 : color1);
        channel4.setTextColor(position == 3 ? color2 : color1);
    }

    @Click({R.id.ui_tool_organization_top_channel_1, R.id.ui_tool_organization_top_channel_2,
            R.id.ui_tool_organization_top_channel_3, R.id.ui_tool_organization_top_channel_4})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_organization_top_channel_1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_organization_top_channel_2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_organization_top_channel_3:
                setDisplayPage(2);
                break;
            case R.id.ui_tool_organization_top_channel_4:
                setDisplayPage(3);
                break;
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

    @Override
    protected void destroyView() {

    }
}
