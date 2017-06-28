package com.gzlk.android.isp.nim.viewholder;

import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.activity.sign.SignFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.nim.callback.SignCallback;
import com.gzlk.android.isp.nim.constant.SigningNotifyType;
import com.gzlk.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>网易云信对话列表里显示签到提醒<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 15:53 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 15:53 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MsgViewHolderSignNotify extends MsgViewHolderBase {

    private CorneredView iconContainer;
    private CustomTextView notifyIcon;
    private TextView contentTextView;

    public MsgViewHolderSignNotify(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_msg_view_holder_sign_notify;
    }

    @Override
    protected void inflateContentView() {
        iconContainer = (CorneredView) view.findViewById(R.id.message_item_sign_notify_icon_container);
        notifyIcon = (CustomTextView) view.findViewById(R.id.message_item_sign_notify_icon);
        contentTextView = (TextView) view.findViewById(R.id.message_item_sign_notify_title_label);
    }

    private SigningNotifyAttachment notify;

    private int getIcon() {
        switch (notify.getNotifyType()) {
            case SigningNotifyType.ALMOST_START:
                return R.string.ui_icon_material_timer;
            case SigningNotifyType.NEW:
            case SigningNotifyType.STARTED:
                return R.string.ui_icon_material_location_sign;
            case SigningNotifyType.ALMOST_END:
                return R.string.ui_icon_material_timer_1;
            default:
                return R.string.ui_icon_material_location_off;
        }
    }

    private int getColor() {
        switch (notify.getNotifyType()) {
            case SigningNotifyType.ALMOST_START:
                return R.color.color_faaa2d;
            case SigningNotifyType.NEW:
            case SigningNotifyType.STARTED:
                return R.color.color_3eb135;
            case SigningNotifyType.ALMOST_END:
                return R.color.colorCaution;
            default:
                return R.color.textColor;
        }
    }

    @Override
    protected void bindContentView() {
        notify = (SigningNotifyAttachment) message.getAttachment();
        notifyIcon.setText(getIcon());
        iconContainer.setBackground(context.getResources().getColor(getColor()));
        contentTextView.setText(notify.getContent());
    }

    @Override
    protected void onItemClick() {
        // 打开签到页面
        String params = StringHelper.format("%s,%s,", notify.getTid(), notify.getSetupId());
        SignFragment.callback = new SignCallback() {
            @Override
            public void onSuccess() {
                // 签到成功，群发tip
                IMMessage msg = MessageBuilder.createTipMessage(message.getSessionId(), message.getSessionType());
                msg.setFromAccount(Cache.cache().userId);
                String nick = "";
                if (message.getSessionType() == SessionTypeEnum.Team) {
                    nick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(message.getSessionId(), message.getFromAccount());
                } else if (message.getSessionType() == SessionTypeEnum.P2P) {
                    nick = message.getFromAccount().equals(NimUIKit.getAccount()) ? "你" : "对方";
                }
//                if (msg.getSessionType() == SessionTypeEnum.Team) {
//                    nick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(msg.getSessionId(), msg.getFromAccount());
//                }
                msg.setContent(nick + "已签到");
                msg.setStatus(MsgStatusEnum.success);

                CustomMessageConfig config = new CustomMessageConfig();
                config.enableUnreadCount = false;
                msg.setConfig(config);
                NIMClient.getService(MsgService.class).saveMessageToLocalEx(msg, false, msg.getTime());
            }
        };
        BaseActivity.openActivity(context, SignFragment.class.getName(), params, true, false);
    }
}
