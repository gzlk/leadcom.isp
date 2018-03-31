package com.leadcom.android.isp.nim.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.fragment.main.SystemMessageFragment;
import com.leadcom.android.isp.fragment.talk.TalkTeamPropertyFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.publishable.Collectable;
import com.leadcom.android.isp.nim.action.CameraAction;
import com.leadcom.android.isp.nim.action.FileAction;
import com.leadcom.android.isp.nim.action.ImageAction;
import com.leadcom.android.isp.nim.action.IssueAction;
import com.leadcom.android.isp.nim.action.LocationAction;
import com.leadcom.android.isp.nim.action.NoticeAction;
import com.leadcom.android.isp.nim.action.SignAction;
import com.leadcom.android.isp.nim.action.VoteAction;
import com.leadcom.android.isp.nim.activity.VideoPlayerActivity;
import com.leadcom.android.isp.nim.model.extension.ArchiveAttachment;
import com.leadcom.android.isp.nim.model.extension.BaseAttachmentParser;
import com.leadcom.android.isp.nim.model.extension.MinutesAttachment;
import com.leadcom.android.isp.nim.model.extension.NoticeAttachment;
import com.leadcom.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.leadcom.android.isp.nim.model.extension.StickerAttachment;
import com.leadcom.android.isp.nim.model.extension.TopicAttachment;
import com.leadcom.android.isp.nim.model.extension.VoteAttachment;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderArchive;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderFile;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderMinutes;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderNotice;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderSignNotify;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderSticker;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderTip;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderTopic;
import com.leadcom.android.isp.nim.viewholder.MsgViewHolderVote;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.OnSessionMessageViewHolderClick;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.session.SessionEventListener;
import com.netease.nim.uikit.api.wrapper.NimMessageRevokeObserver;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.module.MsgForwardFilter;
import com.netease.nim.uikit.business.session.module.MsgRevokeFilter;
import com.netease.nim.uikit.business.team.model.TeamExtras;
import com.netease.nim.uikit.business.team.model.TeamRequestCode;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        NimUIKit.setLocationProvider(new HLKLocationProvider());

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

        // 文件提供者
        NimUIKit.setFileProviderAuthority(StringHelper.format("%s.fileProvider", BuildConfig.APPLICATION_ID));

        // 视频播放
        NimUIKit.setOnSessionMessageViewHolderClick(onSessionMessageViewHolderClick);
    }

    /**
     * 设置当前登录者的信息
     */
    public static void setAccount(String account) {
        NimUIKit.setAccount(account);
    }

    private static void registerParsers() {
        // 注册自定义通知消息解析器
        //NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new NimMessageParser());
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
        // 发布的新议题消息
        NimUIKit.registerMsgItemViewHolder(TopicAttachment.class, MsgViewHolderTopic.class);
        // 会议纪要
        NimUIKit.registerMsgItemViewHolder(MinutesAttachment.class, MsgViewHolderMinutes.class);
        // 分享的文档
        NimUIKit.registerMsgItemViewHolder(ArchiveAttachment.class, MsgViewHolderArchive.class);
        // 贴图消息
        NimUIKit.registerMsgItemViewHolder(StickerAttachment.class, MsgViewHolderSticker.class);
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
                App.openUserInfo(context, message.getFromAccount());
            }

            @Override
            public void onAvatarLongClicked(Context context, IMMessage message) {
                // 一般用于群组@功能，或者弹出菜单，做拉黑，加好友等功能
            }

            @Override
            public void onAckMsgClicked(Context context, IMMessage message) {
                // 已读回执事件处理，用于群组的已读回执事件的响应，弹出消息已读详情
                //AckMsgInfoActivity.start(context, message);
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
                if (message.getDirect() == MsgDirectionEnum.In &&
                        (message.getAttachStatus() == AttachStatusEnum.transferring || message.getAttachStatus() == AttachStatusEnum.fail)) {
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
                // 发给我的电脑 不允许撤回
                return Cache.cache().userId.equals(message.getSessionId());
            }
        });
    }

    /**
     * 注册消息撤回监听器
     */
    private static void registerMsgRevokeObserver() {
        NIMClient.getService(MsgServiceObserve.class).observeRevokeMessage(new NimMessageRevokeObserver(), true);
    }

    public static void startP2PSession(Context context, String account) {
        startP2PSession(context, account, null);
    }

    private static void startP2PSession(Context context, String account, IMMessage anchor) {
        if (!Cache.cache().userId.equals(account)) {
            // 和别人聊天
            NimUIKit.startP2PSession(context, account, anchor);
        } else {
            // 和我的电脑聊天
            //NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getMyP2pCustomization(), anchor);
        }
    }

    public static void startTeamSession(Context context, String tid) {
        startTeamSession(context, tid, null);
    }

    public static void startTeamSession(Context context, String tid, IMMessage anchor) {
        // 通知中的 RecentContact 对象的未读数为0
        NIMClient.getService(MsgService.class).clearUnreadCount(tid, SessionTypeEnum.Team);
        //NimUIKit.startTeamSession(context, tid, anchor);
        startTeamSession(context, tid, null, anchor);
    }

    // 打开群聊界面(用于 UIKIT 中部分界面跳转回到指定的页面)
    public static void startTeamSession(Context context, String tid, Class<? extends Activity> backToClass, IMMessage anchor) {
        //Model model = getObject(tid);
        //if (null != model && model instanceof AppTopic) {
        //    NimUIKit.startChatting(context, tid, SessionTypeEnum.Team, getTopicCustomization(), backToClass, anchor);
        //} else {
        NimUIKit.startChatting(context, tid, SessionTypeEnum.Team, getTeamCustomization(), backToClass, anchor);
        //}
    }

    // 单聊界面定制
    private static SessionCustomization p2pCustomization;
    // 群聊界面定制
    private static SessionCustomization teamCustomization;

    private static ArrayList<BaseAction> getActions(SessionTypeEnum type, boolean topic) {
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
        if (!Cache.isReleasable()) {
            // 相机录制视频
            //actions.add(new VideoCaptureAction());
            // 相册选择视频
            //actions.add(new VideoChooseAction());
        }
        // 跟电脑对话时不需要发送位置
        if (type != SessionTypeEnum.System) {
            actions.add(new LocationAction());
        }
        actions.add(new FileAction());
        if (type == SessionTypeEnum.Team) {
            actions.add(new NoticeAction());
            actions.add(new VoteAction());
            actions.add(new SignAction());
            //actions.add(new BlankAction());
            //actions.add(new SurveyAction());
            //actions.add(new MinutesAction());
            if (!topic) {
                actions.add(new IssueAction());
            }
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
                    // 查看主窗口是否存在，不存在时打开
                    if (!MainActivity.innerOpen) {
                        MainActivity.start(activity);
                    }
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return new StickerAttachment(category, item);
                }
            };

            p2pCustomization.actions = getActions(SessionTypeEnum.P2P, false);

            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
            // 用户信息入口
            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
                @Override
                public void onClick(Context context, View view, String sessionId) {
                    // 打开用户点对点单聊属性页面
                    TalkTeamPropertyFragment.open(context, sessionId, true);
//                    NimUserInfo info = NimUserInfoCache.getInstance().getUserInfo(sessionId);
//                    if (null != info) {
//                        UserPropertyFragment.open(context, info.getAccount());
//                    } else {
//                        ToastHelper.make().showMsg("用户不存在");
//                    }
                }
            };
            infoButton.iconId = R.drawable.ic_action_person;
            buttons.add(infoButton);
            p2pCustomization.buttons = buttons;

            p2pCustomization.withSticker = true;
            p2pCustomization.buttonSelectorResources = R.drawable.nim_action_bar_button_selector;
        }

        return p2pCustomization;
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
                    return new StickerAttachment(category, item);
                }
            };

            teamCustomization.actions = getActions(SessionTypeEnum.Team, false);

            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
            SessionCustomization.OptionsButton chatButton = new SessionCustomization.OptionsButton() {
                @Override
                public void onClick(Context context, View view, String sessionId) {
                    SystemMessageFragment.open(context);
                }
            };
            chatButton.iconId = R.drawable.ic_action_chat;

            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
                @Override
                public void onClick(Context context, View view, String sessionId) {
                    Team team = TeamDataCache.getInstance().getTeamById(sessionId);
                    if (team != null && team.isMyTeam()) {
                        // 打开群组属性页
                        TalkTeamPropertyFragment.open(context, team.getId(), false);
                    } else {
                        Toast.makeText(context, R.string.team_invalid_tip, Toast.LENGTH_SHORT).show();
                    }
                }
            };
            infoButton.iconId = R.drawable.ic_action_group;

            buttons.add(infoButton);
            //buttons.add(chatButton);
            teamCustomization.buttons = buttons;

            teamCustomization.withSticker = true;

            teamCustomization.buttonSelectorResources = R.drawable.nim_action_bar_button_selector;
        }

        return teamCustomization;
    }

    private static OnSessionMessageViewHolderClick onSessionMessageViewHolderClick = new OnSessionMessageViewHolderClick() {
        @Override
        public void onClick(Context context, IMMessage message) {
            // 群聊收藏准备
            Collectable.resetSessionCollectionParams(message);
            if (message.getAttachment() instanceof ImageAttachment) {
                // 查询当前session的所有图片聊天记录
                queryNimImageMessages(context, message);
            } else {
                VideoAttachment video = (VideoAttachment) message.getAttachment();
                //VideoPlayerActivity.open(context, video.getDisplayName(), video.getUrl());
                VideoPlayerActivity.start(context, video.getUrl());
            }
        }
    };

    private static void queryNimImageMessages(final Context context, final IMMessage message) {

        IMMessage anchor = MessageBuilder.createEmptyMessage(message.getSessionId(), message.getSessionType(), 0);

        NIMClient.getService(MsgService.class).queryMessageListByType(MsgTypeEnum.image, anchor, Integer.MAX_VALUE).setCallback(new RequestCallback<List<IMMessage>>() {
            @Override
            public void onSuccess(List<IMMessage> param) {
                Collections.reverse(param);
                ArrayList<String> list = new ArrayList<>();
                int selected = -1;
                for (int i = 0, size = param.size(); i < size; i++) {
                    IMMessage msg = param.get(i);
                    if (selected < 0) {
                        if (msg.getUuid().equals(message.getUuid())) {
                            selected = i;
                        }
                    }
                    list.add(((ImageAttachment) msg.getAttachment()).getUrl());
                }
                ImageViewerFragment.open(context, selected, list);
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.make().showMsg("查询消息失败：" + code);
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }
}
