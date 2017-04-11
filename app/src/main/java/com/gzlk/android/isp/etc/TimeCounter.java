package com.gzlk.android.isp.etc;

import android.os.CountDownTimer;

/**
 * <b>功能描述：</b>计时器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/11 15:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/11 15:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class TimeCounter extends CountDownTimer {

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public TimeCounter(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mOnTimeCounterListener != null) {
            mOnTimeCounterListener.onTick(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        if (mOnTimeCounterListener != null) {
            mOnTimeCounterListener.onFinished();
        }
    }

    private OnTimeCounterListener mOnTimeCounterListener;

    /**
     * 添加计时事件监听回调
     */
    public void addOnTimeCounterListener(OnTimeCounterListener l) {
        mOnTimeCounterListener = l;
    }

    public interface OnTimeCounterListener {
        /**
         * 计时步数
         */
        void onTick(long timeLeft);

        /**
         * 计时完成
         */
        void onFinished();
    }
}
