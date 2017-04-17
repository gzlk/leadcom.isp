package com.gzlk.android.isp.fragment.login;

import android.text.TextUtils;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.system.LoginParam;
import com.gzlk.android.isp.api.system.Regist;
import com.gzlk.android.isp.fragment.base.BaseDelayRefreshSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
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

    @ViewId(R.id.ui_sign_in_account)
    private ClearEditText accountText;
    @ViewId(R.id.ui_sign_in_password)
    private ClearEditText passwordText;

    @Override
    public int getLayout() {
        return R.layout.fragment_sign_in;
    }

//    private boolean isScreenOn() {
//        PowerManager pm = (PowerManager) Activity().getSystemService(Context.POWER_SERVICE);
//        return Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn();
//    }

    @Override
    public void doingInResume() {
        //log("doing in resume");
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
                ToastHelper.make(Activity()).showMsg(R.string.ui_text_sign_in_error_account_error);
            } else if (TextUtils.isEmpty(passwordText.getValue())) {
                ToastHelper.make(Activity()).showMsg(R.string.ui_text_sign_in_error_password_error);
            } else {
                // 开始登录
                httpRequest(loginParams(accountText.getValue(), passwordText.getValue()));
            }
        } else {
            String params = String.valueOf(id == R.id.ui_sign_in_to_sign_up ? PhoneVerifyFragment.VT_SIGN_UP : PhoneVerifyFragment.VT_PASSWORD);
            openActivity(PhoneVerifyFragment.class.getName(), params, true, true);
        }
    }

    @Override
    protected void onDelayRefreshComplete(@BaseDelayRefreshSupportFragment.DelayType int type) {

    }

    private JsonRequest<Regist> loginParams(String account, String password) {
        LoginParam param = new LoginParam(account, password, "");
        String json = Json.gson(HttpRichParamModel.class).toJson(param, new TypeToken<LoginParam>() {
        }.getType());
        JsonRequest<Regist> login = new JsonRequest<>(param, Regist.class);
        login.setHttpListener(new OnHttpListener<Regist>() {

            @Override
            public void onSucceed(Regist data, Response<Regist> response) {
                super.onSucceed(data, response);
                ToastHelper.make().showMsg(data.getMsg());
                // 检测服务器返回的状态
                if (data.success()) {
                    // 打开主页面
                    finish(true);
                } else {
                    ToastHelper.make().showMsg(data.getMsg());
                }
            }
        }).setHttpBody(new JsonBody(json), HttpMethods.Post);
        return login;
    }
}
