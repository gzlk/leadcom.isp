package com.leadcom.android.isp.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.common.PushMsgRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.NimApplication;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.home.SystemMessageViewHolder;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnNimMessageEvent;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.common.Message;
import com.leadcom.android.isp.nim.model.notification.NimMessage;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.leadcom.android.isp.view.SwipeItemLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>系统消息页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 02:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 02:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SystemMessageFragment extends BaseSwipeRefreshSupportFragment {

    public static void open(Context context) {
        BaseActivity.openActivity(context, SystemMessageFragment.class.getName(), "", true, false);
    }

    public static void open(BaseFragment fragment) {
        fragment.openActivity(SystemMessageFragment.class.getName(), "", true, false);
    }

    private MessageAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NimApplication.addNimMessageEvent(event);
    }

    @Override
    public void onDestroy() {
        NimApplication.removeNimMessageEvent(event);
        super.onDestroy();
    }

    private OnNimMessageEvent event = new OnNimMessageEvent() {
        @Override
        public void onMessageEvent(NimMessage message) {
            mAdapter.update(message);
        }
    };

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_top_paddingable_swipe_recycler_view;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_system_message_fragment_title);
        setNothingText(R.string.ui_system_message_nothing);
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
        stopRefreshing();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MessageAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(mAdapter);
            setRightIcon(R.string.ui_icon_delete);
            setRightText(R.string.ui_base_text_clear);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    warningClear();
                }
            });
            fetchingPushMessages();
        }
    }

    private void warningClear() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_system_message_clear_warning, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                clearPushMessage();
                return true;
            }
        }, null);
    }

    private void clearPushMessage() {
        displayLoading(true);
        displayNothing(false);
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<NimMessage>() {
            @Override
            public void onResponse(NimMessage nimMessage, boolean success, String message) {
                super.onResponse(nimMessage, success, message);
                displayLoading(false);
                if (success) {
                    NimMessage.clear();
                    NimApplication.dispatchCallbacks();
                    mAdapter.clear();
                    displayNothing(mAdapter.getItemCount() < 1);
                }
            }
        }).clearByUser();
    }

    private void fetchingPushMessages() {
        displayLoading(true);
        displayNothing(false);
        PushMsgRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<NimMessage>() {
            @Override
            public void onResponse(List<NimMessage> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    mAdapter.update(list);
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).list();
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 点击查看通知
            NimMessage msg = mAdapter.get(index);
            if (msg.getMsgType() == NimMessage.Type.ACTIVITY_INVITE && msg.isRead()) {
                // 活动邀请且已处理过的话
                if (msg.isHandled()) {
                    checkTeamMember(msg.getTid());
                } else {
                    MainActivity.handleNimMessageDetails(Activity(), msg);
                }
            } else {
                MainActivity.handleNimMessageDetails(Activity(), msg);
            }
            if (!msg.isRead()) {
                updatePushMessage(msg.getUuid());
            }
//            if (!msg.isHandled()) {
//                msg.setHandled(true);
//                msg.setStatus(NimMessage.Status.READ);
//                mAdapter.notifyItemChanged(index);
//                NimMessage.resetStatus(msg.getUuid());
//                NimApplication.dispatchCallbacks();
//            }
        }
    };

    private void updatePushMessage(final String uuid) {
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<NimMessage>() {
            @Override
            public void onResponse(NimMessage nimMessage, boolean success, String message) {
                super.onResponse(nimMessage, success, message);
                if (success) {
                    if (null != nimMessage) {
                        mAdapter.update(nimMessage);
                    } else {
                        NimMessage msg = mAdapter.query(uuid);
                        if (null != msg) {
                            msg.setStatus(Message.Status.READ);
                            mAdapter.update(msg);
                            NimMessage.resetStatus(msg.getTid());
                            NimApplication.dispatchCallbacks();
                        }
                    }
                }
            }
        }).update(uuid);
    }

    private void checkTeamMember(final String tid) {
        NIMClient.getService(TeamService.class).queryTeamMember(tid, Cache.cache().userId).setCallback(new RequestCallback<TeamMember>() {
            @Override
            public void onSuccess(TeamMember teamMember) {
                // 查找当前用户是否已经在群内
                if (null != teamMember) {
                    if (teamMember.isInTeam()) {
                        // 已经是群内的成员, 直接打开群聊页面
                        NimSessionHelper.startTeamSession(Activity(), tid);
                    } else {
                        // 已经退出群了
                        ToastHelper.make().showMsg(R.string.ui_activity_property_exited);
                    }
                }
            }

            @Override
            public void onFailed(int i) {
                displayLoading(false);
            }

            @Override
            public void onException(Throwable throwable) {
                displayLoading(false);
            }
        });
    }

    private OnHandleBoundDataListener<NimMessage> handlerBoundDataListener = new OnHandleBoundDataListener<NimMessage>() {
        @Override
        public NimMessage onHandlerBoundData(BaseViewHolder holder) {
            // 删除通知
            warningDelete((SystemMessageViewHolder) holder);
            return null;
        }
    };

    private void warningDelete(final SystemMessageViewHolder holder) {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_system_message_delete_warning, R.string.ui_base_text_yes, R.string.cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                NimMessage msg = mAdapter.get(holder.getAdapterPosition());
                mAdapter.remove(msg);
                removeCache(msg.getUuid());
                removePushMessage(msg.getUuid());
                return true;
            }
        }, null);
    }

    private void removeCache(String uuid) {
        NimMessage.deleteByUuid(uuid);
        NimApplication.dispatchCallbacks();
    }

    private void removePushMessage(String uuid) {
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<NimMessage>() {
            @Override
            public void onResponse(NimMessage nimMessage, boolean success, String message) {
                super.onResponse(nimMessage, success, message);
            }
        }).delete(uuid);
    }

    private class MessageAdapter extends RecyclerViewAdapter<SystemMessageViewHolder, NimMessage> {

        @Override
        public SystemMessageViewHolder onCreateViewHolder(View itemView, int viewType) {
            SystemMessageViewHolder holder = new SystemMessageViewHolder(itemView, SystemMessageFragment.this);
            // 删除
            holder.addOnHandlerBoundDataListener(handlerBoundDataListener);
            // 点击
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_system_message_deleteable;
        }

        @Override
        public void onBindHolderOfView(SystemMessageViewHolder holder, int position, @Nullable NimMessage item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(NimMessage item1, NimMessage item2) {
            return 0;
        }

        public NimMessage query(String uuid) {
            Iterator<NimMessage> iterator = iterator();
            while (iterator.hasNext()) {
                NimMessage msg = iterator.next();
                if (!isEmpty(msg.getUuid()) && msg.getUuid().equals(uuid)) {
                    return msg;
                }
            }
            return null;
        }
    }
}
