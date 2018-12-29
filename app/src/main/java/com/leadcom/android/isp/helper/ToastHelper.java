package com.leadcom.android.isp.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * 提供静态显示Toast消息的Helper
 * Created by Hsiang Leekwok on 2015/07/17.
 */
public class ToastHelper {

    private static final String TAG = "ToastHelper";
    @SuppressLint("StaticFieldLeak")
    private static ToastHelper helper;

    private Handler handler;
    private final Object synObj = new Object();
    private Toast toast = null;
    private View toastView;
    private TextView toastText;
    private CustomTextView toastIcon;

    public static ToastHelper helper() {
        if (null == helper) {
            helper = new ToastHelper();
        }
        return helper;
    }

    private ToastHelper() {
        handler = new Handler(Looper.getMainLooper());
    }

    public void showMsg(int text) {
        showMsg(StringHelper.getString(text));
    }

    public void showMsg(int text, int icon) {
        showMsg(StringHelper.getString(text), (0 == icon ? null : StringHelper.getString(icon)));
    }

    public void showMsg(String msg) {
        showMsg(msg, null);
    }

    public void showMsg(String msg, int icon) {
        showMsg(msg, (0 == icon ? null : StringHelper.getString(icon)));
    }

    public void showMsg(final String msg, final String icon) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            toast(msg, icon);
                        }
                    }
                });
            }
        }).start();
    }

    private void toast(String msg, String icon) {
        Context context = App.app();
        if (null == toastView) {
            toastView = View.inflate(context, R.layout.base_custom_toast, null);
            toastText = toastView.findViewById(R.id.ui_base_custom_toast_text);
            toastIcon = toastView.findViewById(R.id.ui_base_custom_toast_icon);
        }
        toastText.setText(Html.fromHtml(msg));
        toastIcon.setText(icon);
        toastIcon.setVisibility(null == icon ? View.GONE : View.VISIBLE);
        if (null != toast) {
            toast.cancel();
            toast = null;
        }

        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
        LogHelper.log(TAG, msg);
    }
}
