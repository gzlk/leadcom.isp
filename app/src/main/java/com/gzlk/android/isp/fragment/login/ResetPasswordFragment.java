package com.gzlk.android.isp.fragment.login;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

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
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_VERIFY_TYPE, Integer.valueOf(params));
        rpf.setArguments(bundle);
        return rpf;
    }

    @ViewId(R.id.ui_verify_reset_password_text)
    private ClearEditText passwordText;

    @Override
    public int getLayout() {
        return R.layout.fragment_verify_reset_password;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_reset_password_fragment_title);
        setLeftText(R.string.ui_text_reset_password_fragment_title_left_text);
        if (verifyType != VT_PASSWORD) {
            ToastHelper.make(Activity()).showMsg("无效的参数，不能重设密码");
            finish();
        }
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_verify_reset_password_finish})
    private void elementClick(View view) {
        if (!StringHelper.isEmpty(passwordText.getValue())) {
            // 密码验证通过，返回到登录页面
            finishToSignIn();
        }
    }
}
