package com.gzlk.android.isp.fragment.activity.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.api.activity.AppTopicMemberRequest;
import com.gzlk.android.isp.api.activity.AppTopicRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.activity.ActivityMemberFragment;
import com.gzlk.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BasePopupInputSupportFragment;
import com.gzlk.android.isp.fragment.organization.GroupContactPickFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.activity.VoteItemUserViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.ToggleableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.topic.AppTopic;
import com.gzlk.android.isp.model.activity.topic.AppTopicMember;
import com.gzlk.android.isp.model.organization.SubMember;
import com.gzlk.android.isp.nim.activity.SessionHistoryActivity;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>议题属性页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/30 21:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/30 21:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TopicPropertyFragment extends BaseDownloadingUploadingSupportFragment {

    private static final String PARAM_TOPIC_ID = "tpf_topic_id";
    private static final String PARAM_ACT_ID = "tpf_act_id";

    public static TopicPropertyFragment newInstance(String params) {
        TopicPropertyFragment tpf = new TopicPropertyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        tpf.setArguments(bundle);
        return tpf;
    }

    public static void open(BaseFragment fragment, String tid, int req) {
        fragment.openActivity(TopicPropertyFragment.class.getName(), tid, req, true, false);
    }

    public static void open(Context context, String tid, int req) {
        BaseActivity.openActivity(context, TopicPropertyFragment.class.getName(), tid, req, true, false);
    }

    private String[] items;
    @ViewId(R.id.ui_activity_topic_property_title)
    private View titleView;
    @ViewId(R.id.ui_activity_topic_property_members_title)
    private TextView memberTitle;
    @ViewId(R.id.ui_activity_topic_property_members)
    private FlexboxLayout members;
    @ViewId(R.id.ui_activity_topic_property_history)
    private View historyView;
    @ViewId(R.id.ui_activity_topic_property_files)
    private View fileView;
    @ViewId(R.id.ui_activity_topic_property_mute)
    private View muteView;
    @ViewId(R.id.ui_activity_topic_property_button)
    private CorneredButton button;

    private SimpleClickableViewHolder titleHolder, historyHolder, fileHolder;
    private ToggleableViewHolder muteHolder;
    private String topicId = "", actId = "";
    private AppTopic appTopic;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        topicId = bundle.getString(PARAM_TOPIC_ID, "");
        actId = bundle.getString(PARAM_ACT_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TOPIC_ID, topicId);
        bundle.putString(PARAM_ACT_ID, actId);
    }

    @Override
    public int getLayout() {
        return R.layout.fratment_activity_topic_property;
    }

    @Override
    public void doingInResume() {
        initializeHolder();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void resetTitleEvent() {
        setCustomTitle(R.string.ui_activity_topic_property_fragment_title);
    }

    private void fetchLocalTopic() {
        if (isEmpty(topicId)) {
            AppTopic topic = AppTopic.queryByTid(mQueryId);
            if (null == topic) {
                ToastHelper.make().showMsg(R.string.ui_activity_topic_property_invalid);
                finish();
            } else {
                topicId = topic.getId();
                actId = topic.getActId();
            }
        }
    }

    private void initializeHolder() {
        fetchLocalTopic();
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_topic_property_items);
            resetTitleEvent();
            memberTitle.setText(getString(R.string.ui_activity_topic_property_members_title, 0));
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, this);
            titleHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            titleHolder.showContent(format(items[0], ""));
        }
        if (null == historyHolder) {
            historyHolder = new SimpleClickableViewHolder(historyView, this);
            historyHolder.showContent(items[1]);
            historyHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        if (null == fileHolder) {
            fileHolder = new SimpleClickableViewHolder(fileView, this);
            fileHolder.showContent(items[2]);
            fileHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        if (null == muteHolder) {
            muteHolder = new ToggleableViewHolder(muteView, this);
            muteHolder.showContent(format(items[3], 0));
            muteHolder.addOnViewHolderToggleChangedListener(toggleChangedListener);
        }
        fetchingTopic();
        Handler().post(new Runnable() {
            @Override
            public void run() {
                resetTopicMute();
            }
        });
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 更改议题名称
                    if (isTopicCreatedByMe()) {
                        String name = StringHelper.isEmpty(appTopic.getTitle()) ? "" : appTopic.getTitle();
                        String regex = StringHelper.getString(R.string.ui_popup_input_activity_title, name).replace("", "");
                        openActivity(BasePopupInputSupportFragment.class.getName(), regex, REQUEST_DELETE, true, false);
                    }
                    break;
                case 1:
                    // 查看聊天内容
                    SessionHistoryActivity.start(Activity(), mQueryId, SessionTypeEnum.Team);
                    break;
            }
        }
    };

    private ToggleableViewHolder.OnViewHolderToggleChangedListener toggleChangedListener = new ToggleableViewHolder.OnViewHolderToggleChangedListener() {
        @Override
        public void onChange(int index, boolean togged) {
            // 消息免打扰
            NIMClient.getService(TeamService.class).muteTeam(mQueryId, togged);
        }
    };

    private boolean isTopicCreatedByMe() {
        return null != appTopic && !isEmpty(appTopic.getCreatorId()) && appTopic.getCreatorId().equals(Cache.cache().userId);
    }

    private void resetHolders() {
        titleHolder.showContent(format(items[0], appTopic.getTitle()));
        memberTitle.setText(getString(R.string.ui_activity_topic_property_members_title, appTopic.getActTopicMemberList().size()));
        button.setText(isTopicCreatedByMe() ? R.string.ui_activity_topic_property_end : R.string.ui_activity_topic_property_exit);
        showTopicMembers();
    }

    private void showTopicMembers() {
        members.removeAllViews();
        if (null != appTopic.getActTopicMemberList()) {
            for (AppTopicMember member : appTopic.getActTopicMemberList()) {
                addMember(member);
            }
        }
        if (isTopicCreatedByMe()) {
            View view = View.inflate(members.getContext(), R.layout.holder_view_activity_topic_member_addor, null);
            view.setOnClickListener(addClick);
            members.addView(view);
        }
    }

    private View.OnClickListener addClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 选择参与人
            TopicMemberSelectorFragment.open(TopicPropertyFragment.this, REQUEST_SELECT, "");
        }
    };

    private void addMember(AppTopicMember member) {
        View view = View.inflate(members.getContext(), R.layout.holder_view_activity_vote_item_details_user, null);
        members.addView(view);
        VoteItemUserViewHolder holder = new VoteItemUserViewHolder(view, this);
        holder.showContent(member);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT:
                Activity act = Activity.get(actId);
                if (null != act) {
                    int req = Integer.valueOf(getResultedData(data));
                    if (req == REQUEST_CHANGE) {
                        // 从活动中选择
                        ActivityMemberFragment.open(TopicPropertyFragment.this, REQUEST_CHANGE, act.getId(), act.getGroupId(), true, true);
                    } else if (req == REQUEST_DELETE) {
                        // 从组织通讯录中选择
                        GroupContactPickFragment.open(TopicPropertyFragment.this, REQUEST_CHANGE, act.getGroupId(), false, false, "");
                    }
                } else {
                    ToastHelper.make().showMsg("活动不存在");
                    finish();
                }
                break;
            case REQUEST_CHANGE:
                // 活动或组织成员选择之后的返回内容
                String json = getResultedData(data);
                inviteNewMembers(json);
                break;
            case REQUEST_DELETE:
                // 更改议题名称
                String result = getResultedData(data);
                if (!isEmpty(result)) {
                    updateTopicTitle(result);
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void inviteNewMembers(String json) {
        ArrayList<String> userIds = SubMember.getUserIds(SubMember.fromJson(json));
        if (userIds.size() < 1) {
            ToastHelper.make().showMsg(R.string.ui_activity_topic_property_member_selected_none);
        } else {
            showImageHandlingDialog(R.string.ui_activity_topic_property_member_inviting);
            AppTopicMemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppTopicMember>() {
                @Override
                public void onResponse(AppTopicMember member, boolean success, String message) {
                    super.onResponse(member, success, message);
                    hideImageHandlingDialog();
                    if (success) {
                        ToastHelper.make().showMsg(message);
                        fetchingTopicDirectly();
                    }
                }
            }).add(topicId, userIds);
        }
    }

    private void updateTopicTitle(final String title) {
        showImageHandlingDialog(R.string.ui_activity_topic_property_update_title);
        AppTopicRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppTopic>() {
            @Override
            public void onResponse(AppTopic topic, boolean success, String message) {
                super.onResponse(topic, success, message);
                hideImageHandlingDialog();
                if (success) {
                    titleHolder.showContent(format(items[0], title));
                }
            }
        }).update(topicId, title);
    }

    private void resetTopicMute() {
        Team team = TeamDataCache.getInstance().getTeamById(mQueryId);
        if (null != team) {
            muteHolder.showContent(format(items[3], team.mute() ? 1 : 0));
        } else {
            TeamDataCache.getInstance().fetchTeamById(mQueryId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result) {
                    if (success && result != null) {
                        muteHolder.showContent(format(items[3], result.mute() ? 1 : 0));
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_topic_property_invalid);
                    }
                }
            });
        }
    }

    private void fetchingTopic() {
        if (!isEmpty(topicId) && null == appTopic) {
            fetchingTopicDirectly();
        }
    }

    private void fetchingTopicDirectly() {
        showImageHandlingDialog(R.string.ui_activity_topic_property_loading);
        AppTopicRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppTopic>() {
            @Override
            public void onResponse(AppTopic topic, boolean success, String message) {
                super.onResponse(topic, success, message);
                hideImageHandlingDialog();
                if (success) {
                    appTopic = topic;
                    resetHolders();
                }
            }
        }).find(topicId);
    }

    @Click({R.id.ui_activity_topic_property_button})
    private void elementClick(View view) {
        if (isTopicCreatedByMe()) {
            warningEndTopic();
        } else {
            exitTopic();
        }
    }

    private void exitTopic() {
        showImageHandlingDialog(R.string.ui_activity_topic_property_exiting);
        AppTopicMemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppTopicMember>() {
            @Override
            public void onResponse(AppTopicMember appTopicMember, boolean success, String message) {
                super.onResponse(appTopicMember, success, message);
                hideImageHandlingDialog();
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_activity_topic_property_exited);
                    finish();
                }
            }
        }).exit(topicId);
    }

    private void warningEndTopic() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_topic_property_end_warning, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                endTopic();
                return true;
            }
        }, null);
    }

    private void endTopic() {
        showImageHandlingDialog(R.string.ui_activity_topic_property_ending);
        AppTopicRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppTopic>() {
            @Override
            public void onResponse(AppTopic topic, boolean success, String message) {
                super.onResponse(topic, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_activity_topic_property_ended);
                    finish();
                }
            }
        }).delete(topicId);
    }
}
