package com.gzlk.android.isp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>组织首页中已关注组织列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 11:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 11:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationConcerned extends FrameLayout {

    public OrganizationConcerned(Context context) {
        this(context, null);
    }

    public OrganizationConcerned(Context context, int layout) {
        this(context);
        isBig = false;
        init(layout);
    }

    public OrganizationConcerned(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrganizationConcerned(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean isBig = true;

    public void setContentLayout(int layoutRes) {
        init(layoutRes);
    }

    private void init(int layout) {
        View view = LayoutInflater.from(getContext()).inflate(layout, this);
        ViewUtility.bind(this, view);
        imageDisplayer.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                container.performClick();
            }
        });
    }

    @ViewId(R.id.ui_tool_organization_concerned_pager_container)
    private LinearLayout container;
    @ViewId(R.id.ui_tool_organization_concerned_pager_logo)
    private ImageDisplayer imageDisplayer;
    @ViewId(R.id.ui_tool_organization_concerned_pager_name)
    private TextView textView;

    public void showOrganization(Organization organization) {
        imageDisplayer.displayImage(organization.getLogo(), getResources().getDimensionPixelSize(isBig ? R.dimen.ui_static_dp_75 : R.dimen.ui_static_dp_60), false, false);
        textView.setText(organization.getName());
        container.setTag(R.id.hlklib_ids_custom_view_click_tag, organization.getId());
    }

    @Click({R.id.ui_tool_organization_concerned_pager_container})
    private void click(View view) {
        if (null != onClickListener) {
            String id = (String) container.getTag(R.id.hlklib_ids_custom_view_click_tag);
            onClickListener.onClick(OrganizationConcerned.this, id);
        }
    }

    private OnContainerClickListener onClickListener;

    public void setOnContainerClickListener(OnContainerClickListener l) {
        onClickListener = l;
    }

    public interface OnContainerClickListener {
        void onClick(OrganizationConcerned concerned, String id);
    }
}
