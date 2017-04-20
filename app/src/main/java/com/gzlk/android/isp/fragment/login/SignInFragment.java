package com.gzlk.android.isp.fragment.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.system.SignIn;
import com.gzlk.android.isp.api.system.Regist;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseDelayRefreshSupportFragment;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.litesuits.http.data.TypeToken;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpRichParamModel;
import com.litesuits.http.response.Response;

/**
 * <b>功能描述：</b>登录页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/10 20:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/10 20:57 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignInFragment extends BaseDelayRefreshSupportFragment {

    private static final String PARAM_STILL_SIGN_IN = "sif_still_in_sign_in";
    @ViewId(R.id.ui_sign_in_account)
    private ClearEditText accountText;
    @ViewId(R.id.ui_sign_in_password)
    private ClearEditText passwordText;
    @ViewId(R.id.ui_sign_in_to_sign_in)
    private CorneredButton signInButton;

    private boolean stillInSignIn = false;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        stillInSignIn = bundle.getBoolean(PARAM_STILL_SIGN_IN, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_STILL_SIGN_IN, stillInSignIn);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_sign_in;
    }

//    private boolean isScreenOn() {
//        PowerManager pm = (PowerManager) Activity().getSystemService(Context.POWER_SERVICE);
//        return Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn();
//    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void doingInResume() {
        if (null != App.app().Me() && isAdded()) {
            accountText.setValue(App.app().Me().getPhone());
            signInButton.setEnabled(false);
            signInButton.setText(R.string.ui_text_sign_in_still_processing);
            delayRefreshLoading(5000, DELAY_TYPE_TIME_DELAY);
        }
        //log(String.format("screen on: %s, visible: %s, resumed: %s", isScreenOn(), isVisible(), isResumed()));
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_sign_in_to_sign_up, R.id.ui_sign_in_to_retrieve_password,
            R.id.ui_sign_in_to_sign_in})
    private void click(View view) {
        int id = view.getId();
        if (id == R.id.ui_sign_in_to_sign_in) {
            // 登录
            if (TextUtils.isEmpty(accountText.getValue())) {
                ToastHelper.make(Activity()).showMsg(R.string.ui_text_sign_in_account_value_incorrect);
            } else if (TextUtils.isEmpty(passwordText.getValue())) {
                ToastHelper.make(Activity()).showMsg(R.string.ui_text_sign_in_password_value_incorrect);
            } else {
                if (!stillInSignIn) {
                    stillInSignIn = true;
                    signInButton.setEnabled(false);
                    // 开始登录
                    httpRequest(loginParams(accountText.getValue(), passwordText.getValue()));
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_sign_in_still_processing);
                }
            }
        } else {
            String params = String.valueOf(id == R.id.ui_sign_in_to_sign_up ? PhoneVerifyFragment.VT_SIGN_UP : PhoneVerifyFragment.VT_PASSWORD);
            openActivity(PhoneVerifyFragment.class.getName(), params, true, true);
        }
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {
        if (type == DELAY_TYPE_TIME_DELAY) {
            finish(true);
        }
    }

    private JsonRequest<Regist> loginParams(String account, String password) {
        SignIn param = new SignIn(account, password, "");
        String json = Json.gson(HttpRichParamModel.class).toJson(param, new TypeToken<SignIn>() {
        }.getType());
        JsonRequest<Regist> login = new JsonRequest<>(param, Regist.class);
        login.setHttpListener(new OnHttpListener<Regist>() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSucceed(Regist data, Response<Regist> response) {
                super.onSucceed(data, response);
                ToastHelper.make().showMsg(data.getMsg());
                // 检测服务器返回的状态
                if (data.success()) {
                    new Dao<>(User.class).save(data.getData());
                    PreferenceHelper.save(R.string.pf_last_login_user_id, data.getData().getId());
                    // 这里尝试访问一下全局me以便及时更新已登录的用户的信息
                    App.app().Me();
                    // 打开主页面
                    finish(true);
                } else {
                    signInButton.setEnabled(true);
                    ToastHelper.make().showMsg(data.getMsg());
                }
            }
            @Override
            public void onFailed() {
                super.onFailed();
                // 失败后可以重新登陆
                signInButton.setEnabled(true);
            }
            @Override
            public void onEnd(Response<Regist> response) {
                super.onEnd(response);
                stillInSignIn = false;
            }
        }).setHttpBody(new JsonBody(json), HttpMethods.Post);
        return login;
    }
}
