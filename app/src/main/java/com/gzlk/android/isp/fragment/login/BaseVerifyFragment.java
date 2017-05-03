package com.gzlk.android.isp.fragment.login;

import android.os.Bundle;

import com.gzlk.android.isp.fragment.base.BaseLayoutSupportFragment;

/**
 * <b>功能描述：</b>验证相关的fragment页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/11 15:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/11 15:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseVerifyFragment extends BaseLayoutSupportFragment {

    /**
     * 注册时的验证
     */
    public static final int VT_SIGN_UP = 0;
    /**
     * 忘记密码时的验证
     */
    public static final int VT_PASSWORD = 1;
    /**
     * 修改手机号码时的验证
     */
    public static final int VT_MODIFY_PHONE = 2;

    protected static final String PARAM_VERIFY_TYPE = "pvf_type";
    protected static final String PARAM_VERIFY_PHONE = "pvf_phone";
    protected static final String PARAM_VERIFY_CODE = "pvf_code";
    private static final String PARAM_VERIFY_CODE_RECEIVED = "pvf_received_code";

    /**
     * 用户接收到的验证码，如果为空则说明用户没有权限直接读取SMS内容
     */
    protected String receivedVerifyCode = "";
    /**
     * 当前验证方式
     */
    protected int verifyType = 0;
    /**
     * 用户输入的手机号码
     */
    protected String verifyPhone;
    /**
     * 用户收到的校验码
     */
    protected String verifyCode;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        verifyType = bundle.getInt(PARAM_VERIFY_TYPE, VT_SIGN_UP);
        verifyPhone = bundle.getString(PARAM_VERIFY_PHONE, "");
        verifyCode = bundle.getString(PARAM_VERIFY_CODE, "");
        receivedVerifyCode = bundle.getString(PARAM_VERIFY_CODE_RECEIVED, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putInt(PARAM_VERIFY_TYPE, verifyType);
        bundle.putString(PARAM_VERIFY_PHONE, verifyPhone);
        bundle.putString(PARAM_VERIFY_CODE, verifyCode);
        bundle.putString(PARAM_VERIFY_CODE_RECEIVED, receivedVerifyCode);
    }
}
