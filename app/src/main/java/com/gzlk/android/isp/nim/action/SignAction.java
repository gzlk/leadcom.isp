package com.gzlk.android.isp.nim.action;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.fragment.activity.sign.SignListFragment;
import com.netease.nim.uikit.session.actions.BaseAction;

/**
 * <b>功能描述：</b>网易云信签到Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 11:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 11:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignAction extends BaseAction {

    public SignAction() {
        super(R.drawable.nim_action_sign, R.string.ui_nim_action_sign);
    }

    @Override
    public void onClick() {
        // 打开发布签到页面
        BaseActivity.openActivity(getActivity(), SignListFragment.class.getName(), getAccount(), 0, true, true);
    }
}
