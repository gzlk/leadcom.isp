package com.gzlk.android.isp.fragment.activity.topic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.AppTopicRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.activity.ActivityViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.topic.AppTopic;
import com.gzlk.android.isp.nim.model.extension.NoticeAttachment;
import com.gzlk.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.gzlk.android.isp.nim.model.extension.VoteAttachment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
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

    public static TopicListFragment newInstance(String params) {
        TopicListFragment tlf = new TopicListFragment();
        Bundle bundle = new Bundle();
        // 传过来的tid
        bundle.putString(PARAM_QUERY_ID, params);
        tlf.setArguments(bundle);
        return tlf;
    }

    public static void open(Context context, String tid, int req) {
        BaseActivity.openActivity(context, TopicListFragment.class.getName(), tid, req, true, false);
    }

    private TopicAdapter mAdapter;

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
                    nick = "null";
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
        setRightText(R.string.ui_activity_topic_list_right_title_text);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultSucceededActivity();
            }
        });
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

    private void loadingTopics() {
        displayLoading(true);
        displayNothing(false);
        AppTopicRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppTopic>() {
            @Override
            public void onResponse(List<AppTopic> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
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
            }
        }).list(mQueryId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setLoadingText(R.string.ui_activity_topic_list_loading_text);
            setNothingText(R.string.ui_activity_topic_list_nothing_text);
            mAdapter = new TopicAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingTopics();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {

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
