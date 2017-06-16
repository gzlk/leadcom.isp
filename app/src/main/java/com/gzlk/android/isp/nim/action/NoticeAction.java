package com.gzlk.android.isp.nim.action;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.fragment.activity.notice.NoticeCreatorFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.nim.constant.RequestCode;
import com.gzlk.android.isp.nim.model.extension.NoticeAttachment;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>网易云信发送通知Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 10:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 10:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NoticeAction extends BaseAction {

    public NoticeAction() {
        super(R.drawable.nim_action_notice, R.string.ui_nim_action_notice);
    }

    @Override
    public void onClick() {
        // 打开发布通知页面
        int requestCode = makeRequestCode(RequestCode.REQ_NOTICE);
        BaseActivity.openActivity(getActivity(), NoticeCreatorFragment.class.getName(), getAccount(), requestCode, true, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.REQ_NOTICE) {
            // 群发通知
            String result = BaseFragment.getResultedData(data);
            LogHelper.log("NoticeAction", result);
            IMMessage message;
            NoticeAttachment notice = Json.gson().fromJson(result, new TypeToken<NoticeAttachment>() {
            }.getType());
            message = MessageBuilder.createCustomMessage(getAccount(), SessionTypeEnum.Team, notice.getTitle(), notice);
            sendMessage(message);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
