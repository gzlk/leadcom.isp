package com.gzlk.android.isp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.organization.Group;
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

    public OrganizationConcerned(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrganizationConcerned(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tool_view_organziation_concerned_pager, this);
        ViewUtility.bind(this, view);
        imageDisplayer.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(String url) {
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

    public void showOrganization(Group group) {
        imageDisplayer.displayImage(group.getLogo(), getResources().getDimensionPixelSize(R.dimen.ui_static_dp_80), false, false);
        textView.setText(group.getName());
    }

    private int position;

    public void showContent(int position) {
        this.position = position;
        textView.setText(StringHelper.format("测试%d", position));
    }

    public int getPosition() {
        return position;
    }

    @Click({R.id.ui_tool_organization_concerned_pager_container})
    private void click(View view) {
        if (null != onClickListener) {
            onClickListener.onClick(position);
        }
    }

    private OnContainerClickListener onClickListener;

    public void setOnContainerClickListener(OnContainerClickListener l) {
        onClickListener = l;
    }

    public interface OnContainerClickListener {
        void onClick(int position);
    }
}
