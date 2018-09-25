package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Member;

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

    public static void open(BaseFragment fragment, String replyId) {
        Bundle bundle = getBundle(replyId, null);
        fragment.openActivity(ActivityReplyFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_group_activity_reply_subject)
    private View subjectView;
    @ViewId(R.id.ui_group_activity_reply_title)
    private View titleView;
    @ViewId(R.id.ui_group_activity_reply_content)
    private CorneredEditText contentView;
    private SimpleInputableViewHolder subjectHolder;
    private SimpleClickableViewHolder titleHolder;
    private Archive mArchive;
    private String[] items;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mArchive = (Archive) bundle.getSerializable(PARAM_JSON);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
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
                tryReplyActivity();
            }
        });
    }

    private void loadingActivityReplyList() {
        displayLoading(true);
        displayNothing(false);
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
                    resetRightEvent(replied ? 0 : R.string.ui_base_text_complete);
                    if (!replied) {
                        loadingDefaultReplyContent();
                    } else {
                        loadingReplyContent(replyId);
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
                    displayReplyContent(archive.getReply(), archive.getTitle(), archive.getContent());
                    subjectHolder.setEditable(false);
                    contentView.setFocusable(false);
                    contentView.setLongClickable(false);
                } else {
                    setNothingText(message + "\n(点击重新加载)");
                    displayNothing(true);
                }
                displayLoading(false);
            }
        }).fetchingActivityReplyContent(replyId);
    }

    private void displayReplyContent(String subject, String title, String content) {
        subjectHolder.showContent(format(items[0], subject));
        subjectHolder.focusEnd();
        titleHolder.showContent(new SimpleClickableItem(format(items[1], title)));
        contentView.setText(content);
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
