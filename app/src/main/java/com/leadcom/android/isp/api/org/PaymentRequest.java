package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Payment;
import com.litesuits.http.request.param.HttpMethods;

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

    private static class ListPayment extends PageQuery<Payment> {
    }

    private static class SinglePayment extends SingleQuery<Payment> {
    }

    @Override
    protected String url(String action) {
        return format("/pay%s", action);
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
    public void save(Payment payment) {
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", payment.getGroupId())
                    .put("payAmount", payment.getPayAmount())
                    .put("payDate", payment.getPayDate())
                    .put("remark", payment.getRemark())
                    .put("squadId", checkNull(payment.getSquadId()))
                    .put("userId", payment.getUserId())
                    .put("image", Attachment.getJson(payment.getImage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(ListPayment.class, url(SAVE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查询组织的缴费记录列表
     */
    public void list(String groupId) {
        executeHttpRequest(getRequest(ListPayment.class, format("%s?groupId=%s", url(LIST), groupId), "", HttpMethods.Get));
    }

    /**
     * 查询组织用户的缴费记录列表
     */
    public void listByUserId(String groupId, String userId) {
        executeHttpRequest(getRequest(ListPayment.class, format("%s?groupId=%s&userId=%s", url("/getPayFlowerByUserId"), groupId, userId), "", HttpMethods.Get));
    }
}
