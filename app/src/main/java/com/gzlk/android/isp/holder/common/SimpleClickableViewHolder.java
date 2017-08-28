package com.gzlk.android.isp.holder.common;

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.common.SimpleClickableItem;
import com.gzlk.android.isp.model.organization.Concern;
import com.gzlk.android.isp.model.organization.Squad;
import com.gzlk.android.isp.model.user.UserExtra;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>简单的可点击的viewholder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 23:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 23:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleClickableViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_simple_clickable_title)
    public TextView titleTextView;
    @ViewId(R.id.ui_holder_view_simple_clickable_value)
    public TextView valueTextView;
    @ViewId(R.id.ui_holder_view_simple_clickable_value_icon)
    public CustomTextView valueIcon;
    @ViewId(R.id.ui_holder_view_simple_clickable_image)
    public ImageDisplayer imageView;
    @ViewId(R.id.ui_holder_view_simple_clickable_right_icon_add)
    public CustomTextView appendIcon;
    @ViewId(R.id.ui_holder_view_simple_clickable_right_icon)
    public CustomTextView rightIcon;
    @ViewId(R.id.ui_tool_view_contact_button2)
    private TextView deleteView;

    public SimpleClickableViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        if (null != imageView) {
            imageView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    /**
     * 是否需要最左侧的空白距离
     */
    public void isNeedLeftPadding(boolean need) {
        int top = itemView.getPaddingTop();
        int right = itemView.getPaddingRight();
        int bottom = itemView.getPaddingBottom();
        itemView.setPadding(need ? right : 0, top, right, bottom);
    }

    public void showContent(Model model) {
        if (model instanceof Squad) {
            showContent((Squad) model);
        } else if (model instanceof SimpleClickableItem) {
            showContent((SimpleClickableItem) model);
        } else if (model instanceof UserExtra) {
            UserExtra ue = (UserExtra) model;
            boolean deletable = null != deleteView && deleteView.getVisibility() == View.VISIBLE;
            showContent(-1, ue.getTitle(), StringHelper.getString(R.string.ui_text_user_property_self_defined_value, ue.getContent(), deletable ? format("(%s)", StringHelper.getStringArray(R.array.ui_text_user_property_self_defined_shown_type)[ue.getShow()]) : ""));
        } else {
            showContent(model.getId());
        }
    }

    public void showContent(String string) {
        SimpleClickableItem item = new SimpleClickableItem(string);
        showContent(item);
//        String[] strings = string.split("\\|", -1);
//        showContent(Integer.valueOf(strings[0]), strings[1], strings[2]);
//        if (strings.length > 3) {
//            if (isEmpty(strings[3])) {
//                rightIcon.setVisibility(View.GONE);
//                appendIcon.setVisibility(View.GONE);
//            } else {
//                try {
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            boolean invisible = isEmpty(strings[3]) || strings[3].equals("0");
//            rightIcon.setVisibility(invisible ? View.GONE : View.VISIBLE);
//        }
    }

    public void showImage(String path) {
        if (null != imageView) {
            imageView.setVisibility(isEmpty(path) ? View.GONE : View.VISIBLE);
            imageView.displayImage(path, getDimension(R.dimen.ui_base_dimen_button_height), false, false);
            appendIcon.setVisibility(isEmpty(path) ? View.VISIBLE : View.GONE);
            valueTextView.setVisibility(isEmpty(path) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void showDelete(boolean shown) {
        if (null != deleteView) {
            deleteView.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    public void showContent(Squad squad) {
        // 貌似只显示小组的名称
        showContent(0, "", squad.getName());
    }

    public void showContent(Concern concern) {
        String name = concern.getName();
        name += format("(%s)", Concern.getTypeString(concern.getType()));
        showContent(0, "", name);
    }

    public void showContent(SimpleClickableItem item) {
        showContent(item.getIndex(), item.getTitle(), item.getValue());
        rightIcon.setVisibility(item.isIconVisible() ? View.VISIBLE : View.GONE);
        if (null != appendIcon) {
            appendIcon.setVisibility(item.isAddVisible() ? View.VISIBLE : View.GONE);
        }
    }

    public void showContent(int index, String title, String value) {
        this.index = index;
        titleTextView.setText(title);
        if (isEmpty(title)) {
            // 没有title则value的文字大小变成正常大小
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size));
        }
        if (value.length() > 2 && value.charAt(0) == '0' && value.charAt(1) == 'x') {
            valueTextView.setText(null);
            Integer i = Integer.decode(value);
            if (null != valueIcon) {
                valueIcon.setText(String.valueOf((char) i.intValue()));
                valueIcon.setVisibility(View.VISIBLE);
            }
        } else {
            valueTextView.setText(value);
            if (null != valueIcon) {
                valueIcon.setVisibility(View.GONE);
            }
        }
    }

    private int index;

    @Click({R.id.ui_holder_view_simple_clickable,
            R.id.ui_tool_view_contact_button2})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_contact_button2:
                // 删除
                if (null != mOnHandlerBoundDataListener) {
                    mOnHandlerBoundDataListener.onHandlerBoundData(SimpleClickableViewHolder.this);
                }
                break;
            case R.id.ui_holder_view_simple_clickable:
                if (null != mOnViewHolderClickListener) {
                    int pos = getAdapterPosition();
                    mOnViewHolderClickListener.onClick(pos < 0 ? index : pos);
                }
                break;
        }
    }
}
