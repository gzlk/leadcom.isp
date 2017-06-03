package com.gzlk.android.isp.holder.common;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 02:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 02:59 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SystemMessageViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_system_message_icon_container)
    private CorneredView iconContainer;
    @ViewId(R.id.ui_holder_view_system_message_icon)
    private CustomTextView iconTextView;
    @ViewId(R.id.ui_holder_view_system_message_name)
    private TextView nameTextView;
    @ViewId(R.id.ui_holder_view_system_message_time)
    private TextView timeTextView;
    @ViewId(R.id.ui_holder_view_system_message_description)
    private TextView descTextView;

    public SystemMessageViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(String string){

    }
}
