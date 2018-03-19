package com.leadcom.android.isp.fragment.organization.archive;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.ActArchiveRequest;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.activity.ActArchive;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>存档活动附件页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 01:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 01:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityArchivingFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_SELECT_ALL = "aaf_select_all_";
    private static final String PARAM_SEARCHED = "aaf_last_searched";
    private static final String PARAM_ARCHIVE_ID = "aaf_archive_id";

    public static ActivityArchivingFragment newInstance(String params) {
        ActivityArchivingFragment af = new ActivityArchivingFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 活动档案的id
        bundle.putString(PARAM_ARCHIVE_ID, strings[1]);
        af.setArguments(bundle);
        return af;
    }

    private String searchedText = "";
    private String mArchiveId = "";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isSelectAll = bundle.getBoolean(PARAM_SELECT_ALL, false);
        searchedText = bundle.getString(PARAM_SEARCHED, "");
        mArchiveId = bundle.getString(PARAM_ARCHIVE_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SELECT_ALL, isSelectAll);
        bundle.putString(PARAM_SEARCHED, searchedText);
        bundle.putString(PARAM_ARCHIVE_ID, mArchiveId);
    }

    @ViewId(R.id.ui_tool_view_select_all_root)
    private View selectAllView;
    @ViewId(R.id.ui_tool_view_select_all_icon)
    private CustomTextView selectAllIcon;
    @ViewId(R.id.ui_archive_approve_reject)
    private CorneredView rejectView;
    @ViewId(R.id.ui_archive_approve_reject_text)
    private TextView rejectTextView;
    @ViewId(R.id.ui_archive_approve_passed)
    private CorneredView passView;
    @ViewId(R.id.ui_archive_approve_container)
    private LinearLayout approveContainer;

    private boolean isSelectAll = false;
    private ArchiveAdapter mAdapter;
    private ArrayList<ActArchive> archives = new ArrayList<>();

    private InputableSearchViewHolder inputableSearchViewHolder;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_archive_management_title_button_2);
        rejectTextView.setText(R.string.ui_activity_archiving_save_to_archive);
        passView.setVisibility(View.GONE);
        approveContainer.setVisibility(View.VISIBLE);
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_approve;
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
        fetchingUnArchived();
    }

    @Override
    protected void onLoadingMore() {
        fetchingUnArchived();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_tool_view_select_all_root,
            R.id.ui_archive_approve_reject,
            R.id.ui_archive_approve_passed})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_select_all_root:
                // 全选或全部选
                isSelectAll = !isSelectAll;
                resetSelectAll();
                break;
            case R.id.ui_archive_approve_reject:
                if (rejectView.isEnabled()) {
                    rejectView.setEnabled(false);
                    // 存档文件
                    archiveSelectedArchives();
                }
                break;
            case R.id.ui_archive_approve_passed:
                // 本页面没有用到这个按钮
                break;
        }
    }

    private int archivingIndex = 0;

    /**
     * 开始存档全选的文件
     */
    private void archiveSelectedArchives() {
        if (getUnArchived() > 0) {
            setLoadingText(R.string.ui_activity_archiving_update);
            archivingIndex = 0;
            archivingArchive();
        } else {
            ToastHelper.make().showMsg(R.string.ui_activity_archive_no_more_file);
            rejectView.setEnabled(false);
            // 没有文件可以存档，此时将活动存为档案
            archivingActivityAsArchive();
        }
    }

    private int getUnArchived() {
        int cnt = 0;
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            ActArchive archive = (ActArchive) mAdapter.get(i);
            if (archive.isSelected()) {
                // 如果是选中状态则看其是否还未存档
                if (archive.getStatus() <= Attachment.AttachmentStatus.ARCHIVING) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    /**
     * 递归存档所有选中的未存档内容
     */
    private void archivingArchive() {
        // 获取选中的文件中未存档的项目统计数量
        int unarchived = getUnArchived();
        if (unarchived > 0) {
            ActArchive archive = (ActArchive) mAdapter.get(archivingIndex);
            if (archive.isSelected() && archive.getStatus() <= Attachment.AttachmentStatus.ARCHIVING) {
                archiveActivityArchive(archive.getId(), archive.getActId(), archivingIndex);
            } else {
                archivingIndex++;
                archivingArchive();
            }
        } else {
            // 文件存档完毕，把活动存为档案
            ToastHelper.make().showMsg(R.string.ui_activity_archive_file_complete);
            rejectView.setEnabled(false);
            archivingActivityAsArchive();
        }
    }

    /**
     * 将本活动当作文档存档
     */
    private void archivingActivityAsArchive() {
        setLoadingText(R.string.ui_activity_archive_archiving_activity);
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                displayLoading(false);
                if (success) {
                    // 存档完毕后返回上一页
                    resultSucceededActivity();
                } else {
                    rejectView.setEnabled(true);
                    ToastHelper.make().showMsg(message);
                }
            }
        }).archive(mArchiveId, Archive.ArchiveStatus.APPROVED);
    }

    // 重置全选或全不选状态
    private void resetSelectAll() {
        selectAllIcon.setTextColor(getColor(isSelectAll ? R.color.colorPrimary : R.color.textColorHintLight));
        int selectedCount = 0;
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            ActArchive archive = (ActArchive) mAdapter.get(i);
            if (archive.getStatus() <= Attachment.AttachmentStatus.ARCHIVING) {
                archive.setSelected(isSelectAll);
                mAdapter.notifyItemChanged(i);
                if (archive.isSelected()) {
                    selectedCount++;
                }
            }
        }
        // 全选选中了东西之后才可以显示更新
        //rejectView.setEnabled(isSelectedSomeThing);
        if (selectedCount > 10) {
            ToastHelper.make().showMsg(R.string.ui_activity_archiving_selected_too_much);
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            ActArchive archive = (ActArchive) mAdapter.get(index);
            if (archive.getStatus() <= Attachment.AttachmentStatus.ARCHIVING) {
                // 没有存档则存档
                archiveActivityArchive(archive.getId(), archive.getActId(), index);
            } else {
                ToastHelper.make().showMsg(R.string.ui_activity_archive_archived_file);
            }
        }
    };

    /**
     * 存档活动里的文件
     */
    private void archiveActivityArchive(String fileId, String actId, final int index) {
        setLoadingText(R.string.ui_activity_archive_archiving_file);
        displayLoading(true);
        displayNothing(false);
        ActArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ActArchive>() {
            @Override
            public void onResponse(ActArchive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                displayLoading(false);
                if (success) {
                    //mAdapter.remove(index);
                    //rejectView.setEnabled(getUnArchived() > 0);
                    ActArchive acv = (ActArchive) mAdapter.get(index);
                    // 存档成功
                    acv.setStatus(Attachment.AttachmentStatus.ARCHIVED);
                    acv.setSelected(true);
                    mAdapter.notifyItemChanged(index);
                    if (isSelectAll) {
                        archivingIndex++;
                        // 如果是全选状态下则进行下一个未存档的文件
                        archivingArchive();
                    }
                } else {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).update(fileId, actId, Attachment.AttachmentStatus.ARCHIVED);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter(this);
            mAdapter.setOnViewHolderClickListener(onViewHolderClickListener);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (null == inputableSearchViewHolder) {
            inputableSearchViewHolder = new InputableSearchViewHolder(mRootView, this);
            inputableSearchViewHolder.setOnSearchingListener(onSearchingListener);
            fetchingUnArchived();
        }
    }

    private InputableSearchViewHolder.OnSearchingListener onSearchingListener = new InputableSearchViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            if (StringHelper.isEmpty(text)) {
                searchedText = "";
                loadLocalArchives();
            } else {
                searchedText = text;
                loadLocalArchives();
            }
        }
    };

    private void fetchingUnArchived() {
        setLoadingText(R.string.ui_activity_archive_loading_all);
        displayLoading(true);
        displayNothing(false);
        ActArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<ActArchive>() {
            @Override
            public void onResponse(List<ActArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        addIntoList(list);
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
        }).list(mQueryId, 0, remotePageNumber);
    }

    private void addIntoList(List<ActArchive> list) {
        //archives.clear();
        for (ActArchive archive : list) {
            // 未存档
            archive.setSelectable(true);
            // 已存档的设置成选中状态
            archive.setSelected(archive.getStatus() > Attachment.AttachmentStatus.ARCHIVING);

            if (!archives.contains(archive)) {
                archives.add(archive);
            } else {
                int index = archives.indexOf(archive);
                archives.set(index, archive);
            }
        }
        loadLocalArchives();
    }

    private void loadLocalArchives() {
        mAdapter.clear();
        mAdapter.setSearchingText(searchedText);
        for (ActArchive archive : archives) {
            // 已审核的和审核失败的都不显示
//            if (archive.getStatus() > Attachment.AttachmentStatus.ARCHIVING) {
//                continue;
//            }
            if (!isEmpty(searchedText)) {
                // 文件名搜索
                if (!archive.getName().contains(searchedText)) {
                    continue;
                }
            }
            // 默认是否为全选状态
            //archive.setSelected(isSelectAll);
            mAdapter.update(archive);
        }
        displayNothing(mAdapter.getItemCount() <= 0);
    }
}
