package com.leadcom.android.isp.fragment.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.ActRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.activity.notice.NoticeListFragment;
import com.leadcom.android.isp.fragment.common.BaseTransparentPropertyFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BasePopupInputSupportFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.organization.SimpleMemberViewHolder;
import com.leadcom.android.isp.holder.individual.UserHeaderBigViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.ToggleableViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.nim.activity.SessionHistoryActivity;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.List;

/**
 * <b>功能描述：</b>活动属性页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 16:05 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 16:05 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityPropertiesFragment extends BaseTransparentPropertyFragment {

    private static final String PARAM_SESSION_ID = "apf_session_id";

    public static ActivityPropertiesFragment newInstance(String params) {
        ActivityPropertiesFragment apf = new ActivityPropertiesFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 活动的sessionId
        bundle.putString(PARAM_SESSION_ID, strings[1]);
        apf.setArguments(bundle);
        return apf;
    }

    public static void open(Context context, String actId, String tid) {
        BaseActivity.openActivity(context, ActivityPropertiesFragment.class.getName(), format("%s,%s", actId, tid), false, false, true);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mSessionId = bundle.getString(PARAM_SESSION_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_SESSION_ID, mSessionId);
    }

    private String mSessionId = "";
    private String[] items;
    private PropertiesAdapter mAdapter;

    @Override
    public void doingInResume() {
        super.doingInResume();
        bottomButton.setText(R.string.ui_activity_property_button_text);
        initializeAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 默认先隐藏底部按钮
        bottomButton.setVisibility(View.GONE);
    }

    @Override
    protected void onBottomButtonClicked() {
        warningExit();
    }

    private void warningExit() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_property_exit_warning, R.string.ui_base_text_yes, R.string.ui_base_text_think_again, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                exitActivity();
                return true;
            }
        }, null);
    }

    private void exitActivity() {
        // 退出活动
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    new Dao<>(Activity.class).delete(mQueryId);
                    ToastHelper.make().showMsg(R.string.ui_activity_property_exited);
                    finish();
                }
            }
        }).activityExit(mQueryId);
    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingActivity();
    }

    private void fetchingActivity() {
        displayLoading(true);
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    if (null != activity) {
                        initializeActivity(activity);
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_details_invalid_parameter);
                        finish();
                    }
                }
                stopRefreshing();
                displayLoading(false);
            }
        }).findFromRemote(mQueryId, ActRequest.ACT_OPE_MEMBERS);
    }

    private void initializeActivity(Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_property_items);
        }
        boolean isMe = !isEmpty(activity.getCreatorId()) && activity.getCreatorId().equals(Cache.cache().userId);
        // 我创建的活动不能退出，只能在活动管理里结束
        bottomButton.setVisibility(isMe ? View.GONE : View.VISIBLE);
        int index = 0;
        for (String string : items) {
            if (string.startsWith("0|")) {
                mAdapter.update(activity);
                index++;
                continue;
            }
            String text;
            switch (index) {
                case 1:
                    // 活动成员
                    //int size = null == activity.getMemberIdArray() ? 0 : activity.getMemberIdArray().size();
                    text = format(string, 0);
                    break;
                case 2:
                    // 活动标题
                    text = format(string, activity.getTitle());
                    break;
                case 5:
                    // 消息免打扰
                    text = format(string, 0);
                    break;
                default:
                    text = string;
                    break;
            }
            SimpleClickableItem item = new SimpleClickableItem(text);
            mAdapter.update(item);
            index++;
        }
        Handler().post(new Runnable() {
            @Override
            public void run() {
                resetNotificationStatus();
            }
        });
    }

    private void resetNotificationStatus() {
        Team team = TeamDataCache.getInstance().getTeamById(mSessionId);
        if (null != team) {
            // 静音=1，反之=0
            resetNotificationStatus(team.getMessageNotifyType() == TeamMessageNotifyTypeEnum.Mute);
        } else {
            TeamDataCache.getInstance().fetchTeamById(mSessionId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result, int code) {
                    if (success && result != null) {
                        resetNotificationStatus(result.getMessageNotifyType() == TeamMessageNotifyTypeEnum.Mute);
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_property_not_exist);
                    }
                }
            });
        }
    }

    /**
     * 重设消息免打扰状态
     *
     * @param mute true=静音开启（免打扰）
     */
    private void resetNotificationStatus(boolean mute) {
        String string = items[5];
        string = format(string, mute ? 1 : 0);
        SimpleClickableItem item = new SimpleClickableItem(string);
        mAdapter.update(item);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new PropertiesAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        fetchingActivity();
    }

    private static final int REQUEST_NAME = ACTIVITY_BASE_REQUEST + 10;
    private static final int REQUEST_PICK_ONE = REQUEST_NAME + 1;

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Activity activity = (Activity) mAdapter.get(0);
            switch (index) {
                case 1:
                    // 查看活动成员列表
                    ActivityMemberFragment.open(ActivityPropertiesFragment.this, ACTIVITY_BASE_REQUEST, mQueryId, activity.getGroupId(), false, false);
                    break;
                case 2:
                    // 创建者是当前登录的用户时，可以 修改群名称
                    if (activity.getCreatorId().equals(Cache.cache().userId)) {
                        String name = StringHelper.isEmpty(activity.getTitle()) ? "" : activity.getTitle();
                        String regex = StringHelper.getString(R.string.ui_popup_input_activity_title, name).replace("", "");
                        openActivity(BasePopupInputSupportFragment.class.getName(), regex, REQUEST_NAME, true, false);
                    }
                    break;
                case 3:
                    // 管理权转让
                    tryTransferOwner();
                    break;
                case 6:
                    // 活动介绍
                    break;
                case 7:
                    // 活动公告
                    NoticeListFragment.open(ActivityPropertiesFragment.this, mSessionId, false);
                    break;
                case 8:
                    // 查看聊天内容
                    SessionHistoryActivity.start(Activity(), mSessionId, SessionTypeEnum.Team);
                    break;
                case 9:
                    // 清空聊天记录
                    warningClearChatHistory();
                    break;
            }
        }
    };

    private void warningTransferOwner(final String groupId) {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_property_transfer_warning, R.string.ui_base_text_i_known, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                ActivityMemberFragment.open(ActivityPropertiesFragment.this, REQUEST_PICK_ONE, mQueryId, groupId, true, false);
                return true;
            }
        }, null);
    }

    // 尝试转让活动组群
    private void tryTransferOwner() {
        Activity act = (Activity) mAdapter.get(0);
        final String groupId = act.getGroupId();
        TeamDataCache.getInstance().fetchTeamMember(mSessionId, Cache.cache().userId, new SimpleCallback<TeamMember>() {
            @Override
            public void onResult(boolean success, TeamMember result, int code) {
                if (success && null != result) {
                    if (result.getType() == TeamMemberType.Manager || result.getType() == TeamMemberType.Owner) {
                        warningTransferOwner(groupId);
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_property_transfer_failed_no_permission);
                    }
                }
            }
        });
    }

    private void warningClearChatHistory() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_property_clean_chat_history, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                NIMClient.getService(MsgService.class).clearChattingHistory(mSessionId, SessionTypeEnum.Team);
                MessageListPanelHelper.getInstance().notifyClearMessages(mSessionId);
                return true;
            }
        }, null);
    }

    private ToggleableViewHolder.OnViewHolderToggleChangedListener toggleChangedListener = new ToggleableViewHolder.OnViewHolderToggleChangedListener() {
        @Override
        public void onChange(int index, boolean togged) {
            if (index == 5) {
                // 消息免打扰
                NIMClient.getService(TeamService.class).muteTeam(mSessionId, togged ? TeamMessageNotifyTypeEnum.Mute : TeamMessageNotifyTypeEnum.All);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_NAME) {
            String result = getResultedData(data);
            tryEditActivity(ActRequest.TYPE_TITLE, result);
        } else if (requestCode == REQUEST_PICK_ONE) {
            // 选择了新的组群所有者
            String account = getResultedData(data);
            transferActivity(account);
        }
        super.onActivityResult(requestCode, data);
    }

    private void transferActivity(String toAccount) {
        if (isEmpty(toAccount)) {
            ToastHelper.make().showMsg(R.string.ui_activity_property_transfer_null_account);
        } else {
            /*
              拥有者将群的拥有者权限转给另外一个人，转移后，另外一个人成为拥有者。
                  原拥有者变成普通成员。若参数quit为true，原拥有者直接退出该群。
              @param teamId 群ID
             * @param account 新任拥有者的用户帐号
             * @param quit 转移时是否要同时退出该群
             * @return InvocationFuture 可以设置回调函数，如果成功，视参数 quit 值：
             *     quit为false：参数仅包含原拥有着和当前拥有者的(即操作者和 account)，权限已被更新。
             *     quit为true: 参数为空。
             */
            NIMClient.getService(TeamService.class).transferTeam(mSessionId, toAccount, false).setCallback(new RequestCallback<List<TeamMember>>() {
                @Override
                public void onSuccess(List<TeamMember> teamMembers) {
                    ToastHelper.make().showMsg(R.string.ui_activity_property_transfer_success);
                }

                @Override
                public void onFailed(int i) {
                    ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_activity_property_transfer_failed, i));
                }

                @Override
                public void onException(Throwable throwable) {
                    ToastHelper.make().showMsg(format("转让失败：" + throwable.getMessage()));
                }
            });
        }
    }

    private void tryEditActivity(int type, String value) {
        displayLoading(true);
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                // 无论活动名称更改成功与否都重新拉取活动的信息
                // 此时失败有可能是服务器端不能同步网易云，但实际上名称已经改了
                fetchingActivity();
            }
        }).update(mQueryId, type, value);
    }

    private class PropertiesAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_MEMBER = 1, VT_TOGGLE = 2, VT_NORMAL = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = ActivityPropertiesFragment.this;
            switch (viewType) {
                case VT_HEADER:
                    UserHeaderBigViewHolder uhbvh = new UserHeaderBigViewHolder(itemView, fragment);
                    //tryPaddingContent(itemView, false);
                    uhbvh.addOnViewHolderClickListener(viewHolderClickListener);
                    return uhbvh;
                case VT_MEMBER:
                    SimpleMemberViewHolder memberViewHolder = new SimpleMemberViewHolder(itemView, fragment);
                    memberViewHolder.addOnViewHolderClickListener(viewHolderClickListener);
                    return memberViewHolder;
                case VT_TOGGLE:
                    ToggleableViewHolder toggleableViewHolder = new ToggleableViewHolder(itemView, fragment);
                    toggleableViewHolder.addOnViewHolderToggleChangedListener(toggleChangedListener);
                    return toggleableViewHolder;
                default:
                    SimpleClickableViewHolder scvh = new SimpleClickableViewHolder(itemView, fragment);
                    scvh.addOnViewHolderClickListener(viewHolderClickListener);
                    return scvh;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return VT_HEADER;
                case 1:
                    return VT_MEMBER;
                case 4:
                case 5:
                    return VT_TOGGLE;
                default:
                    return VT_NORMAL;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.holder_view_individual_header_big;
                case VT_MEMBER:
                    return R.layout.holder_view_organization_simple_member;
                case VT_TOGGLE:
                    return R.layout.holder_view_toggle;
                default:
                    return R.layout.holder_view_simple_clickable;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof SimpleMemberViewHolder) {
                ((SimpleMemberViewHolder) holder).showContent((SimpleClickableItem) item);
                ((SimpleMemberViewHolder) holder).showContent((Activity) mAdapter.get(0));
            } else if (holder instanceof SimpleClickableViewHolder) {
                ((SimpleClickableViewHolder) holder).showContent(item);
            } else if (holder instanceof ToggleableViewHolder) {
                ((ToggleableViewHolder) holder).showContent((SimpleClickableItem) item);
            } else if (holder instanceof UserHeaderBigViewHolder) {
                ((UserHeaderBigViewHolder) holder).showContent((Activity) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
