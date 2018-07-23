package com.leadcom.android.isp.fragment.archive;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.archive.Archive;

/**
 * <b>功能描述：</b>回复流转档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/23 10:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/23 10:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveReplyFragment extends BaseTransparentSupportFragment {

    private static final String PARAM_TITLE = "arf_title";
    private static final String PARAM_GROUP_NAME = "arf_group_name";
    private static final String PARAM_CREATE_TIME = "arf_create_time";
    private static final String PARAM_CONTENT = "arf_content";

    public static ArchiveReplyFragment newInstance(Bundle bundle) {
        ArchiveReplyFragment arf = new ArchiveReplyFragment();
        arf.setArguments(bundle);
        return arf;
    }

    public static void open(BaseFragment fragment, String archiveId, String title, String groupName, String createTime, String content) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, archiveId);
        bundle.putString(PARAM_TITLE, title);
        bundle.putString(PARAM_GROUP_NAME, groupName);
        bundle.putString(PARAM_CREATE_TIME, createTime);
        bundle.putString(PARAM_CONTENT, content);
        fragment.openActivity(ArchiveReplyFragment.class.getName(), bundle, true, true);
    }

    @ViewId(R.id.ui_archive_reply_subject)
    private View titleView;
    @ViewId(R.id.ui_archive_reply_recipient)
    private View recipientView;
    @ViewId(R.id.ui_archive_reply_content)
    private CorneredEditText contentView;
    @ViewId(R.id.ui_archive_reply_source_origin)
    private TextView sourceSender;
    @ViewId(R.id.ui_archive_reply_source_title)
    private TextView sourceTitle;
    @ViewId(R.id.ui_archive_reply_source_time)
    private TextView sourceTime;
    @ViewId(R.id.ui_archive_reply_source_content)
    private TextView sourceContent;
    private String mTitle, mGroupName, mCreateTime, mContent;
    private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder recipientHolder;
    private String[] items;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(R.string.ui_base_text_reply);
        setRightText(R.string.ui_base_text_finish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 提交档案的回复
                tryReplyArchive();
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_reply;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(PARAM_TITLE, "");
        mGroupName = bundle.getString(PARAM_GROUP_NAME, "");
        mCreateTime = bundle.getString(PARAM_CREATE_TIME, "");
        mContent = bundle.getString(PARAM_CONTENT, "");
    }

    @Override
    public void doingInResume() {
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TITLE, mTitle);
        bundle.putString(PARAM_GROUP_NAME, mGroupName);
        bundle.putString(PARAM_CREATE_TIME, mCreateTime);
        bundle.putString(PARAM_CONTENT, mContent);
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected boolean checkStillEditing() {
        return super.checkStillEditing();
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_text_archive_reply_items);
        }
        if (null == recipientHolder) {
            recipientHolder = new SimpleClickableViewHolder(recipientView, this);
            recipientHolder.showContent(format(items[0], mGroupName));
        }
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
            titleHolder.showContent(format(items[1], mTitle));
        }
        sourceSender.setText(Html.fromHtml(getString(R.string.ui_text_archive_reply_source_sender, mGroupName)));
        sourceTitle.setText(Html.fromHtml(getString(R.string.ui_text_archive_reply_source_title, mTitle)));
        sourceTime.setText(getString(R.string.ui_text_archive_reply_source_time, formatDate(mCreateTime, R.string.ui_base_text_date_time_format_hhmm)));
        sourceContent.setText(EmojiUtility.getEmojiString(sourceContent.getContext(), mContent.replaceAll("<img.*?>", ""), true));
    }

    private void tryReplyArchive() {
        String title = titleHolder.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_reply_title_blank);
            return;
        }
        String content = contentView.getValue();
        if (isEmpty(content)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_reply_content_blank);
            return;
        }
        replyArchive(title, content);
    }

    private void replyArchive(String title, String content) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_reply_success);
                    finish();
                }
            }
        }).reply(mQueryId, title, content);
    }
}
