package com.gzlk.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.archive.CommentRequest;
import com.gzlk.android.isp.api.archive.LikeRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseChatInputSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.organization.StructureFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveAdditionalViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveCommentViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsHeaderViewHolder;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.listener.OnHandleBoundDataListener;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Additional;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>档案详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 08:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 08:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsFragment extends BaseChatInputSupportFragment {

    private static final String TAG = "document_%s";
    private static final String TYPE = "adf_type";

    public static ArchiveDetailsFragment newInstance(String params) {
        ArchiveDetailsFragment ddf = new ArchiveDetailsFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, Integer.valueOf(strings[0]));
        bundle.putString(PARAM_QUERY_ID, strings[1]);
        ddf.setArguments(bundle);
        return ddf;
    }

    public static void open(BaseFragment fragment, int type, String archiveId, int req) {
        fragment.openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, archiveId), req, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(TYPE, Archive.Type.USER);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(TYPE, archiveType);
    }

    // 默认用户档案
    private int archiveType = Archive.Type.USER;
    private DocumentDetailsAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_details;
    }

    @Override
    public void doingInResume() {
        showAppend = false;
        showRecorder = false;
        super.doingInResume();
        if (isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_document_details_not_exists);
        } else {
            setCustomTitle(R.string.ui_text_document_details_fragment_title);
            initializeAdapter();
        }
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
        refreshingDocument();
    }

    // 刷新当前文档的所有属性
    private void refreshingDocument() {
        //fetchingDocument(true);
        fetchingRemoteComment();
        fetchingRemoteLikes();
    }

    @Override
    protected void onLoadingMore() {
        fetchingRemoteComment();
    }

    @Override
    protected String getLocalPageTag() {
        return format(TAG, mQueryId);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            addOnInputCompleteListener(onInputCompleteListener);
            mAdapter = new DocumentDetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            // 从本地缓存中查找档案
            fetchingDocument(false);
        }
    }

    private boolean isManagerOrMe(Archive archive) {
        boolean isMe = !isEmpty(archive.getUserId()) && archive.getUserId().equals(Cache.cache().userId);
        if (archiveType == Archive.Type.GROUP) {
            Member member = StructureFragment.my;
            // 我在组织内的角色可以编辑档案的话，显示编辑按钮
            return isMe || (null != member && member.archiveEditable());
        }
        return isMe;
    }

    private void resetRightTitleButton(@NonNull Archive archive) {
        // 档案创建者、组织档案管理者可以编辑
        if (isManagerOrMe(archive)) {
            //setRightIcon(R.string.ui_icon_more);
            // 个人档案可以编辑、未审核的档案可以编辑
            //if (archive.getType() == Archive.Type.USER || archive.getStatus() <= Archive.ArchiveStatus.APPROVED) {
            // 未审核之前的档案可以编辑
//                if (archive.getType() == Archive.ArchiveType.ACTIVITY) {
//                    // 活动产生的档案不需要再编辑
//                    setRightText(0);
//                } else {
            setRightText(R.string.ui_base_text_edit);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    openEditSelector();
                    //openActivity(ArchiveNewFragment.class.getName(), format("%d,%s", archiveType, mQueryId), true, true);
                }
            });
//                }
            //}
        }
        // 用户档案不需要审核
