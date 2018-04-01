package com.leadcom.android.isp.helper.popup;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.view.ClearEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/25 10:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class EditableDialogHelper {

    public static EditableDialogHelper helper() {
        return new EditableDialogHelper();
    }

    private int layout = R.layout.popup_dialog_simple_input_edit;
    private DialogHelper.OnDialogConfirmListener confirmListener;
    private BaseFragment fragment;
    private String titleString, inputString, inputHint;
    private DialogHelper dialogHelper;

    public EditableDialogHelper init(BaseFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    public EditableDialogHelper setLayout(int resLayout) {
        this.layout = resLayout;
        return this;
    }

    public EditableDialogHelper setOnDialogConfirmListener(DialogHelper.OnDialogConfirmListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public EditableDialogHelper setTitleText(int titleText) {
        titleString = StringHelper.getString(titleText);
        return this;
    }

    public EditableDialogHelper setTitleText(String titleText) {
        titleString = titleText;
        return this;
    }

    public EditableDialogHelper setInputHint(int resString) {
        inputHint = StringHelper.getString(resString);
        return this;
    }

    public EditableDialogHelper setInputHint(String value) {
        inputHint = value;
        return this;
    }

    public EditableDialogHelper setInputValue(String value) {
        inputString = value;
        return this;
    }

    private View dialogView;
    private TextView textView;
    private ClearEditText input;

    public void show() {
        if (null == dialogHelper) {
            dialogHelper = DialogHelper.init(fragment.Activity()).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true);
        }
        dialogHelper.addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = View.inflate(fragment.Activity(), layout, null);
                    textView = dialogView.findViewById(R.id.ui_custom_dialog_text);
                    input = dialogView.findViewById(R.id.ui_custom_dialog_input);
                }
                return dialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                textView.setText(titleString);
                input.setTextHint(inputHint);
                input.setValue(inputString);
                input.focusEnd();
            }
        }).addOnDialogConfirmListener(confirmListener);
        dialogHelper.show();
    }

    /**
     * 获取输入的值
     */
    public String getInputValue() {
        if (null != input) {
            return input.getValue();
        }
        return null;
    }
}
