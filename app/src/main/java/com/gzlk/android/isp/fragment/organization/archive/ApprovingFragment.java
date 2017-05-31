package com.gzlk.android.isp.fragment.organization.archive;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>待审核档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/24 21:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/24 21:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ApprovingFragment extends BaseViewPagerSupportFragment {

    public static ApprovingFragment newInstance(String params) {
        ApprovingFragment af = new ApprovingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        af.setArguments(bundle);
        return af;
    }

    @ViewId(R.id.ui_tool_view_archive_approving_title_1)
    private TextView titleText1;
    @ViewId(R.id.ui_tool_view_archive_approving_title_2)
    private TextView titleText2;
    @ViewId(R.id.ui_tool_view_archive_approving_title_3)
    private TextView titleText3;
    @ViewId(R.id.ui_tool_view_archive_approving_title_4)
    private TextView titleText4;

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_approving;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_archive_management_title_button_3);
        setRightText(R.string.ui_base_text_edit);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 打开组织档案的审核页面
                openActivity(ApproveFragment.class.getName(), mQueryId, true, false);
            }
        });
        super.doingInResume();
    }

    @Override
    protected void initializeFragments() {
        mFragments.add(ApprovableArchivesFragment.newInstance(format("%s,0", mQueryId)));
        mFragments.add(ApprovableArchivesFragment.newInstance(format("%s,1", mQueryId)));
        mFragments.add(ApprovableArchivesFragment.newInstance(format("%s,2", mQueryId)));
        mFragments.add(ApprovableArchivesFragment.newInstance(format("%s,3", mQueryId)));
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int text1 = getColor(R.color.textColor);
        int text2 = getColor(R.color.colorPrimary);

        titleText1.setTextColor(position == 0 ? text2 : text1);
        titleText1.setBackgroundColor(position == 0 ? Color.WHITE : Color.TRANSPARENT);

        titleText2.setTextColor(position == 1 ? text2 : text1);
        titleText2.setBackgroundColor(position == 1 ? Color.WHITE : Color.TRANSPARENT);

        titleText3.setTextColor(position == 2 ? text2 : text1);
        titleText3.setBackgroundColor(position == 2 ? Color.WHITE : Color.TRANSPARENT);

        titleText4.setTextColor(position == 3 ? text2 : text1);
        titleText4.setBackgroundColor(position == 3 ? Color.WHITE : Color.TRANSPARENT);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_view_archive_approving_title_1, R.id.ui_tool_view_archive_approving_title_2,
            R.id.ui_tool_view_archive_approving_title_3, R.id.ui_tool_view_archive_approving_title_4})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_archive_approving_title_1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_view_archive_approving_title_2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_view_archive_approving_title_3:
                setDisplayPage(2);
                break;
            case R.id.ui_tool_view_archive_approving_title_4:
                setDisplayPage(3);
                break;
        }
    }
}
