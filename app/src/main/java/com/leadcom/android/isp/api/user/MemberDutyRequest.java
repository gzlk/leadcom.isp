package com.leadcom.android.isp.api.user;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.model.user.MemberDuty;
import com.litesuits.http.request.param.HttpMethods;


/**
 * <b>功能描述：</b>组织成员履职api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/08/14 18:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberDutyRequest extends Request<MemberDuty> {

    public static MemberDutyRequest request() {
        return new MemberDutyRequest();
    }

    private static class PageDuty extends PageQuery<MemberDuty> {
    }

    @Override
    protected String url(String action) {
        return format("/user/appUserRecord%s", action);
    }

    @Override
    protected Class<MemberDuty> getType() {
        return MemberDuty.class;
    }

    @Override
    public MemberDutyRequest setOnSingleRequestListener(OnSingleRequestListener<MemberDuty> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public MemberDutyRequest setOnMultipleRequestListener(OnMultipleRequestListener<MemberDuty> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 无任何限制
     */
    public static final int OPE_NONE = 0;
    /**
     * 查询档案
     */
    public static final int OPE_ARCHIVE = 1;
    /**
     * 查询活动
     */
    public static final int OPE_ACTIVITY = 2;
    /**
     * 全部年限的内容
     */
    public static final int YEAR_ALL = 0;
    /**
     * 今年的内容
     */
    public static final int YEAR_THIS = 1;
    /**
     * 去年的内容
     */
    public static final int YEAR_LAST = 2;

    /**
     * 拉取指定某人的履职记录
     *
     * @param groupId    组织id
     * @param createYear 年份:0.全部,1.今年,2.去年
     * @param classifyId 档案性质
     * @param category   档案类型
     */
    public void list(String groupId, int createYear, String classifyId, String category) {
        String url = format("%s/count?groupId=%s", url(LIST), groupId);
        url = format("%s&createDate=%d", url, createYear);
        if (!isEmpty(classifyId)) {
            url = format("%s&docClassifyId=%s", url, classifyId);
        }
        if (!isEmpty(category)) {
            url = format("%s&category=%s", url, category);
        }
        executeHttpRequest(getRequest(PageDuty.class, url, "", HttpMethods.Get));
    }
}
