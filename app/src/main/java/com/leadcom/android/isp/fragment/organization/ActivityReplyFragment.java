package com.leadcom.android.isp.fragment.organization;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Member;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>功能描述：</b>下级组织的活动回复页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/23 23:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityReplyFragment extends GroupBaseFragment {

    private static final String PARAM_ACT_ID = "arf_activity_id";

    public static ActivityReplyFragment newInstance(Bundle bundle) {
        ActivityReplyFragment arf = new ActivityReplyFragment();
        arf.setArguments(bundle);
        return arf;
    }

    private static Bundle getBundle(String archiveId, Archive archive) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, archiveId);
        bundle.putSerializable(PARAM_JSON, archive);
        return bundle;
    }

    public static void open(BaseFragment fragment, Archive archive) {
        Bundle bundle = getBundle(archive.getId(), archive);
        fragment.openActivity(ActivityReplyFragment.class.getName(), bundle, REQUEST_CREATE, true, false);
    }

    public static void open(BaseFragment fragment, String replyId, String activityId) {
        Bundle bundle = getBundle(replyId, null);
        bundle.putString(PARAM_ACT_ID, activityId);
        fragment.openActivity(ActivityReplyFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_group_activity_reply_subject)
    private View subjectView;
    @ViewId(R.id.ui_group_activity_reply_title)
    private View titleView;
    @ViewId(R.id.ui_group_activity_reply_content)
    private CorneredEditText contentView;
    @ViewId(R.id.ui_group_activity_reply_content_text)
    private TextView contentTextView;
    private SimpleInputableViewHolder subjectHolder;
    private SimpleClickableViewHolder titleHolder;
    private Archive mArchive;
    private String mActivityId, mGroupId;
    private String[] items;
    private int notResponse = 0;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mArchive = (Archive) bundle.getSerializable(PARAM_JSON);
        mActivityId = bundle.getString(PARAM_ACT_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_ACT_ID, mActivityId);
        bundle.putSerializable(PARAM_JSON, mArchive);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(R.string.ui_group_activity_reply_fragment_title);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_group_activity_reply;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Click({R.id.ui_tool_nothing_container})
    private void viewClick(View view) {
        loadingDefaultReplyContent();
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_group_activity_reply_items);
        }
        if (null == subjectHolder) {
            subjectHolder = new SimpleInputableViewHolder(subjectView, this);
            subjectHolder.showContent(format(items[0], ""));
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, this);
            titleHolder.showContent(new SimpleClickableItem(format(items[1], "")));
            // 拉取回复列表，看看本组织是否已经回复过
            if (null == mArchive) {
                setCustomTitle(R.string.ui_group_activity_reply_fragment_title_replied);
                loadingReplyContent(mQueryId);
            } else {
                loadingActivityReplyList();
            }
        }
    }

    private void resetRightEvent(int text) {
        setRightText(text);
        if (0 == text) {
            return;
        }
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (notResponse > 0) {
                    warningReplyHasNoResponse();
                } else {
                    tryReplyActivity();
                }
            }
        });
    }

    private void loadingActivityReplyList() {
        displayLoading(true);
        displayNothing(false);
        //if (isActivityNotAtTime()) {
        //loadingDefaultReplyContent();
        //resetRightEvent(R.string.ui_base_text_complete);
        //return;
        //}
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    // 查询列表中是否有本组织的回复内容，如有则显示出来，没有则可以编辑内容
                    boolean replied = false;
                    String replyId = "";
                    for (Member member : archive.getGroActivityReplyList()) {
                        if (mArchive.getGroupId().equals(member.getGroupId())) {
                            // 已回复，显示已回复的内容，且不能再回复
                            replied = true;
                            replyId = member.getId();
                            break;
                        }
                    }
                    resetRightEvent(replied ? (isActivityNotAtTime() ? R.string.ui_base_text_complete : 0) : R.string.ui_base_text_complete);
                    if (!replied || isActivityNotAtTime()) {
                        // 没有回复或者活动还未结束，则可以继续回复
                        loadingDefaultReplyContent();
                    } else {
                        mQueryId = replyId;
                        mActivityId = mArchive.getId();
                        loadingReplyContent(mQueryId);
                    }
                }
            }
        }).listActivitySubordinateMember(mArchive.getFromGroupId(), mArchive.getId());
    }

    private void loadingDefaultReplyContent() {
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    displayReplyContent(archive.getReply(), archive.getTitle(), archive.getContent());
                } else {
                    setNothingText(message + "\n(点击重新加载)");
                    displayNothing(true);
                }
                displayLoading(false);
            }
        }).fetchingActivityDefaultReplyContent(mArchive.getGroupId(), mArchive.getId());
    }

    private void loadingReplyContent(String replyId) {
        displayNothing(false);
        displayLoading(true);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    String title = StringHelper.getString(R.string.ui_group_activity_reply_fragment_title_replied);
                    title = format("%s(%s)", title, archive.getGroupName());
                    setCustomTitle(title);
                    mGroupId = archive.getGroupId();
                    displayReplyContent(archive.getReply(), archive.getTitle(), archive.getContent());
                    subjectHolder.setEditable(isActivityNotAtTime());
                    contentView.setFocusable(isActivityNotAtTime());
                    contentView.setLongClickable(isActivityNotAtTime());
                } else {
                    setNothingText(message + "\n(点击重新加载)");
                    displayNothing(true);
                }
                displayLoading(false);
            }
        }).fetchingActivityReplyContent(replyId);
    }

    private void getNotResponse(String content) {
        if (isEmpty(content)) {
            notResponse = 0;
            return;
        }
        Matcher matcher = Pattern.compile("应\\d+人", Pattern.CASE_INSENSITIVE).matcher(content);
        if (matcher.find()) {
            String matched = matcher.group(0);
            Matcher number = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE).matcher(matched);
            if (number.find()) {
                String find = number.group(0);
                try {
                    notResponse = Integer.valueOf(find);
                } catch (Exception e) {
                    e.printStackTrace();
                    notResponse = 0;
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void displayReplyContent(String subject, String title, String content) {
        getNotResponse(content);
        subjectHolder.showContent(format(items[0], subject));
        subjectHolder.focusEnd();
        titleHolder.showContent(new SimpleClickableItem(format(items[1], title)));
        contentView.setText(content);
        contentView.setOnTouchListener(onTouchListener);
        if (!isEmpty(mActivityId)) {
            // 查看回复详情时需要列表报名情况
            //initializeAdapter();
            if (isActivityNotAtTime()) {
                resetRightEvent(R.string.ui_base_text_complete);
            } else {
                contentTextView.setText(content);
                contentView.setVisibility(View.GONE);
            }
        }
        if (!isActivityNotAtTime()) {
            resetRightEvent(0);
        } else {
            contentTextView.setText(content);
            contentView.setVisibility(View.GONE);
        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (view instanceof CorneredEditText) {
                if (view.canScrollVertically(-1) || view.canScrollVertically(0)) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }
            return false;
        }

        /**
         * EditText竖直方向能否够滚动
         * @param editText  须要推断的EditText
         * @return true：能够滚动   false：不能够滚动
         */
        @SuppressWarnings("unused")
        private boolean canVerticalScroll(CorneredEditText editText) {
            //滚动的距离
            int scrollY = editText.getScrollY();
            //控件内容的总高度
            int scrollRange = editText.getLayout().getHeight();
            //控件实际显示的高度
            int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
            //控件内容总高度与实际显示高度的差值
            int scrollDifference = scrollRange - scrollExtent;

            if (scrollDifference == 0) {
                return false;
            }

            return (scrollY > 0) || (scrollY < scrollDifference - 1);
        }
    };

    private boolean isActivityNotAtTime() {
        // 查看回复内容，则直接定死活动已过期，只能查看回复内容
        if (null == mArchive) return false;
        long date = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), mArchive.getHappenDate()).getTime();
        long now = System.currentTimeMillis();
        return now < date;
    }

    private void warningReplyHasNoResponse() {
        String title = StringHelper.getString(isActivityNotAtTime() ? R.string.ui_group_activity_reply_has_no_response_member : R.string.ui_group_activity_reply_has_no_response_member_timeout, notResponse);
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryReplyActivity();
                return true;
            }
        }).setTitleText(title).setConfirmText(isActivityNotAtTime() ? R.string.ui_base_text_continue : R.string.ui_base_text_reply).setCancelText(R.string.ui_base_text_cancel).show();
    }

    private void tryReplyActivity() {
        mArchive.setReply(subjectHolder.getValue());
        if (isEmpty(mArchive.getReply())) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_reply_subject_is_null);
            return;
        }
        mArchive.setContent(contentView.getValue());
        if (isEmpty(mArchive.getContent())) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_reply_content_is_null);
            return;
        }
        replyActivity();
    }

    private void replyActivity() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_group_activity_reply_success);
                    resultData(archive.getId());
                }
            }
        }).replyActivity(mArchive);
    }
}
