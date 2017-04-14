package com.gzlk.android.isp.holder;

import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.QRCodeFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>个人头像<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 16:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 16:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualHeaderViewHolder extends BaseViewHolder {

    public IndividualHeaderViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    @Click({R.id.ui_tool_individual_header_to_2d_code})
    private void click(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_individual_header_to_2d_code:
                openActivity(QRCodeFragment.class.getName(), "", false, false, true);
                break;
        }
    }
}
