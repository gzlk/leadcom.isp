package com.gzlk.android.isp.api.user;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    public static UserRequest request() {
        return new UserRequest();
    }

    private static class SingleUser extends Output<User> {
    }

    private static class MultipleUser extends Query<User> {
    }

    private static final String USER = "/user/user";

    @Override
    protected String url(String action) {
        return USER + action;
    }

    @Override
    protected Class<User> getType() {
        return User.class;
    }

    @Override
    public UserRequest setOnSingleRequestListener(OnSingleRequestListener<User> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UserRequest setOnMultipleRequestListener(OnMultipleRequestListener<User> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public static final int TYPE_NAME = 1;
    public static final int TYPE_PHONE = 2;
    public static final int TYPE_EMAIL = 3;
    public static final int TYPE_PASSWORD = 4;
    public static final int TYPE_SEX = 5;
    public static final int TYPE_BIRTHDAY = 6;
    public static final int TYPE_PHOTO = 7;
    public static final int TYPE_ID_NUM = 8;
    public static final int TYPE_COMPANY = 9;
    public static final int TYPE_DUTY = 10;

    /**
     * 更改我的信息
     *
     * @param type  要修改的属性index
     *              <ul>
     *              <li>TYPE_NAME: 修改昵称</li>
     *              <li>TYPE_PHONE: 修改电话</li>
     *              <li>TYPE_EMAIL: 修改email</li>
     *              <li>TYPE_PASSWORD: 修改密码</li>
     *              <li>TYPE_SEX: 修改性别</li>
     *              </ul>
     * @param value 修改的值
     */
    public void update(int type, String value) {
        // 这里不要直接保存返回的用户信息，里面有很多null值
        directlySave = false;
        // {password,headPhoto,name,phone,email,idNum,birthday,company,position,isAuth,sex,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("accessToken", Cache.cache().accessToken);
            switch (type) {
                case TYPE_BIRTHDAY:
                    object.put("birthday", value);
                    break;
                case TYPE_COMPANY:
                    object.put("company", value);
                    break;
                case TYPE_DUTY:
                    object.put("position", value);
                    break;
                case TYPE_EMAIL:
                    object.put("email", value);
                    break;
                case TYPE_ID_NUM:
                    object.put("idNum", value);
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
                case TYPE_PHOTO:
                    object.put("headPhoto", value);
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

    private void findInCache(String userId) {
        User user = new Dao<>(User.class).query(userId);
        if (null == user) {
            httpRequest(getRequest(SingleUser.class, format("%s?userId=%s", url(FIND), userId), "", HttpMethods.Get));
        } else {
            if (null != onSingleRequestListener) {
                onSingleRequestListener.onResponse(user, true, "");
            }
        }
    }

    /**
     * 拉取某个用户的基本信息
     */
    public void find(String userId) {
        findInCache(userId);
    }

    private void findInCache(String loginId, String phone, String name) {
        QueryBuilder<User> builder = new QueryBuilder<>(User.class);
        if (StringHelper.isEmpty(loginId)) {
            builder.where(User.Field.LoginId + " IS NOT NULL ");
        } else {
            builder.where(User.Field.LoginId + " like ?", "%" + loginId + "%");
        }
        builder.whereAppendAnd();
        if (StringHelper.isEmpty(phone)) {
            builder.whereAppend(User.Field.Phone + " IS NOT NULL ");
        } else {
            builder.whereAppend(User.Field.Phone + " like ?", "%" + phone + "%");
        }
        builder.whereAppendAnd();
        if (StringHelper.isEmpty(name)) {
            builder.whereAppend(Model.Field.Name + " IS NOT NULL ");
        } else {
            builder.whereAppend(Model.Field.Name + " like ?", "%" + name + "%");
        }
        List<User> users = new Dao<>(User.class).query(builder);
        if (null == users || users.size() < 1) {
            String params = format("?pageSize=%d&pageNumber=%d&loginId=%s&name=%s&phone=%s", PAGE_SIZE, 1, loginId, name, phone);
            httpRequest(getRequest(MultipleUser.class, format("%s%s", url(LIST), params), "", HttpMethods.Get));
        } else {
            if (null != onMultipleRequestListener) {
                onMultipleRequestListener.onResponse(users, true, 1, PAGE_SIZE, users.size(), 1);
            }
        }
    }

    /**
     * 查找用户(所有参数都可以匹配模糊查询)
     *
     * @param loginId 用户的登录id
     * @param phone   用户注册的手机号
     * @param name    用户真实姓名
     */
    public void list(String loginId, String phone, String name) {
        findInCache(loginId, phone, name);
        // pageSize,pageNumber,loginId,name,phone
//        String params = format("?pageSize=%d&pageNumber=%d&loginId=%s&name=%s&phone=%s", PAGE_SIZE, 1, loginId, name, phone);
//        httpRequest(getRequest(SingleUser.class, format("%s%s", url(LIST), params), "", HttpMethods.Get));
    }
}
