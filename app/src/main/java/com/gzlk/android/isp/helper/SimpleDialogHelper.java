package com.gzlk.android.isp.helper;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;

import java.lang.ref.WeakReference;

/**
 * <b>功能描述：</b>简单的对话框(只显示文字以及取消、确认按钮)<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 15:53 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 15:53 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleDialogHelper {

    public static SimpleDialogHelper init(AppCompatActivity activity) {
        return new SimpleDialogHelper(activity);
    }

    private WeakReference<AppCompatActivity> activity;

    private SimpleDialogHelper(AppCompatActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public void show(int text) {
        show(text, 0);
    }

    public void show(int text, int confirmButton) {
        show(text, confirmButton, 0);
    }

    public void show(int text, int confirm, int cancel) {
        if (0 == text) {
            throw new IllegalArgumentException("You must set the dialog display text.");
        }
        show(text, confirm, cancel, null, null);
    }

    public void show(int text, int confirm, int cancel, DialogHelper.OnDialogConfirmListener confirmListener, DialogHelper.OnDialogCancelListener cancelListener) {
        String content = StringHelper.getString(text);
        String yes = 0 == confirm ? "" : StringHelper.getString(confirm);
        String no = 0 == cancel ? "" : StringHelper.getString(cancel);
        show(content, yes, no, confirmListener, cancelListener);
    }

    public void show(String text) {
        show(text, "");
    }

    public void show(String text, String confirmText) {
        show(text, confirmText, "");
    }

    public void show(String text, String confirmText, String cancelText) {
        show(text, confirmText, cancelText, null, null);
    }

    public void show(int text, DialogHelper.OnDialogConfirmListener listener) {
        show(text, listener, null);
    }

    public void show(String text, DialogHelper.OnDialogConfirmListener listener) {
        show(text, listener, null);
    }

    public void show(int text, DialogHelper.OnDialogConfirmListener confirmListener, DialogHelper.OnDialogCancelListener cancelListener) {
        show(StringHelper.getString(text), confirmListener, cancelListener);
    }

    public void show(String text, DialogHelper.OnDialogConfirmListener confirmListener, DialogHelper.OnDialogCancelListener cancelListener) {
        show(text, "", "", confirmListener, cancelListener);
    }

    public void show(final String text, final String confirmText, final String cancelText, DialogHelper.OnDialogConfirmListener confirmListener, DialogHelper.OnDialogCancelListener cancelListener) {
        DialogHelper.init(activity.get()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                int layout = StringHelper.isEmpty(cancelText) ? R.layout.popup_dialog_simple_text_single_button : R.layout.popup_dialog_simple_text;
                return View.inflate(activity.get(), layout, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                TextView textView = (TextView) dialogView.findViewById(R.id.ui_custom_dialog_text);
                textView.setText(text);
            }
        }).setConfirmText(confirmText)
                .setCancelText(cancelText)
                .addOnDialogConfirmListener(confirmListener)
                .addOnDialogCancelListener(cancelListener).show();
    }
}
