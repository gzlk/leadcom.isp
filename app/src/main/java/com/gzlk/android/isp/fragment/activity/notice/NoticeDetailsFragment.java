package com.gzlk.android.isp.fragment.activity.notice;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.api.activity.AppNoticeRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.activity.AppNotice;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>单条通知详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 09:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 09:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NoticeDetailsFragment extends BaseTransparentSupportFragment {

    public static NoticeDetailsFragment newInstance(String params) {
        NoticeDetailsFragment ndf = new NoticeDetailsFragment();
        Bundle bundle = new Bundle();
        // 通知的id
        bundle.putString(PARAM_QUERY_ID, params);
        ndf.setArguments(bundle);
        return ndf;
    }

    public static void open(BaseFragment fragment, String noticeId) {
        fragment.openActivity(NoticeDetailsFragment.class.getName(), noticeId, true, false);
    }

    public static void open(Context context, int requestCode, String noticeId) {
        BaseActivity.openActivity(context, NoticeDetailsFragment.class.getName(), noticeId, requestCode, true, false);
    }

    private String[] items;
    @ViewId(R.id.ui_activity_notice_details_title)
    private View titleView;
    @ViewId(R.id.ui_activity_notice_details_author)
    private View authorView;
    @ViewId(R.id.ui_activity_notice_details_time)
    private View timeView;
    @ViewId(R.id.ui_activity_notice_details_content)
    private ExpandableTextView contentView;

    private SimpleClickableViewHolder titleHolder, authorHolder, timeHolder;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_notice_details;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_notice_creator_fragment_title);
        setLoadingText(R.string.ui_activity_notice_details_loading);
        setNothingText(R.string.ui_activity_notice_details_not_exists);
        setRightIcon(R.string.ui_icon_refresh);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                loadingNotice();
            }
        });
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_notice_details_items);
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, this);
        }
        titleHolder.showContent(format(items[0], ""));
        if (null == authorHolder) {
            authorHolder = new SimpleClickableViewHolder(authorView, this);
        }
        authorHolder.showContent(format(items[1], ""));
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            loadingNotice();
        }
        timeHolder.showContent(format(items[2], ""));
    }

    private void resetHolders(AppNotice notice) {
        titleHolder.showContent(format(items[0], notice.getTitle()));
        authorHolder.showContent(format(items[1], notice.getCreatorName()));
        timeHolder.showContent(format(items[2], formatDateTime(notice.getCreateDate())));
        contentView.setText(notice.getContent());
        contentView.makeExpandable();
    }

    private void loadingNotice() {
        displayLoading(true);
        displayNothing(false);
        AppNoticeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppNotice>() {
            @Override
            public void onResponse(AppNotice appNotice, boolean success, String message) {
                super.onResponse(appNotice, success, message);
                if (success) {
                    if (null != appNotice) {
                        resetHolders(appNotice);
                    } else {
                        closeWithWarning(R.string.ui_activity_notice_details_not_exists);
                    }
                } else {
                    closeWithWarning(R.string.ui_activity_notice_details_not_exists);
                }
                displayLoading(false);
            }
        }).find(mQueryId);
    }
}
