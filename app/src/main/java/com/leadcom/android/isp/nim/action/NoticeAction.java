package com.leadcom.android.isp.nim.action;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.activity.notice.NoticeCreatorFragment;
import com.leadcom.android.isp.fragment.activity.notice.NoticeListFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.nim.constant.RequestCode;
import com.leadcom.android.isp.nim.model.extension.NoticeAttachment;
import com.netease.nim.uikit.business.session.actions.BaseAction;
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

    /**
     * 通知
     */
    public NoticeAction() {
        super(R.drawable.nim_action_notice, R.string.ui_nim_action_notice);
    }

    @Override
    public void onClick() {
        // 打开通知列表页面
        int requestCode = makeRequestCode(RequestCode.REQ_NOTICE_LIST);
        NoticeListFragment.open(getActivity(), requestCode, getAccount(), true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.REQ_NOTICE_LIST:
                    // 要创建新的通知
                    int code = makeRequestCode(RequestCode.REQ_NOTICE_NEW);
                    NoticeCreatorFragment.open(getActivity(), getAccount(), code);
                    break;
                case RequestCode.REQ_NOTICE_NEW:
                    // 群发通知
                    String result = BaseFragment.getResultedData(data);
                    LogHelper.log("NoticeAction", result);
                    IMMessage message;
                    NoticeAttachment notice = Json.gson().fromJson(result, new TypeToken<NoticeAttachment>() {
                    }.getType());
                    notice.setCustomId(notice.getId());
                    message = MessageBuilder.createCustomMessage(getAccount(), SessionTypeEnum.Team, notice.getTitle(), notice);
                    sendMessage(message);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
