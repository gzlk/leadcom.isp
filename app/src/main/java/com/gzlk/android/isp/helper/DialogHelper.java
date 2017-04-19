package com.gzlk.android.isp.helper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.gzlk.android.isp.R;
import com.hlk.hlklib.lib.view.CorneredButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能</b>：自定义对话框<br />
 * <b>作者</b>：Hsiang Leekwok <br />
 * <b>时间</b>：2016/03/26 22:49 <br />
 * <b>邮箱</b>：xiang.l.g@gmail.com <br />
 */
public class DialogHelper {

    private AlertDialog dialog;
    private boolean cancelable = true;
    private boolean canceledWithOutsideTouch = true;
    private WeakReference<AppCompatActivity> activity;

    /**
     * 弹出效果
     */
    public static final int TYPE_FADE = 0;
    /**
     * 滑入效果
     */
    public static final int TYPE_SLID = 1;

    /**
     * 弹出对话框的动画方式
     */
    @IntDef({TYPE_FADE, TYPE_SLID})
    public @interface PopupType {
    }

    public static DialogHelper init(AppCompatActivity activity) {
        return new DialogHelper(activity);
    }

    private DialogHelper(AppCompatActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    /**
     * 设置是否可以用back键返回
     */
    public DialogHelper setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    /**
     * 设置对话框空白部分是否可以点击退出
     */
    public DialogHelper setCanceledOnTouchOutside(boolean touchable) {
        canceledWithOutsideTouch = touchable;
        return this;
    }

    private void initializeDialog() {
        dialog = new AlertDialog.Builder(activity.get()).create();
        // 弹出效果和滑入效果
        dialog.getWindow().setWindowAnimations(popupType == TYPE_FADE ? R.style.DialogAnimationFade : R.style.DialogAnimationSlid);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(canceledWithOutsideTouch);
        dialog.setOnDismissListener(dismissListener);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent_00);
    }

