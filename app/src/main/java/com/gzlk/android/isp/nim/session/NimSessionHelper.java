package com.gzlk.android.isp.nim.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.activity.ActivityPropertiesFragment;
import com.gzlk.android.isp.fragment.individual.UserPropertyFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.nim.action.CameraAction;
import com.gzlk.android.isp.nim.action.FileAction;
import com.gzlk.android.isp.nim.action.ImageAction;
import com.gzlk.android.isp.nim.action.IssueAction;
import com.gzlk.android.isp.nim.action.LocationAction;
import com.gzlk.android.isp.nim.action.MinutesAction;
import com.gzlk.android.isp.nim.action.NoticeAction;
import com.gzlk.android.isp.nim.action.SignAction;
import com.gzlk.android.isp.nim.action.SurveyAction;
import com.gzlk.android.isp.nim.action.VideoCaptureAction;
import com.gzlk.android.isp.nim.action.VideoChooseAction;
import com.gzlk.android.isp.nim.action.VoteAction;
import com.gzlk.android.isp.nim.model.extension.BaseAttachmentParser;
import com.gzlk.android.isp.nim.model.extension.NoticeAttachment;
import com.gzlk.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.gzlk.android.isp.nim.model.extension.VoteAttachment;
import com.gzlk.android.isp.nim.model.notification.NimMessageParser;
import com.gzlk.android.isp.nim.viewholder.MsgViewHolderFile;
import com.gzlk.android.isp.nim.viewholder.MsgViewHolderNotice;
import com.gzlk.android.isp.nim.viewholder.MsgViewHolderSignNotify;
import com.gzlk.android.isp.nim.viewholder.MsgViewHolderTip;
import com.gzlk.android.isp.nim.viewholder.MsgViewHolderVote;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.SessionEventListener;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.helper.MessageHelper;
import com.netease.nim.uikit.session.module.MsgForwardFilter;
import com.netease.nim.uikit.session.module.MsgRevokeFilter;
import com.netease.nim.uikit.team.model.TeamExtras;
import com.netease.nim.uikit.team.model.TeamRequestCode;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>网易云信Session<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/06 01:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/06 01:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NimSessionHelper {

    public static void init() {

        // 注册位置提供者
        NimUIKit.setLocationProvider(new BaiduLocationProvider());

        // 注册各种消息的解析器
        registerParsers();

        // 注册各种聊天内容ViewHolder
        registerViewHolders();

        // 设置会话中点击事件响应处理
        setSessionListener();

        // 注册消息转发过滤器
        registerMsgForwardFilter();

        // 注册消息撤回过滤器
        registerMsgRevokeFilter();

        // 注册消息撤回监听器
        registerMsgRevokeObserver();

        // 单聊UI定制
        NimUIKit.setCommonP2PSessionCustomization(getP2pCustomization());

        // 群聊UI定制
        NimUIKit.setCommonTeamSessionCustomization(getTeamCustomization());
    }

    /**
     * 设置当前登录者的信息
     */
    public static void setAccount(String account) {
        NimUIKit.setAccount(account);
    }

    private static void registerParsers() {
        // 注册自定义通知消息解析器
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new NimMessageParser());
        // 注册通消息解析器
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new BaseAttachmentParser());
    }

    private static void registerViewHolders() {
        // 文件显示
        NimUIKit.registerMsgItemViewHolder(FileAttachment.class, MsgViewHolderFile.class);
        // 通知
        NimUIKit.registerMsgItemViewHolder(NoticeAttachment.class, MsgViewHolderNotice.class);
        // 签到提醒通知
        NimUIKit.registerMsgItemViewHolder(SigningNotifyAttachment.class, MsgViewHolderSignNotify.class);
        // 投票
        NimUIKit.registerMsgItemViewHolder(VoteAttachment.class, MsgViewHolderVote.class);
        // 提示类消息
        NimUIKit.registerTipMsgViewHolder(MsgViewHolderTip.class);
    }

    /**
     * 设置会话中点击事件响应处理
     */
    private static void setSessionListener() {
        SessionEventListener listener = new SessionEventListener() {
            @Override
            public void onAvatarClicked(Context context, IMMessage message) {
                // 一般用于打开用户资料页面
                BaseActivity.openActivity(context, UserPropertyFragment.class.getName(), message.getFromAccount(), false, false, true);
            }

            @Override
            public void onAvatarLongClicked(Context context, IMMessage message) {
                // 一般用于群组@功能，或者弹出菜单，做拉黑，加好友等功能
            }
        };

        NimUIKit.setSessionListener(listener);
    }


    /**
     * 消息转发过滤器
     */
    private static void registerMsgForwardFilter() {
        NimUIKit.setMsgForwardFilter(new MsgForwardFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                if (message.getDirect() == MsgDirectionEnum.In
                        && (message.getAttachStatus() == AttachStatusEnum.transferring
                        || message.getAttachStatus() == AttachStatusEnum.fail)) {
                    // 接收到的消息，附件没有下载成功，不允许转发
                    return true;
                }
//                else if (message.getMsgType() == MsgTypeEnum.custom && message.getAttachment() != null
//                        && (message.getAttachment() instanceof SnapChatAttachment
//                        || message.getAttachment() instanceof RTSAttachment)) {
//                    // 白板消息和阅后即焚消息 不允许转发
//                    return true;
//                }
                return false;
            }
        });
    }

    /**
     * 消息撤回过滤器
     */
    private static void registerMsgRevokeFilter() {
        NimUIKit.setMsgRevokeFilter(new MsgRevokeFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                if (message.getAttachment() != null) {
                    // 视频通话消息和白板消息 不允许撤回
                    return true;
                } else if (Cache.cache().userId.equals(message.getSessionId())) {
                    // 发给我的电脑 不允许撤回
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 注册消息撤回监听器
     */
    private static void registerMsgRevokeObserver() {
        NIMClient.getService(MsgServiceObserve.class).observeRevokeMessage(new Observer<IMMessage>() {
            @Override
            public void onEvent(IMMessage message) {
                if (message == null) {
                    return;
                }

                MessageHelper.getInstance().onRevokeMessage(message);
            }
        }, true);
    }

    public static void startP2PSession(Context context, String account) {
        startP2PSession(context, account, null);
    }

    public static void startP2PSession(Context context, String account, IMMessage anchor) {
        if (!Cache.cache().userId.equals(account)) {
            // 和别人聊天
            NimUIKit.startP2PSession(context, account, anchor);
        } else {
            // 和我的电脑聊天
            NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getMyP2pCustomization(), anchor);
        }
    }

    public static void startTeamSession(Context context, String tid) {
        startTeamSession(context, tid, null);
    }

    public static void startTeamSession(Context context, String tid, IMMessage anchor) {
        // 通知中的 RecentContact 对象的未读数为0
        NIMClient.getService(MsgService.class).clearUnreadCount(tid, SessionTypeEnum.Team);
        NimUIKit.startTeamSession(context, tid, anchor);
    }

    // 打开群聊界面(用于 UIKIT 中部分界面跳转回到指定的页面)
    public static void startTeamSession(Context context, String tid, Class<? extends Activity> backToClass, IMMessage anchor) {
        NimUIKit.startChatting(context, tid, SessionTypeEnum.Team, getTeamCustomization(), backToClass, anchor);
    }

    // 单聊界面定制
    private static SessionCustomization p2pCustomization;
    // 群聊界面定制
    private static SessionCustomization teamCustomization;
    private static SessionCustomization myP2pCustomization;

    private static ArrayList<BaseAction> getActions(SessionTypeEnum type) {
        // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了

        ArrayList<BaseAction> actions = new ArrayList<>();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                actions.add(new AVChatAction(AVChatType.AUDIO));
//                actions.add(new AVChatAction(AVChatType.VIDEO));
//            }
        // 相册选择图片
        actions.add(new ImageAction());
        // 相机拍摄照片
        actions.add(new CameraAction());
        // 相机录制视频
        //actions.add(new VideoCaptureAction());
        // 相册选择视频
        //actions.add(new VideoChooseAction());
        // 跟电脑对话时不需要发送位置
        if (type != SessionTypeEnum.System) {
            actions.add(new LocationAction());
        }
        actions.add(new FileAction());
        if (type == SessionTypeEnum.Team) {
            actions.add(new NoticeAction());
        }
        if (type == SessionTypeEnum.Team) {
            //actions.add(new SurveyAction());
            //actions.add(new IssueAction());
            actions.add(new VoteAction());
            actions.add(new SignAction());
            //actions.add(new MinutesAction());
        }
        return actions;
    }

    // 定制化单聊界面。如果使用默认界面，返回null即可
    private static SessionCustomization getP2pCustomization() {
        if (p2pCustomization == null) {
            p2pCustomization = new SessionCustomization() {
                // 由于需要Activity Result， 所以重载该函数。
                @Override
                public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
                    super.onActivityResult(activity, requestCode, resultCode, data);

                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return null;//new StickerAttachment(category, item);
                }
            };

            // 背景
//            p2pCustomization.backgroundColor = Color.BLUE;
//            p2pCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
//            p2pCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
//            p2pCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk"

            p2pCustomization.actions = getActions(SessionTypeEnum.P2P);
            p2pCustomization.withSticker = true;

            // 定制ActionBar右边的按钮，可以加多个
//            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.P2P);
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
//
//            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//                    MessageInfoActivity.startActivity(context, sessionId); //打开聊天信息
//                }
//            };
//
//            infoButton.iconId = R.drawable.nim_ic_message_actionbar_p2p_add;
//
//            buttons.add(cloudMsgButton);
//            buttons.add(infoButton);
//            p2pCustomization.buttons = buttons;
        }

        return p2pCustomization;
    }

    /**
     * 我的电脑聊天界面
     */
    private static SessionCustomization getMyP2pCustomization() {
        if (myP2pCustomization == null) {
            myP2pCustomization = new SessionCustomization() {
                // 由于需要Activity Result， 所以重载该函数。
                @Override
                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                    if (requestCode == TeamRequestCode.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                        String result = data.getStringExtra(TeamExtras.RESULT_EXTRA_REASON);
                        if (result == null) {
                            return;
                        }
                        if (result.equals(TeamExtras.RESULT_EXTRA_REASON_CREATE)) {
                            String tid = data.getStringExtra(TeamExtras.RESULT_EXTRA_DATA);
                            if (TextUtils.isEmpty(tid)) {
                                return;
                            }

                            startTeamSession(activity, tid);
                            activity.finish();
                        }
                    }
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return null;//new StickerAttachment(category, item);
                }
            };

            // 背景
//            p2pCustomization.backgroundColor = Color.BLUE;
//            p2pCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
//            p2pCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
//            p2pCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk"

            myP2pCustomization.actions = getActions(SessionTypeEnum.System);
            myP2pCustomization.withSticker = false;
            // 定制ActionBar右边的按钮，可以加多个
//            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.P2P);
//                }
//            };
//
//            cloudMsgButton.iconId = R.drawable.ic_action_access_time;
//
//            buttons.add(cloudMsgButton);
//            myP2pCustomization.buttons = buttons;
        }
        return myP2pCustomization;
    }

    private static SessionCustomization getTeamCustomization() {
        if (teamCustomization == null) {
            teamCustomization = new SessionCustomization() {
                @Override
                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                    if (requestCode == TeamRequestCode.REQUEST_CODE) {
                        if (resultCode == Activity.RESULT_OK) {
                            String reason = data.getStringExtra(TeamExtras.RESULT_EXTRA_REASON);
                            boolean finish = reason != null && (reason.equals(TeamExtras
                                    .RESULT_EXTRA_REASON_DISMISS) || reason.equals(TeamExtras.RESULT_EXTRA_REASON_QUIT));
                            if (finish) {
                                activity.finish(); // 退出or解散群直接退出多人会话
                            }
                        }
                    }
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return null;//new StickerAttachment(category, item);
                }
            };

            teamCustomization.actions = getActions(SessionTypeEnum.Team);

            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId) {
//                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.Team);
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.ic_action_access_time;

            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
                @Override
                public void onClick(Context context, View view, String sessionId) {
                    Team team = TeamDataCache.getInstance().getTeamById(sessionId);
                    if (team != null && team.isMyTeam()) {
                        // 打开群组属性页
                        openGroupPropertyInfo(context, sessionId);
                        //NimUIKit.startTeamInfo(context, sessionId);
                    } else {
                        Toast.makeText(context, R.string.team_invalid_tip, Toast.LENGTH_SHORT).show();
                    }
                }
            };
            infoButton.iconId = R.drawable.ic_action_group;

//            buttons.add(cloudMsgButton);
            buttons.add(infoButton);
            teamCustomization.buttons = buttons;

            teamCustomization.withSticker = false;

            teamCustomization.buttonSelectorResources = R.drawable.nim_nim_action_bar_button_selector;
        }

        return teamCustomization;
    }

    private static void openGroupPropertyInfo(Context context, String sessionId) {
        com.gzlk.android.isp.model.activity.Activity act =
                new Dao<>(com.gzlk.android.isp.model.activity.Activity.class)
                        .querySingle(com.gzlk.android.isp.model.activity.Activity.Field.NimId, sessionId);
        if (null != act) {
            BaseActivity.openActivity(context, ActivityPropertiesFragment.class.getName(), StringHelper.format("%s,%s", act.getId(), sessionId), false, false, true);
        } else {
            // 本地找不到活动记录则按照网易自己的方式打开群属性页
            NimUIKit.startTeamInfo(context, sessionId);
        }
    }
}
