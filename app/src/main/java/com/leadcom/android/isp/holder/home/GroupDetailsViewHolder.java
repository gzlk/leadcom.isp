package com.leadcom.android.isp.holder.home;

import android.text.Html;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.model.archive.Classify;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Payment;
import com.leadcom.android.isp.model.user.UserExtra;


/**
 * <b>功能描述：</b>组织的详细统计信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/16 20:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupDetailsViewHolder extends SimpleClickableViewHolder {

    private static String[] definedValues;

    public GroupDetailsViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        definedValues = StringHelper.getStringArray(R.array.ui_text_user_property_self_defined_shown_type);
    }

    @Override
    public void showContent(SimpleClickableItem item) {
        super.showContent(item);
        if (item.isDisabled()) {
            titleTextView.setTextColor(getColor(R.color.textColorHint));
        }
        Integer i = Integer.decode(item.getIcon());
        valueIcon.setText(String.valueOf((char) i.intValue()));
        valueIcon.setVisibility(View.VISIBLE);
    }

    public void showContent(Classify classify) {
        valueIcon.setText(classify.isDefault() ? R.string.ui_icon_suggestion_box : R.string.ui_icon_favorite_oval);
        valueIcon.setVisibility(View.VISIBLE);
        titleTextView.setText(classify.getName());
        valueTextView.setText("");
    }

    public void showContent(UserExtra extra, boolean isSelf) {
        if (isEmpty(extra.getAccessToken())) {
            valueIcon.setText(R.string.ui_icon_favorite_oval);
        } else {
            valueIcon.setText(extra.getAccessToken());
        }
        titleTextView.setText(extra.getTitle());
        String value = isEmpty(extra.getContent()) ? "" : extra.getContent();
        value = extra.isShowing() || isSelf ? value : "";
        if (isSelf) {
            value += format("(%s)", definedValues[extra.getShow()]);
        } else {
            value += extra.isShowing() ? "" : definedValues[extra.getShow()];
        }
        valueTextView.setText(value);
    }

    public void showContent(Payment payment) {
        valueIcon.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        String header = payment.getUserHeadPhoto();
        if (isEmpty(header) || header.length() < 20) {
            header = "drawable://" + R.drawable.img_default_user_header;
        }
        imageView.displayImage(header, getDimension(R.dimen.ui_base_user_header_image_size_small), false, false);
        if (payment.isPayment()) {
            titleTextView.setText(payment.getUserName());
            valueTextView.setText(format("%.2f", payment.getTotalPayAmount()));
        } else {
            titleTextView.setText(payment.getTitle());
            valueTextView.setText(Html.fromHtml(fragment().formatDate(payment.getExpendDate(), "yyyy年MM月dd日 HH:mm") + payment.getColoredStateText(payment.getState())));
        }
    }

    @Click({R.id.ui_holder_view_simple_clickable, R.id.ui_tool_view_contact_button2})
    @Override
    public void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
