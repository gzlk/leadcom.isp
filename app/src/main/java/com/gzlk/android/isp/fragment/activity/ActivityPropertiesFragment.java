package com.gzlk.android.isp.fragment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.BaseTransparentPropertyFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BasePopupInputSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.organization.SimpleMemberViewHolder;
import com.gzlk.android.isp.holder.individual.UserHeaderBigViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.ToggleableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.common.SimpleClickableItem;
import com.gzlk.android.isp.nim.activity.SessionHistoryActivity;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.session.helper.MessageListPanelHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

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
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    new Dao<>(Activity.class).delete(mQueryId);
                    ToastHelper.make().showMsg(R.string.ui_activity_property_exited);
                    finish();
                }
            }
        }).exit(mQueryId);
    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingActivity(true);
    }

    private void fetchingActivity(boolean fromRemote) {
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
        }).find(mQueryId, fromRemote);
    }

    private void initializeActivity(Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_property_items);
        }
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
                case 6:
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
            resetNotificationStatus(team.mute());
        } else {
            TeamDataCache.getInstance().fetchTeamById(mSessionId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result) {
                    if (success && result != null) {
                        resetNotificationStatus(result.mute());
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
        String string = items[6];
        string = format(string, mute ? 1 : 0);
        SimpleClickableItem item = new SimpleClickableItem(string);
        mAdapter.update(item);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new PropertiesAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingActivity(false);
        }
    }

    private static final int REQUEST_NAME = ACTIVITY_BASE_REQUEST + 10;

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 1:
                    // 查看活动成员列表
                    Activity act = (Activity) mAdapter.get(0);
                    boolean isMaster = act.getCreatorId().equals(Cache.cache().userId);
                    openActivity(ActivityMemberFragment.class.getName(), format("%s,%s,%s", mQueryId, isMaster, act.getGroupId()), true, false);
                    break;
                case 2:
                    // 创建者是当前登录的用户时，可以 修改群名称
                    Activity activity = (Activity) mAdapter.get(0);
                    if (activity.getCreatorId().equals(Cache.cache().userId)) {
                        String name = StringHelper.isEmpty(activity.getTitle()) ? "" : activity.getTitle();
                        String regex = StringHelper.getString(R.string.ui_popup_input_activity_title, name).replace("", "");
                        openActivity(BasePopupInputSupportFragment.class.getName(), regex, REQUEST_NAME, true, false);
                    }
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
            if (index == 6) {
                // 消息免打扰
                NIMClient.getService(TeamService.class).muteTeam(mSessionId, togged);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_NAME) {
            String result = getResultedData(data);
            tryEditActivity(ActRequest.TYPE_TITLE, result);
        }
        super.onActivityResult(requestCode, data);
    }

    private void tryEditActivity(int type, String value) {
        displayLoading(true);
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                // 无论活动名称更改成功与否都重新拉取活动的信息
                // 此时失败有可能是服务器端不能同步网易云，但实际上名称已经改了
                fetchingActivity(true);
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
                case 5:
                case 6:
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
