package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.ListItem;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.ToggleButton;

/**
 * <b>功能描述：</b>单选开关ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/18 09:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/18 09:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ToggleableViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_toggle_title)
    private TextView titleTextView;
    @ViewId(R.id.ui_holder_view_toggle_button)
    private ToggleButton toggleButton;

    private int index;

    public ToggleableViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        toggleButton.addOnToggleChangedListener(new ToggleButton.OnToggleChangedListener() {

            @Override
            public void onToggle(ToggleButton toggleButton, boolean on) {
                int position = getAdapterPosition();
                if (position < 0) {
                    position = index;
                }
                if (null != toggleChangedListener) {
                    toggleChangedListener.onChange(position, on);
                }
            }
        });
    }

    public void showContent(String string) {
        String[] texts = string.split("\\|", -1);
        index = Integer.valueOf(texts[0]);
        showContent(texts[1], (!StringHelper.isEmpty(texts[2]) && texts[2].equals("1")));
    }

    public void showContent(ListItem item) {
        index = item.getIndex();
        showContent(item.getTitle(), !StringHelper.isEmpty(item.getValue()) && item.getValue().equals("1"));
    }

    public void showContent(String title, boolean toggled) {
        titleTextView.setText(title);
        if (toggled) {
            if (!toggleButton.isToggleOn()) {
                toggleButton.setToggleOn(true);
            }
        } else {
            if (toggleButton.isToggleOn()) {
                toggleButton.setToggleOff(true);
            }
        }
    }

    public boolean isToggled() {
        return toggleButton.isToggleOn();
    }

    private OnViewHolderToggleChangedListener toggleChangedListener;

    public void addOnViewHolderToggleChangedListener(OnViewHolderToggleChangedListener l) {
        toggleChangedListener = l;
    }

    /**
     * ViewHolder里的toggleButton状态改变事件处理接口
     */
    public interface OnViewHolderToggleChangedListener {
        /**
         * toggle开关状态改变时
         *
         * @param index  当前ViewHolder所在列表里的位置
         * @param togged 是否开启，true=开启
         */
        void onChange(int index, boolean togged);
    }
}
