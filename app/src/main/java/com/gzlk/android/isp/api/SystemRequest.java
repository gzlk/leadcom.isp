package com.gzlk.android.isp.api;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.user.User;
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

    private static SystemRequest request;

    public static SystemRequest request() {
        if (null == request) {
            request = new SystemRequest();
        }
        return request;
    }

    static class Register extends Output<User> {
    }

    private static final String SYSTEM = "/system";
    private static final String SIGN_UP = SYSTEM + "/regist";
    private static final String SIGN_IN = SYSTEM + "/login";
    private static final String CAPTCHA = SYSTEM + "/getCaptchaTo";
    private static final String PASSWORD = SYSTEM + "/retsetPwd";

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    public SystemRequest setOnRequestListener(OnRequestListener<User> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public SystemRequest setOnRequestListListener(OnRequestListListener<User> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    /**
     * 注册新号码
     */
    public void signUp(String phone, String loginId, String password, String name) {

        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("loginId", checkNull(loginId))
                    .put("password", checkNull(password))
                    .put("name", checkNull(name));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(Register.class, SIGN_UP, object.toString(), HttpMethods.Post));
    }

    /**
     * 登录
     */
    public void signIn(String phone, String password, String loginId) {
        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("loginId", checkNull(loginId))
                    .put("password", checkNull(password));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(Register.class, SIGN_IN, object.toString(), HttpMethods.Post));
    }

    /**
     * 获取验证码
     *
     * @param phone         手机号码
     * @param resetPassword true=找回密码时的验证码，false=注册时的验证码
     */
    public void getCaptcha(String phone, boolean resetPassword) {
        String url = StringHelper.format("%s%s?phone=%s", CAPTCHA, (resetPassword ? "ResetPwd" : "Regist"), phone);
        httpRequest(getRequest(Register.class, url, "", HttpMethods.Get));
    }

    /**
     * 重置密码
     */
    public void resetPassword(String loginId, @NonNull String phone, @NonNull String captcha, @NonNull String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("loginId", checkNull(loginId))
                    .put("captcha", captcha)
                    .put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(Register.class,
                StringHelper.format("%s?loginId=%s&password=%s&phone=%s&captcha=%s", PASSWORD, loginId, password, phone, captcha),
                object.toString(), HttpMethods.Get));
    }
}
