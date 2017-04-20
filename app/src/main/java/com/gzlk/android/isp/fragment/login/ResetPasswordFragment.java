package com.gzlk.android.isp.fragment.login;

import android.os.Bundle;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.system.Regist;
import com.gzlk.android.isp.api.system.ResetPwd;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.param.HttpRichParamModel;
import com.litesuits.http.response.Response;

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
    private ClearEditText passwordText;
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
            ToastHelper.make(Activity()).showMsg(R.string.ui_text_reset_password_params_error);
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
        String value = passwordText.getValue();
        if (StringHelper.isEmpty(value)) {
            ToastHelper.make().showMsg(R.string.ui_text_reset_password_input_hint);
            return;
        }
        finishButton.setEnabled(false);
        tryResetPassword(value);
    }

    private void tryResetPassword(String pwd) {
        httpRequest(requestPwd(pwd));
    }

    private JsonRequest<Regist> requestPwd(String pwd) {
        ResetPwd reset = new ResetPwd("", pwd, verifyPhone, verifyCode);

        String json = Json.gson(HttpRichParamModel.class).toJson(reset, new TypeToken<ResetPwd>() {
        }.getType());
        JsonRequest<Regist> request = new JsonRequest<>(reset, Regist.class);
        request.setHttpListener(new OnHttpListener<Regist>() {
            @Override
            public void onSucceed(Regist data, Response<Regist> response) {
                super.onSucceed(data, response);
                if (data.success()) {
                    ToastHelper.make().showMsg(R.string.ui_text_reset_password_success);
                    finishToSignIn();
                } else {
                    ToastHelper.make().showMsg(data.getMsg());
                    finishButton.setEnabled(true);
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                finishButton.setEnabled(true);
            }
        }).setHttpBody(new JsonBody(json));
        return request;
    }
}