    private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (null != mOnDialogDismissListener) {
                mOnDialogDismissListener.onDismiss();
            }
        }
    };

    private String _cancelText, _confirmText;

    public DialogHelper setCancelText(int res) {
        return setCancelText(StringHelper.getString(res));
    }

    private int popupType = TYPE_FADE;

    /**
     * 设置弹出动画效果
     */
    public DialogHelper setPopupType(@PopupType int popupType) {
        this.popupType = popupType;
        return this;
    }

    public DialogHelper setCancelText(String text) {
        _cancelText = text;
        return this;
    }

    public DialogHelper setConfirmText(int res) {
        return setConfirmText(StringHelper.getString(res));
    }

    public DialogHelper setConfirmText(String text) {
        _confirmText = text;
        return this;
    }

    public void show() {
        initializeDialog();
        // 初始化rootView
        View root;
        if (null != mOnDialogInitializeListener) {
            root = mOnDialogInitializeListener.onInitializeView();
        } else {
            throw new IllegalArgumentException("You must initialize the root view first.");
        }
        if (null == root) {
            throw new IllegalArgumentException("Cannot initialize dialog with null content view.");
        }
        initializeEvents(root);
        // 绑定数据
        if (null != mOnDialogInitializeListener) {
            mOnDialogInitializeListener.onBindData(root, this);
        }
        dialog.setContentView(root);
    }

    /**
     * 初始化指定id的点击事件
     */
    private void initializeEvents(View root) {
        initializeStaticEvents(root);
        if (null != mOnEventHandlerListener) {
            int[] ids = mOnEventHandlerListener.clickEventHandleIds();
            eventHandleIds.clear();
            for (int id : ids) {
                View view = root.findViewById(id);
                if (null != view) {
                    eventHandleIds.add(id);
                    view.setOnClickListener(_clickListener);
                } else {
                    throw new IllegalArgumentException("No view has given id to:" + id + ".");
                }
            }
        }
        ViewGroup parent = (ViewGroup) root.getParent();
        if (null != parent) {
            parent.removeAllViews();
        }
    }

    /**
     * 初始化自有的点击事件（确定、取消）
     */
    private void initializeStaticEvents(View root) {
        View _buttonCancel = root.findViewById(R.id.ui_dialog_button_cancel);
        if (null != _buttonCancel) {
            if (!StringHelper.isEmpty(_cancelText) && _buttonCancel instanceof CorneredButton) {
                ((CorneredButton) _buttonCancel).setText(_cancelText);
            }
            _buttonCancel.setOnClickListener(_clickListener);
        }
        View _buttonConfirm = root.findViewById(R.id.ui_dialog_button_confirm);
        if (null != _buttonConfirm) {
            if (!StringHelper.isEmpty(_confirmText) && _buttonConfirm instanceof CorneredButton) {
                ((CorneredButton) _buttonConfirm).setText(_confirmText);
            }
            _buttonConfirm.setOnClickListener(_clickListener);
        }
    }

    public void dismiss() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    private List<Integer> eventHandleIds = new ArrayList<>();

    private View.OnClickListener _clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (null != mOnEventHandlerListener) {
                if (eventHandleIds.contains(id)) {
                    if (mOnEventHandlerListener.onClick(v)) {
                        dismiss();
                    }
                }
            }
            if (id == R.id.ui_dialog_button_confirm) {
                if (null != confirmListener) {
                    if (confirmListener.onConfirm()) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            } else {
                // 取消按钮时
                if (null != cancelListener) {
                    cancelListener.onCancel();
                }
                dismiss();
            }
        }
    };

    private OnEventHandlerListener mOnEventHandlerListener;

    /**
     * 设置点击事件回调
     */
    public DialogHelper addOnEventHandlerListener(OnEventHandlerListener l) {
        mOnEventHandlerListener = l;
        return this;
    }

    public interface OnEventHandlerListener {

        /**
         * 需要处理点击事件的id列表
         */
        int[] clickEventHandleIds();

        /**
         * View的点击回调
         *
         * @param view 点击的View
         * @return true=dismiss
         */
        boolean onClick(View view);
    }

    // dismiss interface
    private OnDialogDismissListener mOnDialogDismissListener;

    /**
     * 添加对对话框dismiss事件处理
     */
    public DialogHelper addOnDialogDismissListener(OnDialogDismissListener l) {
        mOnDialogDismissListener = l;
        return this;
    }

    /**
     * 对话框dismiss时的处理接口
     */
    public interface OnDialogDismissListener {

        /**
         * 对话框关闭回调
         */
        void onDismiss();
    }

    /**
     * 对话框确认
     */
    public interface OnDialogConfirmListener {
        /**
         * 确定<br />
         *
         * @return true: 可以关闭dialog，false：不关闭dialog
         */
        boolean onConfirm();
    }

    private OnDialogConfirmListener confirmListener;

    /**
     * 添加confirm确认点击事件
     */
    public DialogHelper addOnDialogConfirmListener(OnDialogConfirmListener l) {
        confirmListener = l;
        return this;
    }

    /**
     * 对话框取消按钮
     */
    public interface OnDialogCancelListener {
        void onCancel();
    }

    private OnDialogCancelListener cancelListener;

    /**
     * 添加对话框取消点击事件回调
     */
    public DialogHelper addOnDialogCancelListener(OnDialogCancelListener l) {
        cancelListener = l;
        return this;
    }

    private OnDialogInitializeListener mOnDialogInitializeListener;

    /**
     * 设置回调
     */
    public DialogHelper addOnDialogInitializeListener(OnDialogInitializeListener l) {
        mOnDialogInitializeListener = l;
        return this;
    }

    /**
     * 对话框初始化时的回调
     */
    public interface OnDialogInitializeListener {

        /**
         * 初始化dialog的contentView
         */
        View onInitializeView();

        /**
         * 绑定数据
         */
        void onBindData(View dialogView, DialogHelper helper);
    }
}
