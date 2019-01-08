package com.leadcom.android.isp.holder.organization;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.organization.Payment;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * <b>功能描述：</b>财务统计项目<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/28 20:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/28 20:58  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class FinanceCollectionViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_finance_collect_image)
    private ImageDisplayer imageView;
    @ViewId(R.id.ui_holder_view_finance_collect_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_finance_collect_payment)
    private AppCompatTextView paymentView;
    @ViewId(R.id.ui_holder_view_finance_collect_expend)
    private AppCompatTextView expendView;
    @ViewId(R.id.ui_holder_view_finance_collect_left)
    private TextView leftView;

    private static NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.CHINA);

    public FinanceCollectionViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Payment payment) {
        nameView.setText(payment.getUserName());
        boolean isUser = !isEmpty(payment.getUserId());
        paymentView.setText(numberFormat.format(payment.getTotalPayAmount()));
        expendView.setText(isUser ? "-" : numberFormat.format(payment.getTotalExpendAmount()));
        leftView.setText(isUser ? "-" : numberFormat.format(payment.getUsableAmount()));
    }
}
