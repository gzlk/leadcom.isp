package com.leadcom.android.isp.nim.action;

import android.content.Intent;

import com.leadcom.android.isp.R;
import com.netease.nim.uikit.business.session.actions.BaseAction;

/**
 * <b>功能描述：</b>网易云信议题Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 11:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 11:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IssueAction extends BaseAction {

    /**
     * 议题
     */
    public IssueAction() {
        super(R.drawable.nim_action_issue, R.string.ui_nim_action_issue);
    }

    @Override
    public void onClick() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
