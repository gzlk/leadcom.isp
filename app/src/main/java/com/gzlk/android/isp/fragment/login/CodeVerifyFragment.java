package com.gzlk.android.isp.fragment.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.TimeCounter;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>手机验证码页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/11 15:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/11 15:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CodeVerifyFragment extends BaseVerifyFragment {

    public static CodeVerifyFragment newInstance(String params) {
        CodeVerifyFragment cvf = new CodeVerifyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_VERIFY_TYPE, Integer.valueOf(params));
        cvf.setArguments(bundle);
        return cvf;
    }

    // UI
    @ViewId(R.id.ui_verify_code_code)
    private ClearEditText verifyCode;
    @ViewId(R.id.ui_verify_code_to_resend)
    private TextView resend;
    @ViewId(R.id.ui_verify_code_to_next_step)
    private CorneredButton nextStep;

    private TimeCounter timeCounter;

    @Override
    public int getLayout() {
        return R.layout.fragment_verify_code;
    }

    @Override
    protected void destroyView() {
        if (null != timeCounter) {
            timeCounter.cancel();
        }
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_verify_code_fragment_title);
        setLeftText(verifyType == VT_SIGN_UP ? R.string.ui_text_verify_code_fragment_title_left_text_sign_up : R.string.ui_text_verify_code_fragment_title_left_text_reset_password);
        startTimeCounter();
    }

    @Override
    protected boolean supportDefaultTitle() {
        return true;
    }

    private void startTimeCounter() {
        if (null == timeCounter) {
            timeCounter = new TimeCounter(60000, 1000);
            timeCounter.addOnTimeCounterListener(timeCounterListener);
            start();
        }
    }

    private void start() {
        resend.setEnabled(false);
        resend.setTextColor(getColor(R.color.textColorHint));
        nextStep.setEnabled(false);
        timeCounter.start();
    }

    private TimeCounter.OnTimeCounterListener timeCounterListener = new TimeCounter.OnTimeCounterListener() {

        @Override
        public void onTick(long timeLeft) {
            resend.setText(getString(R.string.ui_text_verify_code_time_count_down, timeLeft / 1000));
        }

        @Override
        public void onFinished() {
            resend.setEnabled(true);
            resend.setTextColor(getColor(R.color.textColor));
            resend.setText(R.string.ui_text_verify_code_time_count_resend);
            nextStep.setEnabled(true);
        }
    };

    @Click({R.id.ui_verify_code_to_resend, R.id.ui_verify_code_to_next_step})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_verify_code_to_resend:
                timeCounter.cancel();
                start();
                break;
            case R.id.ui_verify_code_to_next_step:
                if (!StringHelper.isEmpty(verifyCode.getValue())) {
                    String clazz = verifyType == VT_PASSWORD ? ResetPasswordFragment.class.getName() : SignUpFragment.class.getName();
                    openActivity(clazz, String.valueOf(verifyType), true, true);
                } else {
                    ToastHelper.make(Activity()).showMsg("验证码输入不正确");
                }
                break;
        }
    }
}
