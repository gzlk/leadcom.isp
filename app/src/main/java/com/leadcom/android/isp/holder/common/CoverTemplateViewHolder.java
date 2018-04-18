package com.leadcom.android.isp.holder.common;

import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.common.CoverTemplate;

/**
 * <b>功能描述：</b>预定义封面的显示ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/18 15:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/04/18 15:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class CoverTemplateViewHolder extends BaseViewHolder {

    @ViewId(R.id.id_holder_view_cover_picker_image)
    private ImageDisplayer imageView;
    @ViewId(R.id.id_holder_view_cover_picker_picker)
    private View pickerView;

    private int padding, height;

    public CoverTemplateViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        padding = getDimension(R.dimen.ui_static_dp_5) * 2;
        height = getDimension(R.dimen.ui_static_dp_150);
        imageView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        });
    }

    public void showContent(CoverTemplate cover) {
        imageView.displayImage(cover.getUrl(), fragment().getScreenWidth() - padding, height, false, false);
        pickerView.setVisibility(cover.isSelected() ? View.VISIBLE : View.GONE);
        itemView.setBackgroundResource(cover.isSelected() ? R.color.textColorHintLight : R.color.windowBackground);
    }
}
