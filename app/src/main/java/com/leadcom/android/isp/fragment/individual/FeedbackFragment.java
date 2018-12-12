package com.leadcom.android.isp.fragment.individual;

import android.view.View;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.common.AdviceRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.common.Advice;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>意见与反馈<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/14 14:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/14 14:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FeedbackFragment extends BaseTransparentSupportFragment {

    @ViewId(R.id.ui_feedback_content)
    private ClearEditText contentText;

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_feedback;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_feedback_fragment_title);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_feedback_commit})
    private void elementClick(View view) {
        String content = contentText.getValue();
        if (!isEmpty(content)) {
            String buildType = BuildConfig.BUILD_TYPE;
            String version = BuildConfig.VERSION_NAME;
            String internal = getString(R.string.app_internal_version);
            String api = getString(R.string.app_api_version);
            String additional = getString(R.string.ui_text_feedback_content_version, buildType, version, api, internal);
            commitAdvice(additional + content);
        } else {
            ToastHelper.helper().showMsg(R.string.ui_text_feedback_content_empty);
        }
    }

    private void commitAdvice(String content) {
        AdviceRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Advice>() {
            @Override
            public void onResponse(Advice advice, boolean success, String message) {
                super.onResponse(advice, success, message);
                if (success) {
                    finish();
                    ToastHelper.helper().showMsg(R.string.ui_text_feedback_committed);
                }
            }
        }).add(content);
    }
}
