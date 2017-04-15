package com.gzlk.android.isp.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>个人动态内容框架<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 21:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 21:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_moment_date)
    private TextView date;
    @ViewId(R.id.ui_holder_view_moment_month)
    private TextView month;
    @ViewId(R.id.ui_holder_view_moment_content)
    private LinearLayout content;

    private boolean isToday = false;
    private MomentContentTodayViewHolder today;

    public MomentViewHolder(View itemView, BaseSwipeRefreshSupportFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void setAsToday(boolean asToday) {
        isToday = asToday;
    }

    private void showToday() {
        if (!isToday) {
            return;
        }
        if (null == today) {
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.holder_view_moment_content_today, fragment().mRecyclerView, false);
            today = new MomentContentTodayViewHolder(view, fragment());
        }
        date.setText("今天");
        month.setText("");
        content.addView(today.itemView);
    }

    public void showContent() {
        content.removeAllViews();
        if (isToday) {
            showToday();
        } else {
            date.setText("15");
        }
    }
}
