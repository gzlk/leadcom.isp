package com.gzlk.android.isp.holder.organization;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.model.organization.Squad;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>带有删除按钮的小组ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/01 20:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/01 20:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SquadDeleteableViewHolder extends SimpleClickableViewHolder {

    @ViewId(R.id.ui_tool_view_contact_button2)
    private TextView deleteView;

    public SquadDeleteableViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
    }

    public void showContent(Squad squad) {
        super.showContent(squad);
    }

    public void showDelete(boolean shown) {
        deleteView.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_tool_view_contact_button2})
    private void elementClick(View view) {
        if (view.getId() == R.id.ui_tool_view_contact_button2) {
            // 删除
            if (null != mOnHandlerBoundDataListener) {
                mOnHandlerBoundDataListener.onHandlerBoundData(SquadDeleteableViewHolder.this);
            }
        }
    }
}
