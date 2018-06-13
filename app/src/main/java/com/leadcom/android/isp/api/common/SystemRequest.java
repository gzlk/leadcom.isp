package com.leadcom.android.isp.api.common;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.user.User;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>系统相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/24 23:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/24 23:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SystemRequest extends Request<User> {

    public static SystemRequest request() {
        return new SystemRequest();
    }

    private static class Register extends SingleQuery<User> {
    }

    static final String SYSTEM = "/system";
    private static final String SIGN_UP = SYSTEM + "/regist";
    private static final String SIGN_IN = SYSTEM + "/login";
    private static final String CAPTCHA = SYSTEM + "/getCaptchaTo";
    private static final String PASSWORD = SYSTEM + "/resetPwd";
    private static final String VERIFY_CAPTCHA = SYSTEM + "/verifyCaptcha";

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<User> getType() {
        return User.class;
    }

    @Override
    public SystemRequest setOnSingleRequestListener(OnSingleRequestListener<User> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public SystemRequest setOnMultipleRequestListener(OnMultipleRequestListener<User> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 注册新号码
     */
    public void signUp(String phone, String password, String name) {

        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("password", checkNull(password))
                    .put("name", checkNull(name));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(Register.class, SIGN_UP, object.toString(), HttpMethods.Post));
    }

    /**
     * 登录
     */
    public void signIn(String phone, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("password", checkNull(password));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(Register.class, SIGN_IN, object.toString(), HttpMethods.Post));
    }

    /**
     * 获取验证码
     *
     * @param phone         手机号码
     * @param resetPassword true=找回密码时的验证码，false=注册时的验证码
     */
    public void getCaptcha(String phone, boolean resetPassword) {
        String url = StringHelper.format("%s%s?phone=%s", CAPTCHA, (resetPassword ? "ResetPwd" : "Regist"), phone);
        executeHttpRequest(getRequest(Register.class, url, "", HttpMethods.Post));
    }

    /**
     * 登录前修改密码校验验证码
     */
    public void verifyCaptcha(@NonNull String phone, @NonNull String captcha) {
        // {phone,captcha}
        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("captcha", captcha);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(Register.class, VERIFY_CAPTCHA, object.toString(), HttpMethods.Post));
    }

    /**
     * 登陆前重置密码<br/>
     * 不再需要传captcha，上一步已经验证了(2017/06/30 12:00)
     */
    public void resetPassword(@NonNull String phone, @NonNull String password) {
        // {loginId:"",password:"",phone:"",captcha:""}
        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(Register.class, PASSWORD, object.toString(), HttpMethods.Post));
    }

    /**
     * 重置手机号码时发送验证码
     */
    public void getCaptchaToResetPhone(String phone) {
        String param = format("/system/getCaptchaToResetPhone?phone=%s", phone);
        executeHttpRequest(getRequest(Register.class, param, "", HttpMethods.Post));
    }

    /**
     * 重置手机号码
     */
    public void resetPhone(String phone, String captcha) {
        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("captcha", captcha);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(Register.class, "/system/resetPhone", object.toString(), HttpMethods.Post));
    }
}
