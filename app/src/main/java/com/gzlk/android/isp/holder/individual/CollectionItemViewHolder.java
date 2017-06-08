package com.gzlk.android.isp.holder.individual;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.CollectionDetailsFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.user.Collection;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

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
    private ExpandableTextView textContent;
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

    public CollectionItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Collection collection) {
        creatorName.setText(collection.getCreatorName());
        createTime.setText(Utils.formatTimeAgo(StringHelper.getString(R.string.ui_base_text_date_time_format), collection.getCreateDate()));
        checkViews(collection.getType());
        showCollection(collection.getType(), collection.getContent());
    }

    private void checkViews(int type) {
        textContent.setVisibility(type == Collection.Type.TEXT ? View.VISIBLE : View.GONE);
        imageContent.setVisibility(type == Collection.Type.IMAGE ? View.VISIBLE : View.GONE);
        attachmentContent.setVisibility(type == Collection.Type.ATTACHMENT ? View.VISIBLE : View.GONE);
    }

    @SuppressWarnings("ConstantConditions")
    private void showCollection(int type, String content) {
        switch (type) {
            case Collection.Type.TEXT:
                textContent.setText(EmojiUtility.getEmojiString(textContent.getContext(), content, true));
                textContent.makeExpandable();
                break;
            case Collection.Type.IMAGE:
                int width = showLargeImage ? fragment().getScreenWidth() : getDimension(R.dimen.ui_static_dp_120);
                int height = showLargeImage ? fragment().getScreenHeight() : getDimension(R.dimen.ui_static_dp_80);
                if (!showLargeImage) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageContent.getLayoutParams();
                    params.width = width;
                    params.height = height;
                }
                imageContent.displayImage(content, width, height, false, false);
                break;
            case Collection.Type.ATTACHMENT:
                String name = content.substring(content.lastIndexOf('/') + 1);
                String extension = name.substring(name.lastIndexOf('.') + 1);
                attachmentExtension.setText(extension.toUpperCase(Locale.getDefault()));
                attachmentIcon.setText(AttachmentViewHolder.getFileExtension(extension));
                name = name.replace("." + extension, "");
                attachmentName.setText(name);
                String local = App.app().getLocalFilePath(content, App.OTHER_DIR);
                File file = new File(local);
                long size = file.length();
                attachmentSize.setText(Utils.formatSize(size));
                break;
        }
    }

    private boolean showLargeImage = false;

    public void setShowLargeImage(boolean largeImage) {
        showLargeImage = largeImage;
        createTime.setVisibility(showLargeImage ? View.GONE : View.VISIBLE);
    }

    @Click({R.id.ui_holder_view_collection_content_cover})
    private void click(View view) {
        if (null != dataHandlerBoundDataListener) {
            Object object = dataHandlerBoundDataListener.onHandlerBoundData(this);
            if (null != object && object instanceof Collection) {
                openActivity(CollectionDetailsFragment.class.getName(), ((Collection) object).getId(), true, false);
            }
        }
    }
}
