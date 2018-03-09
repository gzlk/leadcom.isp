package com.leadcom.android.isp.fragment.main;

import android.view.View;

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

    @ViewId(R.id.ui_main_home_top_channel_1_line)
    private View topLine1;
    @ViewId(R.id.ui_main_home_top_channel_2_line)
    private View topLine2;
    @ViewId(R.id.ui_main_home_top_channel_3_line)
    private View topLine3;

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
            mFragments.add(FeaturedFragment.newInstance(format("%d", FeaturedFragment.TYPE_ARCHIVE)));
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {

    }
}
