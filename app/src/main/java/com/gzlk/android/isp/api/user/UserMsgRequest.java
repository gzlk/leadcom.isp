package com.gzlk.android.isp.api.user;

import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.model.user.UserMessage;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>个人消息api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/19 10:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/19 10:04 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserMsgRequest extends Request<UserMessage> {

    public static UserMsgRequest request() {
        return new UserMsgRequest();
    }

    private static class SingleMsg extends SingleQuery<UserMessage> {
    }

    private static class MultipleMsg extends PaginationQuery<UserMessage> {
    }

    private static final String MSG = "/user/userInfo";

    @Override
    protected String url(String action) {
        return format("%s%s", MSG, action);
    }

    @Override
    protected Class<UserMessage> getType() {
        return UserMessage.class;
    }

    @Override
    public UserMsgRequest setOnSingleRequestListener(OnSingleRequestListener<UserMessage> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UserMsgRequest setOnMultipleRequestListener(OnMultipleRequestListener<UserMessage> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 拉取我的消息列表
     */
    public void list(int pageNumber) {
        String params = format("%s?pageNumber=%d", url(LIST), pageNumber);
        httpRequest(getRequest(MultipleMsg.class, params, "", HttpMethods.Get));
    }

    /**
     * 删除某条消息
     */
    public void delete(String msgId) {
        httpRequest(getRequest(SingleMsg.class, format("%s?userInfoId=%s", url(DELETE), msgId), "", HttpMethods.Get));
    }

    /**
     * 情况消息
     */
    public void clear() {
        httpRequest(getRequest(SingleMsg.class, format("%sByUser", url(DELETE)), "", HttpMethods.Get));
    }
}
