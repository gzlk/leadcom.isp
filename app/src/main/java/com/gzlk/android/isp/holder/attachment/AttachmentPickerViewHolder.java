package com.gzlk.android.isp.holder.attachment;

import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/29 02:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/29 02:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AttachmentPickerViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_attachment_picker_image)
    private LinearLayout chooseImage;
    @ViewId(R.id.ui_holder_view_attachment_picker_image_icon)
    private CustomTextView imageIcon;
    @ViewId(R.id.ui_holder_view_attachment_picker_other)
    private LinearLayout chooseOther;
    @ViewId(R.id.ui_holder_view_attachment_picker_other_icon)
    private CustomTextView otherIcon;

    public AttachmentPickerViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    private boolean pickImage = true;

    @Click({R.id.ui_holder_view_attachment_picker_image, R.id.ui_holder_view_attachment_picker_other})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_attachment_picker_image:
                pickImage = true;
                reset();
                break;
            case R.id.ui_holder_view_attachment_picker_other:
                pickImage = false;
                reset();
                break;
        }
    }

    private void reset() {
        imageIcon.setTextColor(getColor(pickImage ? R.color.colorPrimary : R.color.textColorHintLight));
        otherIcon.setTextColor(getColor(pickImage ? R.color.textColorHintLight : R.color.colorPrimary));
    }
}
