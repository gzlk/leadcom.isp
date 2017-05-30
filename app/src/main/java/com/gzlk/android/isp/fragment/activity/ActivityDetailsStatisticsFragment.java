package com.gzlk.android.isp.fragment.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>活动详情：统计<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/30 18:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/30 18:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityDetailsStatisticsFragment extends BaseTransparentSupportFragment {

    public static ActivityDetailsStatisticsFragment newInstance(String params) {
        ActivityDetailsStatisticsFragment adsf = new ActivityDetailsStatisticsFragment();
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, params);
        adsf.setArguments(bundle);
        return adsf;
    }

    @ViewId(R.id.text_test)
    private TextView textView;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_details_statistics;
    }

    @Override
    public void doingInResume() {
        textView.setText(mQueryId.equals("0") ? R.string.ui_activity_details_title_button_text_2 : R.string.ui_activity_details_title_button_text_3);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }
}
