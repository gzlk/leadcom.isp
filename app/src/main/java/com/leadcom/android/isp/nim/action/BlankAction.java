package com.leadcom.android.isp.nim.action;

import com.leadcom.android.isp.R;
import com.netease.nim.uikit.business.session.actions.BaseAction;

/**
 * <b>功能描述：</b>空白按钮<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/01 21:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/01 21:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BlankAction extends BaseAction {

    /**
     * 空白Action
     */
    public BlankAction() {
        super(R.drawable.nim_action_blank, R.string.ui_nim_action_blank);
    }

    @Override
    public void onClick() {

    }
}
