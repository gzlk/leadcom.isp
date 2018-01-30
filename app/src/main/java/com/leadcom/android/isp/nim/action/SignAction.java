package com.leadcom.android.isp.nim.action;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.activity.sign.SignCreatorFragment;
import com.leadcom.android.isp.fragment.activity.sign.SignListFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.activity.sign.AppSigning;
import com.leadcom.android.isp.nim.constant.RequestCode;
import com.leadcom.android.isp.nim.constant.SigningNotifyType;
import com.leadcom.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

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

    /**
     * 签到
     */
    public SignAction() {
        super(R.drawable.nim_action_sign, R.string.ui_nim_action_sign);
    }

    @Override
    public void onClick() {
        // 打开发布签到页面
        int requestCode = makeRequestCode(RequestCode.REQ_SIGN_LIST);
        BaseActivity.openActivity(getActivity(), SignListFragment.class.getName(), getAccount(), requestCode, true, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.REQ_SIGN_LIST:
                    int code = makeRequestCode(RequestCode.REQ_SIGN_NEW);
                    BaseActivity.openActivity(getActivity(), SignCreatorFragment.class.getName(), getAccount(), code, true, true);
                    break;
                case RequestCode.REQ_SIGN_NEW:
                    // 群发通知
                    String result = BaseFragment.getResultedData(data);
                    LogHelper.log("SignAction", result);
                    AppSigning signing = Json.gson().fromJson(result, new TypeToken<AppSigning>() {
                    }.getType());
                    IMMessage message;
                    SigningNotifyAttachment attachment = new SigningNotifyAttachment();
                    attachment.setNotifyType(SigningNotifyType.NEW);
                    attachment.setTid(getAccount());
                    attachment.setSetupId(signing.getId());
                    attachment.setCustomId(signing.getId());
                    attachment.setTitle(signing.getTitle());
                    attachment.setContent(signing.getContent());
                    attachment.setAddress(signing.getSite());
                    attachment.setBeginTime(signing.getBeginDate());
                    attachment.setEndTime(signing.getEndDate());
                    message = MessageBuilder.createCustomMessage(getAccount(), SessionTypeEnum.Team, attachment.getContent(), attachment);
                    sendMessage(message);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
