package com.leadcom.android.isp.fragment.login;

import android.os.Bundle;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.common.SystemRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CleanableEditText;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>重置密码<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/11 19:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/11 19:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ResetPasswordFragment extends BaseVerifyFragment {

    public static ResetPasswordFragment newInstance(String params) {
        ResetPasswordFragment rpf = new ResetPasswordFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_VERIFY_TYPE, Integer.valueOf(strings[0]));
        bundle.putString(PARAM_VERIFY_PHONE, strings[1]);
        bundle.putString(PARAM_VERIFY_CODE, strings[2]);
        rpf.setArguments(bundle);
        return rpf;
    }

    @ViewId(R.id.ui_verify_reset_password_text)
    private CleanableEditText passwordText;
    @ViewId(R.id.ui_verify_reset_password_finish)
    private CorneredButton finishButton;

    @Override
    public int getLayout() {
        return R.layout.fragment_verify_reset_password;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_reset_password_fragment_title);
        setLeftText(R.string.ui_text_reset_password_fragment_title_left_text);
        if (verifyType != VT_PASSWORD) {
            ToastHelper.helper().showMsg(R.string.ui_text_reset_password_params_error);
            finish();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_verify_reset_password_finish})
    private void elementClick(View view) {
        String value = passwordText.getText().toString();
        if (!passwordText.verifyValue()) {
            value = "";
        }
        if (StringHelper.isEmpty(value)) {
            ToastHelper.helper().showMsg(R.string.ui_text_reset_password_input_hint);
            return;
        }
        finishButton.setEnabled(false);
        tryResetPassword(value);
    }

    private void tryResetPassword(String pwd) {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    ToastHelper.helper().showMsg(R.string.ui_text_reset_password_success);
                    finishToSignIn();
                } else {
                    finishButton.setEnabled(true);
                }
            }
        }).resetPassword(verifyPhone, pwd);
    }
}
