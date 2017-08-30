package com.gzlk.android.isp.nim.action;

import android.app.Activity;
import android.content.Intent;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.activity.topic.TopicCreatorFragment;
import com.gzlk.android.isp.fragment.activity.topic.TopicListFragment;
import com.gzlk.android.isp.nim.constant.RequestCode;
import com.netease.nim.uikit.session.actions.BaseAction;

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

    public IssueAction() {
        super(R.drawable.nim_action_issue, R.string.ui_nim_action_issue);
    }

    @Override
    public void onClick() {
        // 打开通知列表页面
        int requestCode = makeRequestCode(RequestCode.REQ_TOPIC_LIST);
        TopicListFragment.open(getActivity(), getAccount(), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.REQ_TOPIC_LIST:
                    // 到议题创建页面
                    TopicCreatorFragment.open(getActivity(), makeRequestCode(RequestCode.REQ_TOPIC_NEW), getAccount());
                    break;
                case RequestCode.REQ_TOPIC_NEW:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
