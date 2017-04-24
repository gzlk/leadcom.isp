package com.gzlk.android.isp.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredView;

/**
 * <b>功能描述：</b>添加附件的holder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/16 15:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/16 15:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class AttachItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_attach_item)
    private CorneredView container;

    public AttachItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public AttachItemViewHolder setSize(int width, int height) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
        params.width = width;
        params.height = height;
        itemView.setLayoutParams(params);
        return this;
    }

    public AttachItemViewHolder setOnViewHolderClickListener(OnViewHolderClickListener l) {
        super.addOnViewHolderClickListener(l);
        return this;
    }

    public void showContent(boolean shown) {
        container.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_holder_view_attach_item})
    private void click(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(0);
        }
    }
}
