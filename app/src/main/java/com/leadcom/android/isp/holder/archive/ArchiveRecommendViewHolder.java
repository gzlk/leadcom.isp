package com.leadcom.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.RecommendArchive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>推荐档案ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/17 15:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/17 15:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveRecommendViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_recommend_image)
    private ImageDisplayer imageView;
    @ViewId(R.id.ui_holder_view_archive_recommend_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_archive_recommend_group)
    private TextView groupView;
    @ViewId(R.id.ui_holder_view_archive_recommend_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_archive_recommend_icon)
    private CustomTextView iconView;

    public ArchiveRecommendViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(RecommendArchive archive) {
        Archive doc = null != archive.getUserDoc() ? archive.getUserDoc() : archive.getGroDoc();
        String cover = null == doc ? "" : doc.getCover();
        if (isEmpty(cover)) {
            if (null != doc && doc.getImage().size() > 0) {
                cover = doc.getImage().get(0).getUrl();
            } else {
                cover = "drawable://" + R.drawable.img_default_app_icon;
            }
        }
        imageView.displayImage(cover, getDimension(R.dimen.ui_static_dp_100), getDimension(R.dimen.ui_static_dp_80), false, false);
        titleView.setText(null == doc ? "" : doc.getTitle());
        String groupName = archive.getGroupName();
        if (isEmpty(groupName)) {
            assert doc != null;
            if (null == doc.getGroEntity()) {
                groupName = doc.getUserName();
            } else {
                groupName = doc.getGroEntity().getName();
            }
        }
        groupView.setText(StringHelper.getString(R.string.ui_archive_recommend_belong_group, Html.fromHtml(groupName)));
        timeView.setText(fragment().formatDate(archive.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_format)));
        iconView.setTextColor(getColor(archive.isRecommended() ? R.color.colorCaution : R.color.textColorHint));
    }

    @Click({R.id.ui_holder_view_archive_recommend_layout, R.id.ui_holder_view_archive_recommend_icon})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_archive_recommend_layout:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
            case R.id.ui_holder_view_archive_recommend_icon:
                if (null != mOnHandlerBoundDataListener) {
                    mOnHandlerBoundDataListener.onHandlerBoundData(ArchiveRecommendViewHolder.this);
                }
                break;
        }
    }
}
