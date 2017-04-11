package com.gzlk.android.isp.fragment.login;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.view.ClearEditText;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>手机号码验证页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/11 08:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/11 08:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PhoneVerifyFragment extends BaseVerifyFragment {

    public static PhoneVerifyFragment newInstance(String params) {
        PhoneVerifyFragment pvf = new PhoneVerifyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_VERIFY_TYPE, Integer.valueOf(params));
        pvf.setArguments(bundle);
        return pvf;
    }

    @ViewId(R.id.ui_verify_phone_protocol_container)
    private LinearLayout protocolView;
    @ViewId(R.id.ui_verify_phone_phone)
    private ClearEditText phoneText;

    @Override
    public int getLayout() {
        return R.layout.fragment_verify_phone;
    }

    @Override
    public void doingInResume() {
        protocolView.setVisibility(verifyType == VT_SIGN_UP ? View.VISIBLE : View.GONE);
        setCustomTitle(verifyType == VT_SIGN_UP ? R.string.ui_text_verify_phone_fragment_title_sin_up : R.string.ui_text_verify_phone_fragment_title_reset_password);
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_verify_phone_to_next_step, R.id.ui_verify_phone_to_service_protocol,
            R.id.ui_verify_phone_to_privacy_policy})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_verify_phone_to_next_step:
                if (!StringHelper.isEmpty(phoneText.getValue())) {
                    openActivity(CodeVerifyFragment.class.getName(), String.valueOf(verifyType), true, true);
                } else {
                    ToastHelper.make(Activity()).showMsg("手机号码输入不正确");
                }
                break;
            case R.id.ui_verify_phone_to_service_protocol:
                break;
            case R.id.ui_verify_phone_to_privacy_policy:
                break;
        }
    }
}
