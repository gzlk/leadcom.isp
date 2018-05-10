package com.leadcom.android.isp.helper.popup;

import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;

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
    private AppCompatActivity activity;
    private DialogHelper.OnDialogConfirmListener confirmListener;
    private DialogHelper.OnDialogCancelListener cancelListener;
    private DialogHelper dialogHelper;
    private String titleString, confirmString, cancelString;

    public DeleteDialogHelper init(BaseFragment fragment) {
        activity = fragment.Activity();
        // 默认是删除
        confirmString = StringHelper.getString(R.string.ui_base_text_delete);
        return this;
    }

    public DeleteDialogHelper init(AppCompatActivity activity) {
        this.activity = activity;
        // 默认是删除
        confirmString = StringHelper.getString(R.string.ui_base_text_delete);
        cancelString = StringHelper.getString(R.string.ui_base_text_cancel);
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

    public DeleteDialogHelper setOnDialogCancelListener(DialogHelper.OnDialogCancelListener listener) {
        cancelListener = listener;
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

    public DeleteDialogHelper setCancelText(String text) {
        cancelString = text;
        return this;
    }

    public DeleteDialogHelper setCancelText(int text) {
        cancelString = StringHelper.getString(text);
        return this;
    }

    private View dialogView;
    private TextView textView;

    public void show() {
        if (null == dialogHelper) {
            dialogHelper = DialogHelper.init(activity).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
                @Override
                public View onInitializeView() {
                    if (null == dialogView) {
                        dialogView = View.inflate(activity, layout, null);
                        textView = dialogView.findViewById(R.id.ui_custom_dialog_text);
                    }
                    return dialogView;
                }

                @Override
                public void onBindData(View dialogView, DialogHelper helper) {
                    textView.setText(!StringHelper.isEmpty(titleString) ? Html.fromHtml(titleString) : titleString);
                }
            }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
                @Override
                public int[] clickEventHandleIds() {
                    // 默认添加背景点击事件，点击之后关闭dialog
                    return new int[]{R.id.ui_custom_dialog_background};
                }

                @Override
                public boolean onClick(View view) {
                    // 背景点击事件，返回true表示立即关闭dialog
                    return true;
                }
            }).setConfirmText(confirmString).setCancelText(cancelString)
                    .addOnDialogConfirmListener(confirmListener)
                    .addOnDialogCancelListener(cancelListener)
                    .setPopupType(DialogHelper.SLID_IN_BOTTOM)
                    .setAdjustScreenWidth(true);
        }
        dialogHelper.show();
    }
}
