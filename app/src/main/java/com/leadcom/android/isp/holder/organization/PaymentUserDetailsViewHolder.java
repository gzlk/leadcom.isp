package com.leadcom.android.isp.holder.organization;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.organization.Payment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * <b>功能描述：</b>用户的缴费记录凭证<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/24 23:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/24 23:23  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class PaymentUserDetailsViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_payment_user_details_amount)
    private TextView amountView;
    @ViewId(R.id.ui_holder_view_payment_user_details_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_payment_user_details_remark)
    private TextView remarkView;
    @ViewId(R.id.ui_holder_view_payment_user_details_time)
    private TextView timeView;

    private static NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.CHINA);

    public PaymentUserDetailsViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Payment payment) {
        amountView.setText(numberFormat.format(payment.getPayAmount()));
        nameView.setText(payment.getUserName());
        remarkView.setText(payment.getRemark());
        timeView.setText(fragment().formatDate(payment.getPayDate()));
    }

    @Click({R.id.ui_holder_view_payment_user_details_more})
    private void viewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
