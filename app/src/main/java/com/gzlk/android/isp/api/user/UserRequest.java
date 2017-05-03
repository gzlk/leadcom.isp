package com.gzlk.android.isp.api.user;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>用户相关api集合<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 10:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 10:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserRequest extends Request<User> {
    private static UserRequest request;

    public static UserRequest request() {
        if (null == request) {
            request = new UserRequest();
        }
        return request;
    }

    static class SingleUser extends Output<User> {
    }

    static class MultipleUser extends Query<User> {
    }

    private static final String USER = "/user/user";

    @Override
    protected String url(String action) {
        return USER + action;
    }

    @Override
    public UserRequest setOnRequestListener(OnRequestListener<User> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public UserRequest setOnRequestListListener(OnRequestListListener<User> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    public static final int TYPE_NAME = 1;
    public static final int TYPE_PHONE = 2;
    public static final int TYPE_EMAIL = 3;
    public static final int TYPE_PASSWORD = 4;
    public static final int TYPE_SEX = 5;
    public static final int TYPE_BIRTHDAY = 6;

    /**
     * 更改用户的信息
     *
     * @param userId 用户id
     * @param type   要修改的属性index
     *               <ul>
     *               <li>TYPE_NAME: 修改昵称</li>
     *               <li>TYPE_PHONE: 修改电话</li>
     *               <li>TYPE_EMAIL: 修改email</li>
     *               <li>TYPE_PASSWORD: 修改密码</li>
     *               <li>TYPE_SEX: 修改性别</li>
     *               </ul>
     * @param value  修改的值
     */
    public void update(String userId, int type, String value) {
        //{_id,name,phone,email,password,sex,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", userId);
            switch (type) {
                case TYPE_BIRTHDAY:
                    object.put("brithday", value);
                    break;
                case TYPE_EMAIL:
                    object.put("email", value);
                    break;
                case TYPE_NAME:
                    object.put("name", value);
                    break;
                case TYPE_PASSWORD:
                    object.put("password", value);
                    break;
                case TYPE_PHONE:
                    object.put("phone", value);
                    break;
                case TYPE_SEX:
                    object.put("sex", value);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleUser.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public void find(String userId) {
        httpRequest(getRequest(SingleUser.class, format("%s?userId=%s", url(FIND), userId), "", HttpMethods.Get));
    }
}
