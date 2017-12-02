package com.leadcom.android.isp.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.lang.ref.SoftReference;

/**
 * 提供静态显示Toast消息的Helper
 * Created by Hsiang Leekwok on 2015/07/17.
 */
public class ToastHelper {

    private static final String TAG = "ToastHelper";
    private static long lastToasted = 0;
    private SoftReference<Context> mContext;

    private ToastHelper(Context context) {
        mContext = new SoftReference<>(null == context ? App.app() : context);
    }

    public static ToastHelper make() {
        return make(null);
    }

    public static ToastHelper make(Context context) {
        return new ToastHelper(context);
    }

    public void showMsg(int text) {
        showMsg(StringHelper.getString(text));
    }

    public void showMsg(int text, int icon) {
        showMsg(StringHelper.getString(text), StringHelper.getString(icon));
    }

    public void showMsg(String msg) {
        showMsg(msg, null);
    }

    public void showMsg(String msg, String icon) {
        showMsg(msg, icon, null);
    }

    public void showMsg(String msg, String icon, Handler handler) {
        long time = Utils.timestamp();
        if ((time - lastToasted) >= 1000) {
            lastToasted = time;
            if (null == handler) {
                toastInThread(msg, icon);
            } else {
                toastInHandle(handler, msg, icon);
            }
        }
    }

    /**
     * 在线程中显示toast消息
     */
    private void toastInThread(final String msg, final String icon) {

        new Thread() {

            @Override
            public void run() {
                try {
                    // Toast 显示需要出现在一个线程的消息队列中
                    Looper.prepare();
                    toast(msg, icon);
                    Looper.loop();
                } catch (Exception ignore) {
                }
            }
        }.start();

    }

    /**
     * 在Handler中显示toast消息
     */
    private void toastInHandle(Handler handler, final String msg, final String icon) {

        handler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    toast(msg, icon);
                } catch (Exception ignore) {
                }
            }
        });

    }

    private void toast(String msg, String icon) {
        Context context = mContext.get();
        if (null == context) {
            context = App.app();
        }
        View view = View.inflate(context, R.layout.base_custom_toast, null);
        TextView text = (TextView) view.findViewById(R.id.ui_base_custom_toast_text);
        CustomTextView iconView = (CustomTextView) view.findViewById(R.id.ui_base_custom_toast_icon);
        text.setText(msg);
        iconView.setText(icon);
        iconView.setVisibility(null == icon ? View.GONE : View.VISIBLE);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
        LogHelper.log(TAG, msg);
    }
}
