package com.leadcom.android.isp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.leadcom.android.isp.helper.LogHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>功能：</b>SMS收取通知接收器<br />
 * <b>作者：</b>Hsiang Leekwok <br />
 * <b>时间：</b>2016/01/05 20:39 <br />
 * <b>邮箱：</b>xiang.l.g@gmail.com <br />
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELIVER = "android.provider.Telephony.SMS_DELIVER";
    /**
     * 提取字符串中的数字
     */
    public static final String REGULAR_D = "\\d+";
    private static final String TAG = "SmsReceiver";

    /**
     * 从指定的字符串中提取首次出现的数字字符串
     */
    public static String getVerifyCode(String text) {
        Pattern pattern = Pattern.compile(REGULAR_D);
        Matcher ma = pattern.matcher(text);
        if (ma.find()) {
            return ma.group(0);
        }
        return null;
    }

    public static IntentFilter getIntentFilter(){
        IntentFilter intent = new IntentFilter();
        intent.addAction(SMS_RECEIVED);
        intent.addAction(SMS_DELIVER);
        return intent;
    }

    private void log(String string) {
        LogHelper.log(TAG, string);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        log("ACTION: " + intent.getAction());
        Bundle extras = intent.getExtras();
        if (null == extras) {
            log("SmsReceiver received null extra message");
            return;
        }

        Object[] smsExtras = (Object[]) extras.get("pdus");
        if (smsExtras == null || smsExtras.length == 0) {
            return;
        }

        for (Object smsExtra : smsExtras) {
            try {
                byte[] smsBytes = (byte[]) smsExtra;
                SmsMessage sms = SmsMessage.createFromPdu(smsBytes);
                String addr = sms.getDisplayOriginatingAddress();
                String text = sms.getDisplayMessageBody();
                if (null != mOnSmsReceivedListener) {
                    mOnSmsReceivedListener.onReceived(addr, text);
                }
//                if (text.contains("乐享百工")) {
//                    // 提取验证码
//                    text = getVerifyCode(text);
//                    if (!mWarninged) {
//                        mWarninged = true;
//                        // 可以确定修改了
//                        bConfirm.setEnabled(true);
//                        stopCount();
//                        // 显示通知
//                        warningVerify(text);
//                    }
//                }
                //log(String.format("\nFrom: %s\nBody: %s", addr, text));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private OnSmsReceivedListener mOnSmsReceivedListener;

    public void addOnSmsReceivedListener(OnSmsReceivedListener l) {
        mOnSmsReceivedListener = l;
    }

    public interface OnSmsReceivedListener {
        void onReceived(String address, String body);
    }
}
