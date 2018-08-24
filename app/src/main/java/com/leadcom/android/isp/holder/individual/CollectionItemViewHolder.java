package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.attachment.AttachmentViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.model.user.Moment;

import java.io.File;
import java.util.Locale;

/**
 * <b>功能描述：</b>收藏<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/04 23:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/04 23:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CollectionItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_collection_header)
    private ImageDisplayer creatorImage;
    @ViewId(R.id.ui_holder_view_collection_name)
    private TextView creatorName;
    @ViewId(R.id.ui_holder_view_collection_time)
    private TextView createTime;

    @ViewId(R.id.ui_tool_view_collection_content_text)
    private TextView textContent;
    @ViewId(R.id.ui_tool_view_collection_content_indicator)
    private TextView textIndicator;

    @ViewId(R.id.ui_tool_view_collection_content_image)
    private ImageDisplayer imageContent;

    @ViewId(R.id.ui_tool_view_collection_content_attachment)
    private LinearLayout attachmentContent;
    @ViewId(R.id.ui_tool_view_collection_content_attachment_icon)
    private CustomTextView attachmentIcon;
    @ViewId(R.id.ui_tool_view_collection_content_attachment_name)
    private TextView attachmentName;
    @ViewId(R.id.ui_tool_view_collection_content_attachment_extension)
    private TextView attachmentExtension;
    @ViewId(R.id.ui_tool_view_collection_content_attachment_size)
    private TextView attachmentSize;

    @ViewId(R.id.ui_tool_view_collection_content_archive)
    private LinearLayout archiveLayout;
    @ViewId(R.id.ui_tool_view_collection_content_archive_cover)
    private ImageDisplayer archiveCover;
    @ViewId(R.id.ui_tool_view_collection_content_archive_text)
    private TextView archiveText;
    @ViewId(R.id.ui_holder_view_collection_label_add)
    private TextView labelAdd;
    @ViewId(R.id.ui_holder_view_collection_labels)
    private FlexboxLayout labelsLayout;

    private int margin;

    public CollectionItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        margin = getDimension(R.dimen.ui_static_dp_5);
        creatorImage.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderElementClickListener) {
                    mOnViewHolderElementClickListener.onClick(creatorImage, getAdapterPosition());
                }
            }
        });
        imageContent.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderElementClickListener) {
                    mOnViewHolderElementClickListener.onClick(imageContent, getAdapterPosition());
                }
            }
        });
    }

    public void showContent(Collection collection) {
        creatorName.setText(collection.getCreatorName());
        createTime.setText(format("%s收藏", fragment().formatTimeAgo(collection.getCreateDate())));
        String header = collection.getCreatorHeadPhoto();
        if (isEmpty(header) || header.length() < 20) {
            header = "drawable://" + R.drawable.img_default_user_header;
        }
        creatorImage.displayImage(header, getDimension(R.dimen.ui_base_user_header_image_size_small), false, false);
        checkViews(collection.getType());
        showCollection(collection);
        labelAdd.setVisibility(collection.getLabel().size() > 0 ? View.GONE : View.VISIBLE);
        labelsLayout.removeAllViews();
        for (String string : collection.getLabel()) {
            TextView textView = (TextView) View.inflate(labelsLayout.getContext(), R.layout.holder_view_archive_label, null);
            textView.setText(string);
            labelsLayout.addView(textView);
            textView.setOnClickListener(labelClick);
            int lines = labelsLayout.getFlexLines().size();
            FlexboxLayout.LayoutParams params = (FlexboxLayout.LayoutParams) textView.getLayoutParams();
            params.rightMargin = margin;
            params.topMargin = lines > 0 ? margin : 0;
            textView.setLayoutParams(params);
        }
    }

    private View.OnClickListener labelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(App.clickAnimation());
            TextView textView = (TextView) v;
            int index = labelsLayout.indexOfChild(textView);
            if (null != labelClickListener) {
                // 可以修改标签
                labelClickListener.onClick(getAdapterPosition(), index, textView.getText().toString());
            }
        }
    };

    private void checkViews(int type) {
        textContent.setVisibility(type == Collection.Type.TEXT ? View.VISIBLE : View.GONE);
        imageContent.setVisibility(type == Collection.Type.IMAGE ? View.VISIBLE : View.GONE);
        attachmentContent.setVisibility(type == Collection.Type.ATTACHMENT || type == Collection.Type.ARCHIVE ? View.VISIBLE : View.GONE);
        archiveLayout.setVisibility((type == Collection.Type.USER_MOMENT ||
                type == Collection.Type.GROUP_ARCHIVE ||
                type == Collection.Type.USER_ARCHIVE) ||
                type == Collection.Type.VIDEO
                ? View.VISIBLE : View.GONE);
    }

    private void showCollection(Collection col) {
        switch (col.getType()) {
            case Collection.Type.TEXT:
                showTextContent(col);
                break;
            case Collection.Type.IMAGE:
                int width = showLargeImage ? fragment().getScreenWidth() : getDimension(R.dimen.ui_static_dp_120);
                int height = showLargeImage ? fragment().getScreenHeight() : getDimension(R.dimen.ui_static_dp_80);
                if (!showLargeImage) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageContent.getLayoutParams();
                    params.width = width;
                    params.height = height;
                }
                imageContent.displayImage(col.getContent(), width, height, false, false);
                break;
            case Collection.Type.ARCHIVE:
            case Collection.Type.ATTACHMENT:
                Attachment attachment = new Attachment();
                attachment.setName(col.getContent().substring(col.getContent().lastIndexOf('/') + 1));
                attachment.setUrl(col.getContent());
                attachment.resetInformation();
                attachmentExtension.setText(attachment.getExt().toUpperCase(Locale.getDefault()));
                attachmentIcon.setText(AttachmentViewHolder.getFileExtension(attachment.getExt()));
                attachmentName.setText(attachment.getName());
                String local = App.app().getLocalFilePath(attachment.getUrl(), App.OTHER_DIR);
                File file = new File(local);
                long size = file.length();
                attachmentSize.setText(Utils.formatSize(size));
                break;
            case Collection.Type.USER_ARCHIVE:
            case Collection.Type.GROUP_ARCHIVE:
            case Collection.Type.USER_MOMENT:
            case Collection.Type.VIDEO:
                if (col.getType() == Collection.Type.USER_MOMENT) {
                    String image = "drawable://" + R.drawable.img_default_app_icon;
                    if (col.getUserMmt().getImage().size() > 0) {
                        image = col.getUserMmt().getImage().get(0);
                    }
                    archiveCover.displayImage(image, getDimension(R.dimen.ui_static_dp_50), false, false);
                    archiveText.setText(StringHelper.getString(R.string.ui_text_collection_archive_text, "动态", ""));
                } else if (col.getType() == Collection.Type.VIDEO) {
                    archiveCover.displayImage("drawable://" + R.drawable.img_image_video, getDimension(R.dimen.ui_static_dp_50), false, false);
                    archiveText.setText(StringHelper.getString(R.string.ui_text_collection_archive_text, "视频", ""));
                } else {
                    archiveCover.displayImage("drawable://" + R.drawable.img_default_archive, getDimension(R.dimen.ui_static_dp_50), false, false);
                    archiveText.setText(StringHelper.getString(R.string.ui_text_collection_archive_text,
                            (col.getType() == Collection.Type.GROUP_ARCHIVE ? "组织档案" : "个人档案"), col.getSourceTitle()));
                }
                break;
        }
    }

    private static final int MAX_LINE = 3;

    private void showTextContent(final Collection collection) {
        String content = collection.getContent();
        if (isEmpty(content)) {
            content = "[空白内容]";
        }
        if (Moment.State.NONE == collection.getCollapseStatus()) {
            textContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    textContent.getViewTreeObserver().removeOnPreDrawListener(this);
                    // 内容行数超过预设，则状态设置为折叠状态
                    if (textContent.getLineCount() > MAX_LINE) {
                        textContent.setMaxLines(MAX_LINE);
                        textIndicator.setVisibility(View.VISIBLE);
                        textIndicator.setText(R.string.expandable_view_expand_handle_text);
                        collection.setCollapseStatus(Moment.State.COLLAPSED);
                    } else {
                        // 行数未超过预设值，不需要折叠，也不需要显示折叠相关的控件
                        textIndicator.setVisibility(View.GONE);
                        collection.setCollapseStatus(Moment.State.NOT_OVERFLOW);
                    }
                    return true;
                }
            });
        } else {
            // 状态已经设置过了
            switch (collection.getCollapseStatus()) {
                case Moment.State.COLLAPSED:
                    textContent.setMaxLines(MAX_LINE);
                    textIndicator.setVisibility(View.VISIBLE);
                    textIndicator.setText(R.string.expandable_view_expand_handle_text);
                    break;
                case Moment.State.EXPANDED:
                    textContent.setMaxLines(Integer.MAX_VALUE);
                    textIndicator.setVisibility(View.VISIBLE);
                    textIndicator.setText(R.string.expandable_view_collapse_handle_text);
                    break;
                case Moment.State.NOT_OVERFLOW:
                    textIndicator.setVisibility(View.GONE);
                    break;
            }
        }
        textContent.setText(EmojiUtility.getEmojiString(textContent.getContext(), content, true));
    }

    private boolean showLargeImage = false;

    public void setShowLargeImage(boolean largeImage) {
        showLargeImage = largeImage;
        createTime.setVisibility(showLargeImage ? View.GONE : View.VISIBLE);
    }

    @Click({R.id.ui_tool_view_collection_content_archive,
            R.id.ui_holder_view_collection_label_add,
            R.id.ui_holder_view_collection_delete,
            R.id.ui_tool_view_collection_content_indicator,
            R.id.ui_tool_view_collection_content_attachment})
    private void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }

    private OnLabelClickListener labelClickListener;

    public void setOnLabelClickListener(OnLabelClickListener l) {
        labelClickListener = l;
    }

    public interface OnLabelClickListener {
        void onClick(int index, int labelIndex, String oldValue);
    }
}
