package com.leadcom.android.isp.fragment.login;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.common.SystemRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.etc.TimeCounter;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.user.User;
import com.leadcom.android.isp.receiver.SmsReceiver;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredEditText;

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
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_VERIFY_TYPE, Integer.valueOf(strings[0]));
        bundle.putString(PARAM_VERIFY_PHONE, strings[1]);
        cvf.setArguments(bundle);
        return cvf;
    }

    // UI
    @ViewId(R.id.ui_modify_phone_confirm_number)
    private TextView phoneTextView;
    @ViewId(R.id.ui_verify_code_code)
    private ClearEditText codeView;
    @ViewId(R.id.ui_verify_code_to_resend)
    private TextView resend;
    @ViewId(R.id.ui_verify_code_to_next_step)
    private CorneredButton nextStep;
    @ViewId(R.id.ui_verify_code_to_skip)
    private TextView skipTextView;

    private TimeCounter timeCounter;
    // 短消息接收器
    private SmsReceiver mSmsReceiver;

    @Override
    public int getLayout() {
        return verifyType == VT_MODIFY_PHONE ? R.layout.fragment_modify_phone_confirm : R.layout.fragment_verify_code;
    }

    @Override
    protected void destroyView() {
        if (null != timeCounter) {
            timeCounter.cancel();
        }
    }

    @Override
    public void doingInResume() {
        setCustomTitle(verifyType == VT_MODIFY_PHONE ? R.string.ui_text_modify_phone_confirm_fragment_title : R.string.ui_text_verify_code_fragment_title);
        if (verifyType != VT_MODIFY_PHONE) {
            setLeftText(verifyType == VT_SIGN_UP ? R.string.ui_text_verify_code_fragment_title_left_text_sign_up : R.string.ui_text_verify_code_fragment_title_left_text_reset_password);
        }
        if (null != phoneTextView) {
            phoneTextView.setText(StringHelper.getString(R.string.ui_text_modify_phone_number_86, verifyPhone));
        }
        //startTimeCounter();
        codeView.addOnValueVerifyingListener(valueVerifyingListener);
    }

    private CorneredEditText.OnValueVerifyingListener valueVerifyingListener = new CorneredEditText.OnValueVerifyingListener() {
        @Override
        public void onVerifying(boolean passed) {
            if (!passed) {
                // 验证没通过则按钮禁用
                nextStep.setEnabled(false);
            } else {
                if (!StringHelper.isEmpty(receivedVerifyCode)) {
                    // 如果已经收到了验证码了则验证输入是否跟收到的一样
                    nextStep.setEnabled(codeView.getValue().equals(receivedVerifyCode));
                } else {
                    // 没收到验证码时，依靠输入内容是否符合验证规则进行下一步操作
                    nextStep.setEnabled(true);
                }
            }
        }
    };

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private void startTimeCounter() {
        if (null == timeCounter) {
            timeCounter = new TimeCounter(60000, 1000);
            timeCounter.addOnTimeCounterListener(timeCounterListener);
            start();
        }
    }

    // 启动倒计时
    private void start() {
        resend.setEnabled(false);
        resend.setTextColor(getColor(R.color.textColorHint));
        nextStep.setEnabled(false);
        timeCounter.start();
        // 注册SMS接收器
        registerSmsReceiver();
    }

    // 手动停止计时器
    private void stop() {
        // 停止时取消SMS监听
        unregisterSmsReceiver();
        if (null != timeCounter) {
            timeCounter.cancel();
        }
        resend.setEnabled(true);
        resend.setTextColor(getColor(R.color.textColor));
        resend.setText(R.string.ui_text_verify_code_time_count_resend);
    }

    private TimeCounter.OnTimeCounterListener timeCounterListener = new TimeCounter.OnTimeCounterListener() {

        @Override
        public void onTick(long timeLeft) {
            long timeUsed = timeLeft / 1000;
            resend.setText(StringHelper.getString(R.string.ui_text_verify_code_time_count_down, timeUsed));
            //if (BuildConfig.DEBUG && timeUsed < 50 && skipTextView.getVisibility() == View.GONE) {
            // debug版本在10s后可以随便输验证码并进行下一步
            //    skipTextView.setVisibility(View.VISIBLE);
            //    nextStep.setEnabled(true);
            //}
        }

        @Override
        public void onFinished() {
            stop();
            nextStep.setEnabled(true);
        }
    };

    @Click({R.id.ui_verify_code_to_resend, R.id.ui_verify_code_to_next_step})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_verify_code_to_resend:
                // 检测SMS接收权限
                checkSmsReceivablePermission();
                //timeCounter.cancel();
                //start();
                break;
            case R.id.ui_verify_code_to_next_step:
                if (null != timeCounter) {
                    timeCounter.cancel();
                }
                String code = codeView.getValue();
                if (StringHelper.isEmpty(code)) {
                    ToastHelper.make().showMsg(R.string.ui_text_verify_code_value_incorrect);
                    return;
                }
                if (!StringHelper.isEmpty(receivedVerifyCode) && !code.equals(receivedVerifyCode)) {
                    // 如果收到的验证码不为空则需要先验证一下用户输入的是否正确
                    ToastHelper.make().showMsg(R.string.ui_text_verify_code_value_not_same_as_received);
                    return;
                }
                super.verifyCode = code;
                if (verifyType == VT_MODIFY_PHONE) {
                    // 更改手机号码时验证手机号码
                    tryModifyMyPhoneNumber();
                } else {
                    verifyCaptcha();
//                    if (verifyType == VT_PASSWORD) {
//                        // 重置密码时需要先校验验证码是否正确
//                        verifyCaptcha();
//                    } else {
//                        String params = StringHelper.format("%d,%s,%s", verifyType, verifyPhone, code);
//                        openActivity(SignUpFragment.class.getName(), params, true, true);
//                    }
                }
                break;
        }
    }

    private void verifyCaptcha() {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    if (verifyType == VT_PASSWORD) {
                        String params = StringHelper.format("%d,%s,%s", verifyType, verifyPhone, "");
                        openActivity(ResetPasswordFragment.class.getName(), params, true, true);
                    } else {
                        String params = StringHelper.format("%d,%s,%s", verifyType, verifyPhone, CodeVerifyFragment.super.verifyCode);
                        openActivity(SignUpFragment.class.getName(), params, true, true);
                    }
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_verify_code_value_error);
                }
            }
        }).verifyCaptcha(verifyPhone, super.verifyCode);
    }

    private void tryModifyMyPhoneNumber() {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    resultData(verifyPhone);
                }
                ToastHelper.make().showMsg(message);
            }
        }).resetPhone(verifyPhone, super.verifyCode);
    }

    @Override
    public void onDestroy() {
        unregisterSmsReceiver();
        super.onDestroy();
    }

    /**
     * 注册sms接收器
     */
    private void registerSmsReceiver() {
        if (null == mSmsReceiver) {
            mSmsReceiver = new SmsReceiver();
            mSmsReceiver.addOnSmsReceivedListener(mOnSmsReceivedListener);
            try {
                Activity().registerReceiver(mSmsReceiver, SmsReceiver.getIntentFilter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SmsReceiver.OnSmsReceivedListener mOnSmsReceivedListener = new SmsReceiver.OnSmsReceivedListener() {

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onReceived(String address, String body) {
            if (body.contains(StringHelper.getString(R.string.app_sms_stuff_header))) {
                unregisterSmsReceiver();
                nextStep.setEnabled(true);
                resend.setText(R.string.ui_text_verify_code_fetched);
                timeCounter.cancel();
                receivedVerifyCode = SmsReceiver.getVerifyCode(body);
                codeView.setValue(receivedVerifyCode);
            }
        }
    };

    private void unregisterSmsReceiver() {
        if (null != mSmsReceiver) {
            try {
                Activity().unregisterReceiver(mSmsReceiver);
                mSmsReceiver = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 申请SMS接收权限
     */
    private void requestSMSReceivePermission() {
        tryGrantPermission(Manifest.permission.RECEIVE_SMS, GRANT_SMS, "", "");
    }

    /**
     * 检测SMS接受权限
     */
    private void checkSmsReceivablePermission() {
        if (hasPermission(Manifest.permission.RECEIVE_SMS)) {
            requestVerifyCode();
        } else {
            requestSMSReceivePermission();
        }
    }

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_SMS) {
            requestVerifyCode();
        }
        super.permissionGranted(permissions, requestCode);
    }

    @Override
    public void permissionGrantFailed(int requestCode) {
        if (requestCode == GRANT_SMS) {
            if (verifyType == VT_MODIFY_PHONE) {
                // 重置手机号码时的验证码发送
                requestVerifyCodeForResetPhone();
            } else {
                // 其他验证码发送
                requestVerifyCode();
            }
        }
        super.permissionGrantFailed(requestCode);
    }

    /**
     * 重置手机号码时发送验证码
     */
    private void requestVerifyCodeForResetPhone() {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    startTimeCounter();
                }
            }
        }).getCaptchaToResetPhone(verifyPhone);
    }

    // 请求验证码
    private void requestVerifyCode() {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    startTimeCounter();
                }
            }
        }).getCaptcha(verifyPhone, (verifyType == VT_PASSWORD));
    }
}
