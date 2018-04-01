package com.leadcom.android.isp.fragment.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.ActRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.activity.Activity;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>活动详情页（本页包含3个子页：详情页、报名统计、其他统计）<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/30 15:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/30 15:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityDetailsMainFragment extends BaseViewPagerSupportFragment {

    public static ActivityDetailsMainFragment newInstance(String params) {
        ActivityDetailsMainFragment adf = new ActivityDetailsMainFragment();
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, params);
        adf.setArguments(bundle);
        return adf;
    }

    @ViewId(R.id.ui_ui_custom_title_left_text)
    private TextView leftText;
    @ViewId(R.id.ui_ui_custom_title_right_container)
    private LinearLayout rightContainer;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightText;
    @ViewId(R.id.ui_tool_view_activity_details_title_button1)
    private CorneredButton button1;
    @ViewId(R.id.ui_tool_view_activity_details_title_button2)
    private CorneredButton button2;
    @ViewId(R.id.ui_tool_view_activity_details_title_button3)
    private CorneredButton button3;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_management_main;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    public void doingInResume() {
        super.doingInResume();
        rightContainer.setVisibility(View.GONE);
        rightText.setText(R.string.ui_base_text_delete);
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() < 1) {
            mFragments.add(ActivityDetailsSingleFragment.newInstance(mQueryId));
            mFragments.add(ActivityDetailsStatisticsFragment.newInstance("0"));
            mFragments.add(ActivityDetailsStatisticsFragment.newInstance("1"));
            ((ActivityDetailsSingleFragment) mFragments.get(0)).manager = this;
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
    }

    @Click({R.id.ui_tool_view_activity_details_title_button1,
            R.id.ui_tool_view_activity_details_title_button2,
            R.id.ui_tool_view_activity_details_title_button3,
            R.id.ui_ui_custom_title_left_container,
            R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_ui_custom_title_right_container:
                warningDelete();
                break;
            case R.id.ui_tool_view_activity_details_title_button1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_view_activity_details_title_button2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_view_activity_details_title_button3:
                setDisplayPage(2);
                break;
        }
    }

    public void wannaDelete() {
        rightContainer.setVisibility(View.VISIBLE);
    }

    private void warningDelete() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_details_delete_warning, R.string.ui_base_text_yes, R.string.ui_base_text_think_again, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteActivity();
                return true;
            }
        }, null);
    }

    private void deleteActivity() {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    new Dao<>(Activity.class).delete(mQueryId);
                    ToastHelper.make().showMsg(R.string.ui_activity_details_deleted);
                    resultData(mQueryId);
                }
            }
        }).delete(mQueryId);
    }
}
