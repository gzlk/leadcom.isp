package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.NumericQuery;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Payment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>财务记账记录api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/24 21:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/24 21:33  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class PaymentRequest extends Request<Payment> {

    public static PaymentRequest request() {
        return new PaymentRequest();
    }

    private static class PagePayment extends PageQuery<Payment> {
    }

    private static class SinglePayment extends SingleQuery<Payment> {
    }

    private static class BoolPayment extends BoolQuery<Payment> {
    }

    @Override
    protected String url(String action) {
        return format("/pay%s", action);
    }

    private String expend(String action) {
        return format("/expend%s", action);
    }

    private String check(String action) {
        return format("/unChecked%s", action);
    }

    @Override
    protected Class<Payment> getType() {
        return Payment.class;
    }

    @Override
    public PaymentRequest setOnSingleRequestListener(OnSingleRequestListener<Payment> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public PaymentRequest setOnMultipleRequestListener(OnMultipleRequestListener<Payment> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 添加缴费记录
     */
    public void addPayment(Payment payment) {
        directlySave = false;
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", payment.getGroupId())
                    .put("userId", payment.getUserId())
                    .put("payAmount", payment.getPayAmount())
                    .put("payDate", payment.getPayDate())
                    .put("remark", payment.getRemark())
                    .put("image", new JSONArray(Attachment.getJson(payment.getImage())));
            if (!isEmpty(payment.getSquadId())) {
                object.put("squadId", payment.getSquadId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SinglePayment.class, url(SAVE), object.toString(), HttpMethods.Post));
    }

    public void findPayment(String paymentId) {
        directlySave = false;
        executeHttpRequest(getRequest(SinglePayment.class, format("%s?id=%s", url("/getById"), paymentId), "", HttpMethods.Get));
    }

    /**
     * 查询组织的缴费记录列表
     */
    public void listPayment(String groupId, int pageNumber, int pageSize) {
        directlySave = false;
        executeHttpRequest(getRequest(PagePayment.class, format("%s?groupId=%s%s", url(LIST), groupId, page(pageNumber, pageSize)), "", HttpMethods.Get));
    }

    /**
     * 查询组织用户的缴费记录列表
     */
    public void listPaymentByUserId(String groupId, String userId, int pageNumber, int pageSize) {
        directlySave = false;
        executeHttpRequest(getRequest(PagePayment.class, format("%s?groupId=%s&userId=%s%s", url("/getPayFlowerByUserId"), groupId, userId, page(pageNumber, pageSize)), "", HttpMethods.Get));
    }

    /**
     * 查询统计结果
     */
    public void collectPayment(String groupId, String squadId, String date, int pageNumber, int pageSize) {
        directlySave = false;
        String url = format("%s?groupId=%s%s", url("/query"), groupId, page(pageNumber, pageSize));
        if (!isEmpty(squadId)) {
            url = format("%s&squadId=%s", url, squadId);
        }
        if (!isEmpty(date)) {
            url = format("%s&dateString=%s", url, date);
        }
        executeHttpRequest(getRequest(PagePayment.class, url, "", HttpMethods.Get));
    }

    /**
     * 添加支出记录
     */
    public void addExpend(Payment payment) {
        directlySave = false;
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", payment.getGroupId())
                    //.put("userId", payment.getUserId())
                    .put("expendAmount", payment.getExpendAmount())
                    .put("expendDate", payment.getExpendDate())
                    .put("receiverId", payment.getReceiverId())
                    .put("certifierId", payment.getCertifierId())
                    .put("approverId", payment.getApproverId())
                    .put("remark", payment.getRemark())
                    .put("image", new JSONArray(Attachment.getJson(payment.getImage())));
            if (!isEmpty(payment.getSquadId())) {
                object.put("squadId", payment.getSquadId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SinglePayment.class, expend(SAVE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查询支出列表
     */
    public void findExpend(String expendId) {
        directlySave = false;
        executeHttpRequest(getRequest(SinglePayment.class, format("%s?id=%s", expend("/selectById"), expendId), "", HttpMethods.Get));
    }

    /**
     * 查询已审批的支出列表
     */
    public void listExpend(String groupId, int pageNumber, int pageSize) {
        directlySave = false;
        executeHttpRequest(getRequest(PagePayment.class, format("%s?groupId=%s%s", expend(LIST), groupId, page(pageNumber, pageSize)), "", HttpMethods.Get));
    }

    /**
     * 更新支出状态
     */
    public void updateExpend(String expendId, int state) {
        directlySave = false;
        executeHttpRequest(getRequest(SinglePayment.class, format("%s?id=%s&state=%d", expend("/updateState"), expendId, state), "", HttpMethods.Get));
    }

    /**
     * 查询待审批列表
     */
    public void listUnchecked(String groupId, int pageNumber, int pageSize) {
        directlySave = false;
        executeHttpRequest(getRequest(PagePayment.class, format("%s?groupId=%s%s", check(LIST), groupId, page(pageNumber, pageSize)), "", HttpMethods.Get));
    }

    /**
     * 删除待审核记录
     */
    public void deleteUncheck(String uncheckId) {
        directlySave = false;
        executeHttpRequest(getRequest(BoolPayment.class, format("%s?expendFlowerId=%s", check(DELETE), uncheckId), "", HttpMethods.Get));
    }

    /**
     * 清空某个组织下的待审核记录(仅清空通知)
     */
    public void clearUncheck(String groupId) {
        directlySave = false;
        executeHttpRequest(getRequest(BoolPayment.class, format("%s?groupId=%s", check("/empty"), groupId), "", HttpMethods.Get));
    }
}
