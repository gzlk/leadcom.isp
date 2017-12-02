package com.leadcom.android.isp.nim.action;

import android.content.Intent;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppMinutesRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.common.OfficeOnlinePreviewFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.nim.constant.RequestCode;
import com.leadcom.android.isp.nim.model.extension.MinutesAttachment;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>网易云信会议记录Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 11:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 11:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MinutesAction extends BaseAction {

    /**
     * 会议纪要
     */
    public MinutesAction() {
        super(R.drawable.nim_action_minutes, R.string.ui_nim_action_minutes);
    }

    @Override
    public void onClick() {
        gotMinutes();
    }

    // 获取会议纪要文档
    private void gotMinutes() {
        Activity act = Activity.getByTid(getAccount());
        AppMinutesRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<String>() {
            @Override
            public void onResponse(String s, boolean success, String message) {
                super.onResponse(s, success, message);
                if (success) {
                    open(s);
                }
            }
        }).summary(act.getGroupId(), act.getId());
    }

    private void open(String url) {
        // 打开会议纪要详情页面
        int requestCode = makeRequestCode(RequestCode.REQ_MINUTES_DETAILS);
        String ext = Attachment.getExtension(url);
        OfficeOnlinePreviewFragment.open(getActivity(), requestCode, url, StringHelper.getString(R.string.ui_nim_action_minutes), ext, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == android.app.Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.REQ_MINUTES_DETAILS:
                    MinutesAttachment attachment = new MinutesAttachment();
                    attachment.setTitle(StringHelper.getString(R.string.ui_nim_action_minutes));
                    attachment.setUrl(BaseFragment.getResultedData(data));
                    IMMessage message = MessageBuilder.createCustomMessage(getAccount(), SessionTypeEnum.Team, StringHelper.getString(R.string.ui_nim_action_minutes), attachment);
                    message.setPushContent(StringHelper.getString(R.string.ui_activity_minutes_nim_view_holder_content));
                    sendMessage(message);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
