package com.leadcom.android.isp.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.common.PushMsgRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.home.SystemMessageViewHolder;
import com.leadcom.android.isp.listener.NotificationChangeHandleCallback;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.common.PushMessage;
import com.leadcom.android.isp.receiver.LaserCustomMessageReceiver;
import com.leadcom.android.isp.view.SwipeItemLayout;

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

    private static final String PARAM_IS_IN_MAIN = "smf_is_in_main";
    private static final String PARAM_IS_REFRESHABLE = "smf_is_refreshable";

    public static SystemMessageFragment newInstance(Bundle bundle) {
        SystemMessageFragment smf = new SystemMessageFragment();
        smf.setArguments(bundle);
        return smf;
    }

    private static Bundle getBundle(boolean inMain) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAM_IS_IN_MAIN, inMain);
        return bundle;
    }

    public static SystemMessageFragment getInstance(boolean isInMain) {
        return newInstance(getBundle(isInMain));
    }

    public static void open(Context context) {
        BaseActivity.openActivity(context, SystemMessageFragment.class.getName(), getBundle(false), true, false);
    }

    public static void open(BaseFragment fragment) {
        fragment.openActivity(SystemMessageFragment.class.getName(), getBundle(false), true, false);
    }

    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleText;
    @ViewId(R.id.ui_ui_custom_title_right_icon)
    private CustomTextView rightIconView;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightTextView;
    private MessageAdapter mAdapter;
    private boolean isInMainPage = false;
    private boolean isRefreshable = true;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isInMainPage = bundle.getBoolean(PARAM_IS_IN_MAIN, false);
        isRefreshable = bundle.getBoolean(PARAM_IS_REFRESHABLE, true);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_IS_IN_MAIN, isInMainPage);
        bundle.putBoolean(PARAM_IS_REFRESHABLE, isRefreshable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.addNotificationChangeCallback(callback);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int color = getColor(Cache.sdk >= 23 ? R.color.textColor : R.color.textColorLight);
        rightTextView.setTextColor(color);
        rightIconView.setTextColor(color);
        titleText.setTextColor(color);
        rightIconView.setVisibility(isInMainPage ? View.GONE : View.VISIBLE);
        rightTextView.setVisibility(isInMainPage ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        App.removeNotificationChangeCallback(callback);
        super.onDestroy();
    }

    private NotificationChangeHandleCallback callback = new NotificationChangeHandleCallback() {
        @Override
        public void onChanged() {
            if (isRefreshable) {
                // 重新拉取推送消息列表
                onSwipeRefreshing();
            } else {
                isRefreshable = true;
            }
        }
    };

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        if (!isInMainPage) {
            setCustomTitle(R.string.ui_system_message_fragment_title);
        }
        setNothingText(R.string.ui_system_message_nothing);
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return !isInMainPage;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_recent_contacts;
    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        setSupportLoadingMore(true);
        fetchingPushMessages();
    }

    @Override
    protected void onLoadingMore() {
        fetchingPushMessages();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_ui_custom_title_right_text})
    private void viewClick(View view) {
        view.startAnimation(App.clickAnimation());
        warningClear();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MessageAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(mAdapter);
            if (!isInMainPage) {
                setRightIcon(R.string.ui_icon_delete);
                setRightText(R.string.ui_base_text_clear);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        warningClear();
                    }
                });
            }
            fetchingPushMessages();
        }
    }

    private void warningClear() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                clearPushMessage();
                return true;
            }
        }).setConfirmText(R.string.ui_base_text_clear).setTitleText(R.string.ui_system_message_clear_warning).show();
    }

    private void clearPushMessage() {
        displayNothing(false);
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<PushMessage>() {
            @Override
            public void onResponse(PushMessage pushMessage, boolean success, String message) {
                super.onResponse(pushMessage, success, message);
                if (success) {
                    App.dispatchCallbacks();
                    mAdapter.clear();
                }
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).clean("");
    }

    private void fetchingPushMessages() {
        displayLoading(true);
        displayNothing(false);
        PushMsgRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<PushMessage>() {
            @Override
            public void onResponse(List<PushMessage> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    mAdapter.clear();
                }
                int count = null == list ? 0 : list.size();
                remotePageNumber += count < pageSize ? 0 : 1;
                isLoadingComplete(count < pageSize);
                if (success && null != list) {
                    mAdapter.update(list, false);
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
            }
        }).list("", remotePageNumber);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            PushMessage message = mAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_holder_view_system_message_container:
                    // 打开查看详情
                    if (message.isRead()) {
                        // 已读时，直接打开查看详情
                        showDetailsPage(message);
                    } else {
                        // 未读时，先设置为已读，然后再打开查看详情
                        updatePushMessage(message.getId());
                    }
                    break;
                case R.id.ui_tool_view_contact_button2:
                    // 删除
                    warningDelete(index);
                    break;
            }
        }
    };

    private void showDetailsPage(PushMessage message) {
        PushMessage.Extra extra = message.getExtras();
        extra.setMsgId(message.getId());
        LaserCustomMessageReceiver.switchUI(Activity(), extra);
    }

    private void updatePushMessage(final String msgId) {
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<PushMessage>() {
            @Override
            public void onResponse(PushMessage pushMessage, boolean success, String message) {
                super.onResponse(pushMessage, success, message);
                if (success) {
                    if (null != pushMessage) {
                        mAdapter.update(pushMessage);
                        showDetailsPage(pushMessage);
                    }
                    App.app().setUnreadCount(App.app().getUnreadCount() - 1);
                    // 下一次 dispatch 不需要刷新消息列表
                    isRefreshable = false;
                    App.dispatchCallbacks();
                }
            }
        }).find(msgId);
    }

    private void warningDelete(final int index) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                PushMessage msg = mAdapter.get(index);
                removePushMessage(msg.getId(), index);
                return true;
            }
        }).setTitleText(R.string.ui_system_message_delete_warning).setConfirmText(R.string.ui_base_text_delete).show();
    }

    private void removePushMessage(final String msgId, final int index) {
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<PushMessage>() {
            @Override
            public void onResponse(PushMessage nimMessage, boolean success, String message) {
                super.onResponse(nimMessage, success, message);
                if (success) {
                    mAdapter.remove(msgId);
                    // 下一次 dispatch 不需要刷新消息列表
                    isRefreshable = false;
                    App.dispatchCallbacks();
                }
            }
        }).delete(msgId);
    }

    private class MessageAdapter extends RecyclerViewAdapter<SystemMessageViewHolder, PushMessage> {

        @Override
        public SystemMessageViewHolder onCreateViewHolder(View itemView, int viewType) {
            SystemMessageViewHolder holder = new SystemMessageViewHolder(itemView, SystemMessageFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_system_message_deleteable;
        }

        @Override
        public void onBindHolderOfView(SystemMessageViewHolder holder, int position, @Nullable PushMessage item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(PushMessage item1, PushMessage item2) {
            return 0;
        }
    }
}
