package com.gzlk.android.isp.fragment.base;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>提供加载中、无内容提示的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/12 09:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/12 09:23 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseNothingLoadingSupportFragment extends BaseLayoutSupportFragment {

    @ViewId(R.id.ui_tool_loading_container)
    public LinearLayout loadingLayout;
    @ViewId(R.id.ui_tool_loading_text)
    public TextView loadingTextView;

    @ViewId(R.id.ui_tool_nothing_container)
    public LinearLayout nothingLayout;
    @ViewId(R.id.ui_tool_nothing_text)
    public TextView nothingTextView;

    /**
     * 显示或隐藏loading界面
     */
    public void displayLoading(boolean show) {
        if (null != loadingLayout) {
            loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置loading提示内容
     */
    public void setLoadingText(int text) {
        if (null != loadingTextView) {
            loadingTextView.setText(text);
        }
    }

    /**
     * 设置loading提示内容
     */
    public void setLoadingText(String text) {
        if (null != loadingTextView) {
            loadingTextView.setText(text);
        }
    }


    /**
     * 显示或隐藏nothing界面
     */
    public void displayNothing(boolean show) {
        if (null != nothingLayout) {
            nothingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置nothing提示内容
     */
    public void setNothingText(int text) {
        if (null != nothingTextView) {
            nothingTextView.setText(text);
        }
    }

    /**
     * 设置nothing提示内容
     */
    public void setNothingText(String text) {
        if (null != nothingTextView) {
            nothingTextView.setText(text);
        }
    }

    public void showNothing(int res) {
        setNothingText(res);
        displayNothing(true);
    }

    public void showNothing(String text) {
        setNothingText(text);
        displayNothing(true);
    }
}
