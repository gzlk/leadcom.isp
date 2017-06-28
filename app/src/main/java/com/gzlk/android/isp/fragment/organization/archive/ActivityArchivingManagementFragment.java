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
 * <b>功能描述：</b>活动的待存档文件管理页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/24 21:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/24 21:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityArchivingManagementFragment extends BaseViewPagerSupportFragment {

    private static final String PARAM_ARCHIVE_ID = "aamf_archive_id";
    private static boolean firstEnter = true;

    public static ActivityArchivingManagementFragment newInstance(String params) {
        ActivityArchivingManagementFragment af = new ActivityArchivingManagementFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 传过来的活动的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 文档的id
        bundle.putString(PARAM_ARCHIVE_ID, strings[1]);
        af.setArguments(bundle);
        firstEnter = true;
        return af;
    }

    private String mArchiveId;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mArchiveId = bundle.getString(PARAM_ARCHIVE_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_ARCHIVE_ID, mArchiveId);
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
        setCustomTitle(R.string.ui_archive_management_title_button_2);
        super.doingInResume();
        if (firstEnter) {
            firstEnter = false;
            setRightEvent(true);
        } else {
            setRightEvent(false);
        }
        //loadingArchive();
    }

    @Override
    protected void initializeFragments() {
        mFragments.add(ActivityArchivingListFragment.newInstance(format("%s,%d", mQueryId, ActivityArchivingListFragment.ALL)));
        mFragments.add(ActivityArchivingListFragment.newInstance(format("%s,%d", mQueryId, ActivityArchivingListFragment.ARCHIVES)));
        mFragments.add(ActivityArchivingListFragment.newInstance(format("%s,%d", mQueryId, ActivityArchivingListFragment.IMAGES)));
        mFragments.add(ActivityArchivingListFragment.newInstance(format("%s,%d", mQueryId, ActivityArchivingListFragment.VIDEOS)));
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
        for (int i = 0; i < mFragments.size(); i++) {
            mFragments.get(i).setViewPagerDisplayedCurrent(i == position);
        }
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

    private void setRightEvent(boolean handleable) {
        setRightText(handleable ? R.string.ui_base_text_edit : 0);
        setRightTitleClickListener(handleable ? new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 打开活动文档的存档页面
                openActivity(ActivityArchivingFragment.class.getName(), format("%s,%s", mQueryId, mArchiveId), true, false);
            }
        } : null);
    }

    // 拉取本档案的详情，按照审核状态显示是否需要编辑
//    private void loadingArchive() {
//        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
//            @Override
//            public void onResponse(Archive archive, boolean success, String message) {
//                super.onResponse(archive, success, message);
//                if (success && null != archive) {
//                    setRightEvent(archive.getStatus() <= Archive.ArchiveStatus.APPROVING);
//                }
//            }
//        }).find(Archive.Type.GROUP, mArchiveId, false);
//    }
}
