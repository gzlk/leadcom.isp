package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.hlk.hlklib.lib.inject.ViewId;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/26 22:11 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/26 22:11 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FooterViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_view_loading_more_progress)
    private CircleProgressBar progressBar;
    @ViewId(R.id.ui_tool_view_loading_more_text)
    private TextView warningTextView;

    public FooterViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
    }
}
