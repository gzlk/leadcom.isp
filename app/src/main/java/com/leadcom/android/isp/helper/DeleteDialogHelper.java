package com.leadcom.android.isp.helper;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;

/**
 * <b>功能描述：</b>删除对话框helper<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/23 22:05 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DeleteDialogHelper {

    public static DeleteDialogHelper helper() {
        return new DeleteDialogHelper();
    }

    private int layout = R.layout.popup_dialog_common_delete;
    private BaseFragment fragment;
    private DialogHelper.OnDialogConfirmListener confirmListener;
    private DialogHelper dialogHelper;
    private String titleString, confirmString;

    public DeleteDialogHelper init(BaseFragment fragment) {
        this.fragment = fragment;
        // 默认是删除
        confirmString = StringHelper.getString(R.string.ui_base_text_delete);
        return this;
    }

    public DeleteDialogHelper setLayout(int resLayout) {
        this.layout = resLayout;
        return this;
    }

    public DeleteDialogHelper setOnDialogConfirmListener(DialogHelper.OnDialogConfirmListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public DeleteDialogHelper setTitleText(int titleText) {
        titleString = StringHelper.getString(titleText);
        return this;
    }

    public DeleteDialogHelper setTitleText(String titleText) {
        titleString = titleText;
        return this;
    }

    public DeleteDialogHelper setConfirmText(int text) {
        confirmString = StringHelper.getString(text);
        return this;
    }

    public DeleteDialogHelper setConfirmText(String text) {
        confirmString = text;
        return this;
    }

    private View dialogView;
    private TextView textView;

    public void show() {
        if (null == dialogHelper) {
            dialogHelper = DialogHelper.init(fragment.Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
                @Override
                public View onInitializeView() {
                    if (null == dialogView) {
                        dialogView = View.inflate(fragment.Activity(), layout, null);
                        textView = dialogView.findViewById(R.id.ui_custom_dialog_text);
                    }
                    return dialogView;
                }

                @Override
                public void onBindData(View dialogView, DialogHelper helper) {
                    textView.setText(titleString);
                }
            }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
                @Override
                public int[] clickEventHandleIds() {
                    return new int[]{R.id.ui_custom_dialog_background};
                }

                @Override
                public boolean onClick(View view) {
                    return true;
                }
            }).setConfirmText(confirmString).addOnDialogConfirmListener(confirmListener).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true);
        }
        dialogHelper.show();
    }
}
