package com.gzlk.android.isp.api;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private static class Register extends Output<User> {
    }

    private static final String SYSTEM = "/system";
    private static final String SIGN_UP = SYSTEM + "/regist";
    private static final String SIGN_IN = SYSTEM + "/login";
    private static final String SYNC = SYSTEM + "/sync";
    private static final String CAPTCHA = SYSTEM + "/getCaptchaTo";
    private static final String PASSWORD = SYSTEM + "/resetPwd";
    private static final String INVITE_TO_GROUP = SYSTEM + "/sms/invToJoinGroup";
    private static final String INVITE_TO_SQUAD = SYSTEM + "/sms/invToJoinSquad";

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

        httpRequest(getRequest(Register.class, SIGN_IN, object.toString(), HttpMethods.Post));
    }

    /*
      同步用户信息
     */
//    public void sync() {
//        httpRequest(getRequest(Register.class, format("%s?accessToken=%s", SYNC, Cache.cache().accessToken), "", HttpMethods.Get));
//    }

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
        // {loginId:"",password:"",phone:"",captcha:""}
        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone)
                    .put("loginId", checkNull(loginId))
                    .put("captcha", captcha)
                    .put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(Register.class, PASSWORD, object.toString(), HttpMethods.Post));
    }

    /**
     * 重置手机号码时发送验证码
     */
    public void getCaptchaToResetPhone(String phone) {
        String param = format("/system/getCaptchaToResetPhone?phone=%s", phone);
        httpRequest(getRequest(Register.class, param, "", HttpMethods.Get));
    }

    /**
     * 重置手机号码
     */
    public void resetPhone(String phone, String captcha) {
        String param = format("/system/resetPhone?phone=%s&captcha=%s", phone, captcha);
        httpRequest(getRequest(Register.class, param, "", HttpMethods.Get));
    }

    /**
     * 邀请手机通讯录好友加入组织
     */
    public void inviteJoinIntoGroup(@NonNull String phone, @NonNull String groupId) {
        // {toPhoneArr:['1591111111','186111111'],accessToken:"",toGroupId:""}
        ArrayList<String> phones = new ArrayList<>();
        phones.add(phone);
        JSONObject object = new JSONObject();
        try {
            object.put("toPhoneArr", new JSONArray(phones))
                    .put("toGroupId", groupId)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(Register.class, INVITE_TO_GROUP, object.toString(), HttpMethods.Post));
    }

    /**
     * 邀请手机通讯录好友加入小组
     */
    public void inviteJoinIntoSquad(@NonNull String phone, @NonNull String squadId) {
        // {toPhoneArr:['1591111111','186111111'],accessToken:"",toSquadId:""}
        ArrayList<String> phones = new ArrayList<>();
        phones.add(phone);
        JSONObject object = new JSONObject();
        try {
            object.put("toPhoneArr", new JSONArray(phones))
                    .put("toSquadId", squadId)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(Register.class, INVITE_TO_SQUAD, object.toString(), HttpMethods.Post));
    }
}
