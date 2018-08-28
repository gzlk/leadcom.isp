package com.leadcom.android.isp.api.user;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.user.UserMessage;
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

    private static class BoolMsg extends BoolQuery<UserMessage> {
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

    public static final int TYPE_NONE = 0;
    public static final int TYPE_USER_ARCHIVE = 1;
    public static final int TYPE_GROUP_ARCHIVE = 2;
    public static final int TYPE_MOMENT = 3;

    /**
     * 拉取我的消息列表
     */
    public void list(int pageNumber, int sourceType) {
        String params = format("%s?%spageNumber=%d", url(LIST), (sourceType > 0 ? format("sourceType=%d&", sourceType) : ""), pageNumber);
        executeHttpRequest(getRequest(MultipleMsg.class, params, "", HttpMethods.Get));
    }

    /**
     * 删除某条消息
     */
    public void delete(String msgId) {
        executeHttpRequest(getRequest(BoolMsg.class, format("%s?userInfoId=%s", url(DELETE), msgId), "", HttpMethods.Get));
    }

    /**
     * 情况消息
     */
    public void clear() {
        executeHttpRequest(getRequest(BoolMsg.class, format("%sByUser", url(DELETE)), "", HttpMethods.Get));
    }
}
