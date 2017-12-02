package com.leadcom.android.isp.fragment.individual;

import android.support.annotation.Nullable;
import android.view.View;

import com.daimajia.swipe.util.Attributes;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewSwipeAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.UserMsgRequest;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.individual.UserMessageViewHolder;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.user.UserMessage;

import java.util.List;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/19 13:05 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/19 13:05 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserMessageFragment extends BaseSwipeRefreshSupportFragment {

    public static UserMessageFragment newInstance(String params) {
        return new UserMessageFragment();
    }

    public static void open(BaseFragment fragment) {
        fragment.openActivity(UserMessageFragment.class.getName(), "", true, false);
    }

    private MsgAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_individual_message_fragment_title);
        setRightText(R.string.ui_base_text_clear);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                warningClear();
            }
        });
        initializeAdapter();
    }

    private void warningClear() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_individual_message_clear_warning, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                clearMessages();
                return true;
            }
        }, null);
    }

    private void clearMessages() {
        UserMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<UserMessage>() {
            @Override
            public void onResponse(UserMessage userMessage, boolean success, String message) {
                super.onResponse(userMessage, success, message);
                if (success) {
                    mAdapter.clear();
                    mAdapter.update(noMore);
                }
            }
        }).clear();
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
        fetchingUserMessages();
    }

    @Override
    protected void onLoadingMore() {
        fetchingUserMessages();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private Model noMore;


    private void fetchingUserMessages() {
        setLoadingText(R.string.ui_individual_message_fetching);
        displayLoading(true);
        UserMsgRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<UserMessage>() {
            @Override
            public void onResponse(List<UserMessage> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                isLoadingComplete(count < pageSize);
                remotePageNumber += count < pageSize ? 0 : 1;
                if (success) {
                    if (remotePageNumber <= 1) {
                        mAdapter.clear();
                    }
                    if (null != list) {
                        for (UserMessage msg : list) {
                            mAdapter.update(msg);
                        }
                    }
                }
                stopRefreshing();
                displayLoading(false);
                if (count < pageSize) {
                    mAdapter.update(noMore);
                }
            }
        }).list(remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            noMore = new Model();
            noMore.setId(StringHelper.getString(R.string.ui_base_text_nothing_more_id));
            noMore.setAccessToken(StringHelper.getString(R.string.ui_base_text_nothing_more));
            mAdapter = new MsgAdapter();
            mAdapter.setMode(Attributes.Mode.Single);
            mRecyclerView.setAdapter(mAdapter);
            fetchingUserMessages();
        }
    }

    // 打开个人信息
    private OnHandleBoundDataListener<UserMessage> boundDataListener = new OnHandleBoundDataListener<UserMessage>() {
        @Override
        public UserMessage onHandlerBoundData(BaseViewHolder holder) {
            int index = holder.getAdapterPosition();
            UserMessage msg = (UserMessage) mAdapter.get(index);
            UserPropertyFragment.open(UserMessageFragment.this, msg.getUserId());
            return null;
        }
    };

    private OnViewHolderElementClickListener onViewHolderClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            UserMessage msg = (UserMessage) mAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_holder_view_individual_user_message_info:
                case R.id.ui_holder_view_individual_user_message_text:
                    // 打开详情
                    switch (msg.getSourceType()) {
                        case UserMessage.SourceType.MOMENT:
                            MomentDetailsFragment.open(UserMessageFragment.this, msg.getSourceId());
                            break;
                        default:
                            int type = msg.getSourceType() == UserMessage.SourceType.USER_ARCHIVE ? Archive.Type.USER : Archive.Type.GROUP;
                            ArchiveDetailsWebViewFragment.open(UserMessageFragment.this, msg.getSourceId(), type);
                            //ArchiveDetailsFragment.open(UserMessageFragment.this, type, msg.getSourceId(), REQUEST_CHANGE);
                            break;
                    }
                    break;
                case R.id.ui_holder_view_individual_user_message_delete:
                    // 删除这条消息
                    deleteMessage(index, msg.getId());
                    break;
            }
        }
    };

    private void deleteMessage(final int index, String msgId) {
        setLoadingText(R.string.ui_individual_message_deleting);
        displayLoading(true);
        UserMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<UserMessage>() {
            @Override
            public void onResponse(UserMessage userMessage, boolean success, String message) {
                super.onResponse(userMessage, success, message);
                if (success) {
                    mAdapter.remove(index);
                }
                displayLoading(false);
            }
        }).delete(msgId);
    }

    private class MsgAdapter extends RecyclerViewSwipeAdapter<BaseViewHolder, Model> {

        private static final int VT_MSG = 0, VT_NO_MORE = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_MSG) {
                UserMessageViewHolder holder = new UserMessageViewHolder(itemView, UserMessageFragment.this);
                holder.addOnHandlerBoundDataListener(boundDataListener);
                holder.setOnViewHolderElementClickListener(onViewHolderClickListener);
                return holder;
            } else {
                return new NothingMoreViewHolder(itemView, UserMessageFragment.this);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            if (viewType == VT_MSG)
                return R.layout.holder_view_individual_user_message;
            return R.layout.holder_view_nothing_more;
        }

        @Override
        public int getItemViewType(int position) {
            if (get(position) instanceof UserMessage)
                return VT_MSG;
            return VT_NO_MORE;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof UserMessageViewHolder) {
                ((UserMessageViewHolder) holder).showContent((UserMessage) item);
            } else if (holder instanceof NothingMoreViewHolder) {
                ((NothingMoreViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.ui_holder_view_individual_user_message_swipe_layout;
        }
    }
}
