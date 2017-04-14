package com.gzlk.android.isp.fragment.login;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseDelayRefreshSupportFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

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

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void onAttach(Context context) {
        //DEBUG = true;
        super.onAttach(context);
    }

    private boolean isScreenOn() {
        PowerManager pm = (PowerManager) Activity().getSystemService(Context.POWER_SERVICE);
        return Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn();
    }

    @Override
    public void doingInResume() {
        log("doing in resume");
        log(String.format("screen on: %s, visible: %s, resumed: %s",
                isScreenOn(), isVisible(), isResumed()));
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        log("on hidden changed: " + hidden);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

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
            //if (!StringHelper.isEmpty(accountText.getValue()) && !StringHelper.isEmpty(passwordText.getValue())) {
                // 打开主页面
                finish(true);
            //} else {
            //    ToastHelper.make(Activity()).showMsg("您的账号或密码输入不正确");
            //}
        } else {
            String params = String.valueOf(id == R.id.ui_sign_in_to_sign_up ? PhoneVerifyFragment.VT_SIGN_UP : PhoneVerifyFragment.VT_PASSWORD);
            openActivity(PhoneVerifyFragment.class.getName(), params, true, true);
        }
    }

    @Override
    protected void onDelayRefreshComplete(@BaseDelayRefreshSupportFragment.DelayType int type) {

    }
}