//        if (archive.getType() == Archive.Type.USER) {
//            approveContainer.setVisibility(View.GONE);
//        } else {
//            // 未审核过的档案才显示审核按钮
//            approveContainer.setVisibility(archive.getStatus() <= Archive.ArchiveStatus.APPROVING ? View.VISIBLE : View.GONE);
//        }
        // 图片和文件附件列表
        loadingAttachments(archive);
        fetchingRemoteComment();
        // 拉取远程赞列表
        fetchingRemoteLikes();
    }

    private void loadingAttachments(ArrayList<Attachment> list) {
        if (null != list && list.size() > 0) {
            for (Attachment att : list) {
                mAdapter.update(att);
            }
        }
    }

    private void loadingAttachments(final Archive archive) {
        // office 文件列表
        loadingAttachments(archive.getOffice());
        // image
        loadingAttachments(archive.getImage());
        // video
        loadingAttachments(archive.getVideo());
        // other
        loadingAttachments(archive.getAttach());
        // 增加Additional
        mAdapter.update(new Additional() {{
            setId(archive.getId() + "_1");
            setReadNum(archive.getReadNum());
            setLikeNum(archive.getLikeNum());
            setCmtNum(archive.getCmtNum());
            setColNum(archive.getColNum());
        }});
    }

    private void updateAdapter(List<Comment> list) {
        if (null != list) {
            for (Comment comment : list) {
                mAdapter.update(comment);
            }
        }
    }

    private void fetchingRemoteLikes() {
        LikeRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(List<ArchiveLike> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
            }
        }).list(archiveType, mQueryId, remotePageNumber);
    }

    private void fetchingRemoteComment() {
        setLoadingText(R.string.ui_text_document_details_loading_comments);
        displayLoading(true);
        CommentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Comment>() {

            @Override
            public void onResponse(List<Comment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            // 如果取满了一页，则下次需要拉取下一页
                            remotePageNumber += 1;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        if (list.size() > 0) {
                            updateAdapter(list);
                        }
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayLoading(false);
                stopRefreshing();
                smoothScrollToBottom(mAdapter.getItemCount() - 1);
            }
        }).list(commentType(), mQueryId, remotePageNumber);
    }

    public void showLoadingContent(boolean shown) {
        if (shown) {
            showImageHandlingDialog(R.string.ui_text_document_details_loading_document_content);
        } else {
            hideImageHandlingDialog();
        }
    }

    private int commentType() {
        return archiveType == Archive.Type.USER ? Comment.Type.USER : Comment.Type.GROUP;
    }

    private boolean archiveDeletable() {
        Member member = StructureFragment.my;
        return null != member && member.archiveDeletable();
    }

    private void openEditSelector() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return View.inflate(Activity(), R.layout.popup_dialog_edit_selector, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_editor_to_change, R.id.ui_dialog_button_editor_to_delete};
            }

            @Override
            public boolean onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.ui_dialog_button_editor_to_change:
                        //openActivity(ArchiveNewFragment.class.getName(), format("%d,%s", archiveType, mQueryId), true, true);
                        openActivity(ArchiveCreatorFragment.class.getName(), format("%d,%s", archiveType, mQueryId), true, true);
                        break;
                    case R.id.ui_dialog_button_editor_to_delete:
                        if (archiveType == Archive.Type.USER || archiveDeletable()) {
                            warningDeleteDocument();
                        } else {
                            ToastHelper.make().showMsg(R.string.ui_text_document_details_delete_no_permission);
                        }
                        break;
                }
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true).show();
    }

    private void warningDeleteDocument() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_text_document_details_delete, R.string.ui_base_text_yes, R.string.ui_base_text_no_need, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteDocument();
                return true;
            }
        }, null);
    }

    private void deleteDocument() {
        setLoadingText(R.string.ui_text_document_details_deleting_document);
        displayLoading(true);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                displayLoading(false);
                ToastHelper.make().showMsg(message);
                if (success) {
                    new Dao<>(Archive.class).delete(mQueryId);
                    // 返回成功
                    resultData(mQueryId);
                }
            }
        }).delete(archiveType, mQueryId);
    }

    public Comment getFromPosition(int position) {
        Model model = mAdapter.get(position);
        if (model instanceof Comment) {
            return (Comment) model;
        } else {
            return null;
        }
    }

    public void deleteComment(final int position) {
        final Comment cmt = getFromPosition(position);
        if (null == cmt) {
            return;
        }
        setLoadingText(R.string.ui_text_document_details_deleting_comment);
        displayLoading(true);
        CommentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Comment>() {
            @Override
            public void onResponse(Comment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                if (success) {
                    // 删除成功之后本地评论也删除
                    new Dao<>(Comment.class).delete(cmt);
                    mAdapter.remove(position);
                }
                displayLoading(false);
            }
        }).delete(commentType(), mQueryId, cmt.getId());
    }

    private void fetchingDocument(boolean fromLocal) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != message) {
                    mAdapter.update(archive);
                    resetRightTitleButton(archive);
                } else {
                    new Dao<>(Archive.class).delete(mQueryId);
                    closeWithWarning(R.string.ui_text_document_details_not_exists);
                }
            }
        }).find(archiveType, mQueryId, fromLocal);
    }

    private OnInputCompleteListener onInputCompleteListener = new OnInputCompleteListener() {
        @Override
        public void onInputComplete(String text, int length, int type) {
            tryComment(text);
        }
    };

    private void tryComment(String text) {
        setLoadingText(R.string.ui_text_document_details_commenting);
        displayLoading(true);
        CommentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Comment>() {
            @Override
            public void onResponse(Comment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                displayLoading(false);
                if (success) {
                    if (null != comment && !isEmpty(comment.getId())) {
                        mAdapter.update(comment);
                    }
                    refreshing();
                    refreshingDocument();
                }
            }
        }).add(commentType(), mQueryId, text, "");
    }

    private OnHandleBoundDataListener<Model> onHandlerBoundDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Model onHandlerBoundData(BaseViewHolder holder) {
            // 收藏
            return mAdapter.get(0);
        }
    };

    private class DocumentDetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_HEADER = 0, VT_ADDITIONAL = 2, VT_ATTACHMENT = 3, VT_COMMENT = 4;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveDetailsFragment fragment = ArchiveDetailsFragment.this;
            switch (viewType) {
                case VT_HEADER:
                    return new ArchiveDetailsHeaderViewHolder(itemView, fragment);
                case VT_ATTACHMENT:
                    return new AttachmentViewHolder(itemView, fragment);
                case VT_ADDITIONAL:
                    ArchiveAdditionalViewHolder aavh = new ArchiveAdditionalViewHolder(itemView, fragment);
                    aavh.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
                    return aavh;
                default:
                    return new ArchiveCommentViewHolder(itemView, fragment);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.tool_view_document_details_header;
                case VT_ADDITIONAL:
                    return R.layout.tool_view_document_additional;
                case VT_ATTACHMENT:
                    return R.layout.holder_view_attachment;
                default:
                    return R.layout.holder_view_document_comment;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Archive) {
                return VT_HEADER;
            } else if (model instanceof Additional) {
                return VT_ADDITIONAL;
            } else if (model instanceof Comment) {
                return VT_COMMENT;
            }
            return VT_ATTACHMENT;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveDetailsHeaderViewHolder) {
                ((ArchiveDetailsHeaderViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof AttachmentViewHolder) {
                ((AttachmentViewHolder) holder).setEditable(false);
                ((AttachmentViewHolder) holder).showContent((Attachment) item);
            } else if (holder instanceof ArchiveAdditionalViewHolder) {
                ((ArchiveAdditionalViewHolder) holder).showContent((Archive) mAdapter.get(0));
            } else if (holder instanceof ArchiveCommentViewHolder) {
                ((ArchiveCommentViewHolder) holder).showContent((Comment) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            if (item1 instanceof Comment && item2 instanceof Comment) {
                // 按照创建时间倒序排序
                int compared = ((Comment) item1).getCreateDate().compareTo(((Comment) item2).getCreateDate());
                return compared == 0 ? 0 : -compared;
            }
            return 0;
        }
    }
}
