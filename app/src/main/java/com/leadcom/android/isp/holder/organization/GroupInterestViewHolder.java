package com.leadcom.android.isp.holder.organization;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.archive.Classify;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.model.organization.Squad;

/**
 * <b>功能描述：</b>感兴趣的组织item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/09 21:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/09 21:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupInterestViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_group_interest_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_holder_view_group_interest_select)
    private CustomTextView selector;
    @ViewId(R.id.ui_holder_view_group_interest_select_line)
    private CustomTextView selectorLine;
    @ViewId(R.id.ui_holder_view_group_interest_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_group_interest_button)
    private CorneredButton buttonView;
    @ViewId(R.id.ui_holder_view_group_interest_right_icon)
    private CustomTextView rightIcon;
    @ViewId(R.id.ui_holder_view_group_interest_left_blank)
    private View leftBlank;

    private boolean selectable = false, showButton = true;

    public GroupInterestViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        coverView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                coverView.performClick();
            }
        });
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void setButtonShown(boolean shown) {
        showButton = shown;
    }

    public void showContent(Organization organization) {
        coverView.setVisibility(selectable ? View.GONE : View.VISIBLE);
        buttonView.setVisibility(selectable || organization.isSelectable() ? View.GONE : View.VISIBLE);
        selector.setVisibility(selectable && !organization.isSelectable() ? View.VISIBLE : View.GONE);
        selector.setTextColor(getColor(organization.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        // 勾选颜色
        selectorLine.setVisibility(organization.isSelectable() ? View.VISIBLE : View.GONE);
        selectorLine.setTextColor(getColor(organization.isSelected() ? R.color.colorPrimary : R.color.transparent_00));
        String cover = organization.getLogo();
        if (isEmpty(cover) || cover.length() < 20) {
            cover = "drawable://" + R.drawable.img_default_group_icon;
        }
        coverView.displayImage(cover, getDimension(R.dimen.ui_static_dp_35), false, false);
        String name = organization.getName();
//        if (organization instanceof Concern) {
//            Concern concern = (Concern) organization;
//            if (concern.getType() > 0) {
//                name += format("(%s)", Concern.getTypeString(concern.getType()));
//            }
//        }
        nameView.setText(Html.fromHtml(name));
        buttonView.setText(organization.isConcerned() ? R.string.ui_organization_interesting_concerned : R.string.ui_organization_interesting_concern);
        buttonView.setNormalColor(getColor(organization.isConcerned() ? R.color.color_3eb135 : R.color.colorPrimary));
    }

    public void showContent(Squad squad) {
        coverView.setVisibility(selectable ? View.GONE : View.VISIBLE);
        buttonView.setVisibility(selectable || squad.isSelectable() ? View.GONE : View.VISIBLE);
        selector.setVisibility(selectable && !squad.isSelectable() ? View.VISIBLE : View.GONE);
        selector.setTextColor(getColor(squad.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        // 勾选颜色
        selectorLine.setVisibility(squad.isSelectable() ? View.VISIBLE : View.GONE);
        selectorLine.setTextColor(getColor(squad.isSelected() ? R.color.colorPrimary : R.color.transparent_00));
        String name = squad.getName();
        nameView.setText(Html.fromHtml(name));
    }

    public void showContent(Concern concern, String searchingText) {
        coverView.setVisibility(selectable ? View.GONE : View.VISIBLE);
        buttonView.setVisibility(selectable || concern.isSelectable() ? View.GONE : View.VISIBLE);
        selector.setVisibility(selectable && !concern.isSelectable() ? View.VISIBLE : View.GONE);
        selector.setTextColor(getColor(concern.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        // 勾选颜色
        selectorLine.setVisibility(concern.isSelectable() ? View.VISIBLE : View.GONE);
        selectorLine.setTextColor(getColor(concern.isSelected() ? R.color.colorPrimary : R.color.transparent_00));
        String cover = concern.getLogo();
        if (isEmpty(cover) || cover.length() < 20) {
            cover = "drawable://" + R.drawable.img_default_group_icon;
        }
        coverView.displayImage(cover, getDimension(R.dimen.ui_static_dp_35), false, false);
        String name = concern.getName();
        if (isEmpty(name)) {
            name = StringHelper.getString(R.string.ui_base_text_no_name_group);
        }
        name = getSearchingText(name, searchingText);
        //name += format((concern.isConcerned() ? "(%s)" : ""), Concern.getTypeString(concern.getType()));
        nameView.setText(Html.fromHtml(name));
        buttonView.setVisibility(showButton ? View.VISIBLE : View.GONE);
        buttonView.setText(concern.isConcerned() ? R.string.ui_organization_interesting_concerned : R.string.ui_organization_interesting_concern);
        buttonView.setNormalColor(getColor(concern.isConcerned() ? R.color.color_3eb135 : R.color.colorPrimary));
    }

    public void showContent(Concern concern) {
        coverView.setVisibility(View.GONE);
        buttonView.setVisibility(View.GONE);
        selector.setVisibility(View.VISIBLE);
        selector.setText(R.string.ui_icon_select_solid);
        selector.setTextColor(getColor(concern.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        leftBlank.setVisibility(View.GONE);
        nameView.setText(concern.getGroupName());
        rightIcon.setVisibility(concern.getDocClassifyList().size() > 0 ? View.VISIBLE : View.GONE);
        rightIcon.animate().rotation(concern.isSelectable() ? 90 : 0).setDuration(fragment().duration()).start();
    }

    public void showContent(Classify classify) {
        coverView.setVisibility(View.GONE);
        buttonView.setVisibility(View.GONE);
        leftBlank.setVisibility(View.VISIBLE);
        selector.setVisibility(View.VISIBLE);
        boolean selected = classify.isSelected();
        selector.setText(selected ? R.string.ui_icon_select_solid : R.string.ui_icon_radio_unselected);
        selector.setTextColor(getColor(selected ? R.color.colorPrimary : R.color.textColorHintLight));
        nameView.setText(classify.getName());
        rightIcon.setVisibility(View.GONE);
    }

    public void showContent(Concern concern, boolean authorized) {
        coverView.setVisibility(View.GONE);
        selector.setVisibility(View.GONE);
        nameView.setText(concern.getGroupName());
        rightIcon.setVisibility(View.GONE);
        buttonView.setNormalColor(getColor(R.color.textColorLight));
        buttonView.setText(concern.isAuthorized() ? R.string.ui_base_text_authorized : R.string.ui_base_text_authorizing);
        buttonView.setTextColor(getColor(concern.isAuthorized() ? R.color.colorPrimary : R.color.textColor));
        buttonView.setBackgroundType(CorneredButton.BORDER);
        buttonView.setNormalColor(getColor(concern.isAuthorized() ? R.color.colorPrimary : R.color.textColor));
    }

    @Click({R.id.ui_holder_view_group_interest_root, R.id.ui_holder_view_group_interest_button})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
            return;
        }
        switch (view.getId()) {
            case R.id.ui_holder_view_group_interest_root:
                if (buttonView.getVisibility() == View.GONE) {
                    if (null != mOnViewHolderClickListener) {
                        mOnViewHolderClickListener.onClick(getAdapterPosition());
                    }
                }
                break;
            default:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
        }
    }
}
