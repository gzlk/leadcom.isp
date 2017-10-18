package com.gzlk.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.RecommendArchive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;


/**
 * <b>功能描述：</b>首页档案推荐item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/18 09:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/18 09:57 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveHomeRecommendedViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_home_recommend_group)
    private TextView groupView;
    @ViewId(R.id.ui_holder_view_archive_home_recommend_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_holder_view_archive_home_recommend_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_archive_home_recommend_content)
    private TextView contentView;
    // 附加信息
    @ViewId(R.id.ui_tool_view_archive_additional_comment_number)
    private TextView commentsView;
    @ViewId(R.id.ui_tool_view_archive_additional_like_number)
    private TextView likesView;
    @ViewId(R.id.ui_tool_view_archive_additional_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_icon)
    private CustomTextView collectIcon;

    private int width, height;

    public ArchiveHomeRecommendedViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        width = fragment.getScreenWidth() - getDimension(R.dimen.ui_base_dimen_margin_padding) * 2;
        height = width / 2;
        resetImageHeight();
        coverView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                // 图片点击等同于整个view点击
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        });
    }

    private void resetImageHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) coverView.getLayoutParams();
        params.height = height;
        coverView.setLayoutParams(params);
    }

    public void showContent(RecommendArchive archive) {
        groupView.setText(StringHelper.getString(R.string.ui_text_home_archive_recommend_group_name, archive.getGroupName()));
        Archive doc = null == archive.getUserDoc() ? archive.getGroDoc() : archive.getUserDoc();
        coverView.displayImage(doc.getCover(), width, height, false, false);
        titleView.setText(doc.getTitle());
        // 去掉所有html标签
        contentView.setVisibility(isEmpty(doc.getContent()) ? View.GONE : View.VISIBLE);
        contentView.setText(isEmpty(doc.getContent()) ? "" : Utils.clearHtml(doc.getContent()));
        commentsView.setText(String.valueOf(doc.getAddition().getCmtNum()));
        likesView.setText(String.valueOf(doc.getAddition().getLikeNum()));
        collectIcon.setTextColor(getColor(doc.getCollection() == Archive.CollectionType.COLLECTED ? R.color.colorCaution : R.color.textColorHint));
    }

    @Click({R.id.ui_holder_view_archive_home_recommend_layout
            //R.id.ui_tool_view_archive_additional_comment_layout,
            //R.id.ui_tool_view_archive_additional_like_layout,
            //R.id.ui_tool_view_archive_additional_collection_layout
    })
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
//        switch (view.getId()) {
//            case R.id.ui_holder_view_archive_home_recommend_layout:
//                break;
//            default:
//                if (null != mOnViewHolderElementClickListener) {
//                    mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
//                }
//                break;
//        }
    }
}
