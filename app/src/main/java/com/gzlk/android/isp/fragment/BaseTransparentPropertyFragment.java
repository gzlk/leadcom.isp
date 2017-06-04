package com.gzlk.android.isp.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>属性页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 16:20 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 16:20 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseTransparentPropertyFragment extends BaseSwipeRefreshSupportFragment {

    // View
    @ViewId(R.id.ui_transparent_title_container)
    public LinearLayout titleContainer;
    @ViewId(R.id.ui_ui_custom_title_text)
    public TextView titleTextView;
    @ViewId(R.id.ui_transparent_title_property_button)
    public CorneredButton bottomButton;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setSupportLoadingMore(false);
        tryPaddingContent(titleContainer, false);
        titleTextView.setText(null);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_transparent_title_property;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_ui_custom_title_left_container, R.id.ui_transparent_title_property_button})
    public void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_transparent_title_property_button:
                // 退出活动
                onBottomButtonClicked();
                break;
        }
    }

    protected abstract void onBottomButtonClicked();
}
