package com.leadcom.android.isp.fragment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.activity.ActivityListItemViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.nim.model.extension.ArchiveAttachment;
import com.leadcom.android.isp.nim.model.extension.MomentAttachment;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.List;


/**
 * <b>功能描述：</b>分享到app内部群聊时，显示我已加入的群聊列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/27 19:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityShareListFragment extends BaseSwipeRefreshSupportFragment {

    public static ActivityShareListFragment newInstance(String params) {
        ActivityShareListFragment asf = new ActivityShareListFragment();
        Bundle bundle = new Bundle();
        // 传过来的shareInfo
        bundle.putString(PARAM_QUERY_ID, params);
        asf.setArguments(bundle);
        return asf;
    }

    public static void open(BaseFragment fragment, ShareInfo share) {
        String json = ShareInfo.toJson(share);
        fragment.openActivity(ActivityShareListFragment.class.getName(), StringHelper.replaceJson(json, false), REQUEST_SELECT, true, false);
    }

    private static int selected = -1;
    private ShareInfo share;
    private ActivityAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selected = -1;
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        if (null == share) {
            setCustomTitle(R.string.ui_base_share_to_app_fragment_title);
            share = ShareInfo.fromJson(StringHelper.replaceJson(mQueryId, true));
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
        stopRefreshing();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ActivityAdapter();
            mRecyclerView.setAdapter(mAdapter);
            queryRecentContact();
        }
    }

    private void queryRecentContact() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallback<List<RecentContact>>() {
            @Override
            public void onSuccess(List<RecentContact> list) {
                if (null != list) {
                    mAdapter.update(list);
                }
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private View shareView;
    private TextView dialogTitle, shareTitle, shareSummary;
    private ImageDisplayer shareImage;

    private void showShareDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == shareView) {
                    shareView = View.inflate(Activity(), R.layout.popup_dialog_share_in_app, null);
                    dialogTitle = shareView.findViewById(R.id.ui_dialog_share_in_app_title);
                    shareTitle = shareView.findViewById(R.id.ui_dialog_share_in_app_title_label);
                    shareSummary = shareView.findViewById(R.id.ui_dialog_share_in_app_summary_label);
                    shareImage = shareView.findViewById(R.id.ui_dialog_share_in_app_image);
                }
                return shareView;
            }

            private String getContactName(RecentContact contact) {
                if (contact.getSessionType() == SessionTypeEnum.Team) {
                    Team team = TeamDataCache.getInstance().getTeamById(contact.getContactId());
                    return team.getName();
                } else if (contact.getSessionType() == SessionTypeEnum.P2P) {
                    UserInfo info = NimUIKit.getUserInfoProvider().getUserInfo(contact.getContactId());
                    return info.getName();
                }
                return "";
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                RecentContact contact = mAdapter.get(selected);
                dialogTitle.setText(getString(R.string.ui_base_share_to_app_dialog_title, getContactName(contact)));
                shareTitle.setText(share.getTitle());
                shareSummary.setText(Html.fromHtml(share.getDescription()));
                shareImage.setVisibility(isEmpty(share.getImageUrl()) ? View.GONE : View.VISIBLE);
                shareImage.displayImage(share.getImageUrl(), getDimension(R.dimen.ui_static_dp_50), false, false);
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                shareToContact();
                return true;
            }
        }).setConfirmText(R.string.ui_base_text_share).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private ArchiveAttachment getArchiveAttachment() {
        ArchiveAttachment attachment = new ArchiveAttachment();
        attachment.setCustomId(share.getId());
        attachment.setArchiveType(share.getDocType());
        attachment.setImage(share.getImageUrl());
        attachment.setTitle(share.getTitle());
        attachment.setSummary(share.getDescription());
        return attachment;
    }

    private MomentAttachment getMomentAttachment() {
        MomentAttachment attachment = new MomentAttachment();
        attachment.setCustomId(share.getId());
        attachment.setImage(share.getImageUrl());
        attachment.setTitle(share.getTitle());
        attachment.setSummary(share.getDescription());
        return attachment;
    }

    private void shareToContact() {
        RecentContact contact = mAdapter.get(selected);
        final String contactId = contact.getContactId();
        final SessionTypeEnum sessionTypeEnum = contact.getSessionType();
        MsgAttachment attachment = share.getContentType() == ShareInfo.ContentType.MOMENT ? getMomentAttachment() : getArchiveAttachment();
        IMMessage message = MessageBuilder.createCustomMessage(contactId, sessionTypeEnum, attachment);
        NIMClient.getService(MsgService.class).sendMessage(message, true);
        delayed(new Runnable() {
            @Override
            public void run() {
                shareInAppSuccess(contactId, sessionTypeEnum == SessionTypeEnum.Team);
            }
        });
    }

    private void delayed(Runnable runnable) {
        Handler().postDelayed(runnable, 100);
    }

    private void shareInAppSuccess(final String contactId, final boolean team) {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return View.inflate(Activity(), R.layout.popup_dialog_share_in_app_success, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                delayed(new Runnable() {
                    @Override
                    public void run() {
                        if (team) {
                            // 返回到群聊页面
                            NimSessionHelper.startTeamSession(Activity(), contactId);
                        } else {
                            // 返回到点对点单聊页面
                            NimSessionHelper.startP2PSession(Activity(), contactId);
                        }
                    }
                });
                return true;
            }
        }).addOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                delayed(new Runnable() {
                    @Override
                    public void run() {
                        // 返回到档案页面
                        finish();
                    }
                });
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            selected = index;
            showShareDialog();
        }
    };

    private class ActivityAdapter extends RecyclerViewAdapter<ActivityListItemViewHolder, RecentContact> {

        @Override
        public ActivityListItemViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityListItemViewHolder holder = new ActivityListItemViewHolder(itemView, ActivityShareListFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.nim_activity_list_item;
        }

        @Override
        public void onBindHolderOfView(ActivityListItemViewHolder holder, int position, @Nullable RecentContact item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(RecentContact item1, RecentContact item2) {
            return 0;
        }
    }
}
