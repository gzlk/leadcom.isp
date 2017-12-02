package com.leadcom.android.isp.fragment.activity.topic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.ActRequest;
import com.leadcom.android.isp.api.activity.AppTopicMemberRequest;
import com.leadcom.android.isp.api.activity.AppTopicRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.activity.ActivityViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.nim.model.extension.MinutesAttachment;
import com.leadcom.android.isp.nim.model.extension.NoticeAttachment;
import com.leadcom.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.leadcom.android.isp.nim.model.extension.TopicAttachment;
import com.leadcom.android.isp.nim.model.extension.VoteAttachment;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.List;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/29 15:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/29 15:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class TopicListFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TYPE = "tlf_param_type";

    public static TopicListFragment newInstance(String params) {
        TopicListFragment tlf = new TopicListFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 传过来的tid或组织的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 标记tid是组织还是活动
        bundle.putBoolean(PARAM_TYPE, Boolean.valueOf(strings[1]));
        tlf.setArguments(bundle);
        return tlf;
    }

    public static void open(BaseFragment fragment, String groupId) {
        fragment.openActivity(TopicListFragment.class.getName(), format("%s,false", groupId), true, false);
    }

    public static void open(Context context, String tid, int req) {
        BaseActivity.openActivity(context, TopicListFragment.class.getName(), format("%s,true", tid), req, true, false);
    }

    private String activityId = "";
    private boolean isTidActivity = true;
    private TopicAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isTidActivity = bundle.getBoolean(PARAM_TYPE, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_TYPE, isTidActivity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, true);
    }

    @Override
    public void onDestroy() {
        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, false);
        super.onDestroy();
    }

    //  创建观察者对象
    private Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> contacts) {
            // 当最近联系人列表数据有变化时，同步当前显示组织里的所有活动的未读标记和最近聊天内容
            resetUnreadFlags(contacts);
        }
    };

    // 查询最近联系人列表，并同步更新未读消息
    private void resetUnreadFlags() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable e) {
                // recents参数即为最近联系人列表（最近会话列表）
                resetUnreadFlags(recents);
            }
        });
    }

    private void resetUnreadFlags(List<RecentContact> contacts) {
        if (null == mAdapter) {
            return;
        }
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            AppTopic topic = mAdapter.get(i);
            RecentContact contact = get(topic.getTid(), contacts);
            if (null != contact) {
                topic.setUnReadNum(contact.getUnreadCount());
                // 最后发送消息的时间
                topic.setCreateDate(Utils.format(getString(R.string.ui_base_text_date_time_format), contact.getTime()));
                String nick = "";
                try {
                    nick = contact.getFromNick();
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
                if (isEmpty(nick)) {
                    nick = "";
                }
                topic.setAccessToken(format("%s: %s", nick, getRecentMsgType(contact)));
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    private String getRecentMsgType(RecentContact contact) {
        String ret = contact.getContent();
        if (ret.contains(getString(R.string.ui_nim_app_recent_contact_type_custom))) {
            MsgAttachment attachment = contact.getAttachment();
            if (attachment instanceof NoticeAttachment) {
                ret = getString(R.string.ui_nim_app_recent_contact_type_notice);
            } else if (attachment instanceof SigningNotifyAttachment) {
                ret = getString(R.string.ui_nim_app_recent_contact_type_signing);
            } else if (attachment instanceof VoteAttachment) {
                ret = getString(R.string.ui_nim_app_recent_contact_type_vote);
            } else if (attachment instanceof TopicAttachment) {
                ret = getString(R.string.ui_nim_app_recent_contact_type_topic);
            } else if (attachment instanceof MinutesAttachment) {
                ret = getString(R.string.ui_nim_app_recent_contact_type_minutes);
            }
        }
        return ret;
    }

    private RecentContact get(String tid, List<RecentContact> contacts) {
        if (null == contacts || contacts.size() < 1) {
            return null;
        }
        for (RecentContact r : contacts) {
            if (r.getContactId().equals(tid)) {
                return r;
            }
        }
        return null;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_nim_action_issue);
        if (isTidActivity) {
            setRightText(R.string.ui_activity_topic_list_right_title_text);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    resultSucceededActivity();
                }
            });
        }
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        loadingTopics();
    }

    @Override
    protected void onLoadingMore() {
        loadingTopics();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingActivity() {
        if (isEmpty(activityId)) {
            Activity act = Activity.getByTid(mQueryId);
            if (null != act) {
                activityId = act.getId();
            }
        }
    }

    private void loadingTopics() {
        displayLoading(true);
        displayNothing(false);
        if (isTidActivity) {
            loadingActivityTopics();
        } else {
            loadingGroupTopics();
        }
    }

    private void loadingActivityTopics() {
        AppTopicRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppTopic>() {
            @Override
            public void onResponse(List<AppTopic> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                resetAdapter(success, list, pageSize);
            }
        }).list(activityId, remotePageNumber);
    }

    private void loadingGroupTopics() {
        ActRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Activity>() {
            @Override
            public void onResponse(List<Activity> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                resetAdapter(success, actTopicList, pageSize);
            }
        }).listFront(mQueryId, remotePageNumber, Cache.cache().groupIds);
    }

    private void resetAdapter(boolean success, List<AppTopic> list, int pageSize) {
        if (success) {
            if (null != list) {
                // 第一页时清空adapter
                if (remotePageNumber <= 1) {
                    mAdapter.clear();
                }
                if (list.size() >= pageSize) {
                    remotePageNumber++;
                    isLoadingComplete(false);
                } else {
                    isLoadingComplete(true);
                }
                mAdapter.update(list, false);
            } else {
                isLoadingComplete(true);
            }
        } else {
            isLoadingComplete(true);
        }
        displayLoading(false);
        displayNothing(mAdapter.getItemCount() < 1);
        stopRefreshing();
        // 查询网易云信联系人列表，并更新相应的未读提示和最后发送的消息
        resetUnreadFlags();
    }

    private void initializeAdapter() {
        fetchingActivity();
        if (null == mAdapter) {
            setLoadingText(R.string.ui_activity_topic_list_loading_text);
            setNothingText(R.string.ui_activity_topic_list_nothing_text);
            mAdapter = new TopicAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingTopics();
        }
    }

    private void fetchingTopicMembers(String topicId, final String tid) {
        AppTopicMemberRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Member>() {
            @Override
            public void onResponse(List<Member> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (Member.isMeMemberOfTopic(tid)) {
                        NimSessionHelper.startTeamSession(Activity(), tid);
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_topic_not_member_of);
                    }
                }
            }
        }).list(topicId, 1);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            AppTopic topic = mAdapter.get(index);
            if (Member.isMeMemberOfTopic(topic.getTid())) {
                NimSessionHelper.startTeamSession(Activity(), topic.getTid());
            } else {
                fetchingTopicMembers(topic.getId(), topic.getTid());
            }
        }
    };

    private class TopicAdapter extends RecyclerViewAdapter<ActivityViewHolder, AppTopic> {

        @Override
        public ActivityViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityViewHolder holder = new ActivityViewHolder(itemView, TopicListFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_home_item;
        }

        @Override
        public void onBindHolderOfView(ActivityViewHolder holder, int position, @Nullable AppTopic item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(AppTopic item1, AppTopic item2) {
            return 0;
        }
    }
}
