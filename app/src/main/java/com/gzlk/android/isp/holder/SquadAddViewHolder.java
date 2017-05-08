package com.gzlk.android.isp.holder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.organization.StructureFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>组织架构里添加小组<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 23:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 23:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SquadAddViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_squad_add_editor_container)
    private LinearLayout editorContainer;
    @ViewId(R.id.ui_holder_view_squad_add_editor_name)
    private ClearEditText editorName;
    @ViewId(R.id.ui_holder_view_squad_add_editor_confirm)
    private CustomTextView editorConfirm;
    @ViewId(R.id.ui_holder_view_squad_add_container)
    private CorneredView addContainer;

    public SquadAddViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    @Click({R.id.ui_holder_view_squad_add_container, R.id.ui_holder_view_squad_add_editor_confirm})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_squad_add_container:
                // 显示编辑框
                //showEditor(true);
                //openSquadAddDialog();
                ((StructureFragment) fragment()).showSquadAddPopup(true);
                break;
            case R.id.ui_holder_view_squad_add_editor_confirm:
                String value = editorName.getValue();
                if (StringHelper.isEmpty(value)) {
                    ToastHelper.make().showMsg("输入不符合要求");
                    return;
                }
                ((StructureFragment) fragment()).addSquad(value);
                editorName.setValue("");
                showEditor(false);
                break;
        }
    }

    private void showEditor(final boolean shown) {
        editorContainer.animate().setDuration(fragment().duration())
                .translationY(shown ? 0 : -editorContainer.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!shown) {
                    editorContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (shown) {
                    editorContainer.setVisibility(View.VISIBLE);
                }
            }
        }).start();
        addContainer.animate().setDuration(fragment().duration())
                .translationY(addContainer.getHeight() * (shown ? 0 : -1)).withEndAction(new Runnable() {
            @Override
            public void run() {
                addContainer.setTranslationY(0);
            }
        }).start();
    }
}
