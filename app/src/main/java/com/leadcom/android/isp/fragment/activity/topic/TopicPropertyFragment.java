package com.leadcom.android.isp.fragment.activity.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.AppTopicMemberRequest;
import com.leadcom.android.isp.api.activity.AppTopicRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.activity.ActivityMemberFragment;
import com.leadcom.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BasePopupInputSupportFragment;
import com.leadcom.android.isp.fragment.individual.UserPropertyFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.activity.TopicMemberAttacherViewHolder;
import com.leadcom.android.isp.holder.activity.VoteItemUserViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.ToggleableViewHolder;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.nim.activity.SessionHistoryActivity;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.Iterator;

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
    private static final String PARAM_DELETABLE = "tpf_deletable";

    public static TopicPropertyFragment newInstance(String params) {
        TopicPropertyFragment tpf = new TopicPropertyFragment();
        Bundle bundle = new Bundle();
        // 传过来的tid
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
    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    private RecyclerView mRecyclerView;
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
    private boolean deletable = false;
    private AppTopic appTopic;
    private MemberAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        topicId = bundle.getString(PARAM_TOPIC_ID, "");
        actId = bundle.getString(PARAM_ACT_ID, "");
        deletable = bundle.getBoolean(PARAM_DELETABLE, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TOPIC_ID, topicId);
        bundle.putString(PARAM_ACT_ID, actId);
        bundle.putBoolean(PARAM_DELETABLE, deletable);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new FlexboxLayoutManager(mRecyclerView.getContext(), FlexDirection.ROW, FlexWrap.WRAP));
    }

    @Override
    public int getLayout() {
        return R.layout.fratment_activity_topic_property;
    }

    @Override
    public void doingInResume() {
        initializeAdapter();
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

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MemberAdapter();
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mAdapter);
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
            NIMClient.getService(TeamService.class).muteTeam(mQueryId, togged ? TeamMessageNotifyTypeEnum.Mute : TeamMessageNotifyTypeEnum.All);
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
        // 删除成员
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Member) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
        // 显示成员
        if (null != appTopic.getActTopicMemberList()) {
            index = 0;
            for (Member member : appTopic.getActTopicMemberList()) {
                member.setSelectable(deletable);
                mAdapter.add(member, index);
                index++;
            }
        }
        if (isTopicCreatedByMe()) {
            // 添加成员
            Model add = new Model();
            add.setId("+");
            mAdapter.add(add);

            // 删除成员
            Model delete = new Model();
            delete.setId("-");
            mAdapter.add(delete);
        }
    }

    private class MemberAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_MEMBER = 0, VT_OTHER = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_MEMBER) {
                VoteItemUserViewHolder holder = new VoteItemUserViewHolder(itemView, TopicPropertyFragment.this);
                holder.addOnViewHolderClickListener(memberClickListener);
                holder.addOnHandlerBoundDataListener(onHandleBoundDataListener);
                return holder;
            } else {
                TopicMemberAttacherViewHolder tmavh = new TopicMemberAttacherViewHolder(itemView, TopicPropertyFragment.this);
                tmavh.addOnViewHolderClickListener(memberClickListener);
                return tmavh;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return get(position) instanceof Member ? VT_MEMBER : VT_OTHER;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_MEMBER ? R.layout.holder_view_activity_vote_item_details_user : R.layout.holder_view_activity_topic_member_addor;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof VoteItemUserViewHolder) {
                ((VoteItemUserViewHolder) holder).showContent((Member) item);
            } else if (holder instanceof TopicMemberAttacherViewHolder) {
                ((TopicMemberAttacherViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }

    // 点击用户查看用户信息
    private OnViewHolderClickListener memberClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Member) {
                if (!model.isSelectable()) {
                    // 不处于删除状态时，打开用户详情页
                    Member member = (Member) model;
                    UserPropertyFragment.open(TopicPropertyFragment.this, member.getUserId());
                }
            } else {
                if (model.getId().equals("+")) {
                    attachMembers();
                } else if (model.getId().equals("-")) {
                    resetDeleteStatus();
                }
            }
        }
    };

    private void attachMembers() {
        // 选择参与人
        //TopicMemberSelectorFragment.open(TopicPropertyFragment.this, REQUEST_SELECT, "");

        Activity act = Activity.get(actId);
        if (null != act) {
            // 从活动中选择
            ActivityMemberFragment.open(TopicPropertyFragment.this, REQUEST_SELECT, act.getId(), act.getGroupId(), true, true);
        } else {
            ToastHelper.make().showMsg(R.string.ui_activity_property_not_exist);
            finish();
        }
    }

    private void resetDeleteStatus() {
        deletable = !deletable;
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Model m = mAdapter.get(i);
            if (m instanceof Member) {
                Member membr = (Member) m;
                if (!membr.getUserId().equals(Cache.cache().userId)) {
                    // 不是我自己时才显示删除按钮
                    membr.setSelectable(deletable);
                    mAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    // 删除用户
    private OnHandleBoundDataListener<Model> onHandleBoundDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Model onHandlerBoundData(BaseViewHolder holder) {
            Model model = mAdapter.get(holder.getAdapterPosition());
            if (model instanceof Member) {
                Member member = (Member) model;
                warningDeleteMember(member.getUserId(), member.getUserName(), holder.getAdapterPosition());
            }
            return null;
        }
    };

    private void warningDeleteMember(final String userId, String userName, final int deleteIndex) {
        SimpleDialogHelper.init(Activity()).show(getString(R.string.ui_activity_topic_property_member_delete_warning, userName), R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteMember(userId, deleteIndex);
                return true;
            }
        }, null);
    }

    private void deleteMember(String userId, final int deleteIndex) {
        showImageHandlingDialog(R.string.ui_activity_topic_property_member_deleting);
        AppTopicMemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member appTopicMember, boolean success, String message) {
                super.onResponse(appTopicMember, success, message);
                hideImageHandlingDialog();
                if (success) {
                    mAdapter.remove(deleteIndex);
                }
            }
        }).delete(topicId, userId);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT:
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
            AppTopicMemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
                @Override
                public void onResponse(Member member, boolean success, String message) {
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
                public void onResult(boolean success, Team result, int code) {
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
        AppTopicMemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member appTopicMember, boolean success, String message) {
                super.onResponse(appTopicMember, success, message);
                hideImageHandlingDialog();
                if (success) {
                    Member.removeMemberOfTopicId(topicId);
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
                hideImageHandlingDialog();
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_activity_topic_property_ended);
                    finish();
                }
            }
        }).delete(topicId);
    }
}
