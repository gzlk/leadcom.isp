package com.leadcom.android.isp.helper;

import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * <b>功能描述：</b>日期时间选择器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/28 20:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DateTimeHelper {

    public static DateTimeHelper helper() {
        return new DateTimeHelper();
    }

    public void show(BaseFragment fragment, boolean hasYear, boolean hasMonth, boolean hasDay, boolean isDialog, String date) {
        show(fragment, hasYear, hasMonth, hasDay, false, false, false, isDialog, date);
    }

    public void show(BaseFragment fragment, boolean hasYear, boolean hasMonth, boolean hasDay, boolean hasHour, boolean hasMinute, boolean hasSecond, boolean isDialog, String date) {
        TimePickerView tpv = new TimePickerView.Builder(fragment.Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (null != pickListener) {
                    pickListener.onPicked(date);
                }
            }
        }).setType(new boolean[]{hasYear, hasMonth, hasDay, hasHour, hasMinute, hasSecond})
                .setTitleBgColor(fragment.getColor(R.color.colorPrimary))
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setContentSize(fragment.getFontDimension(R.dimen.ui_base_text_size))
                .setOutSideCancelable(false)
                .isDialog(isDialog)
                .isCenterLabel(true)
                .build();
        if (StringHelper.isEmpty(date)) {
            tpv.setDate(Calendar.getInstance());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), date));
            tpv.setDate(calendar);
        }
        tpv.show();
    }

    private OnDateTimePickListener pickListener;

    /**
     * 设置时间选择回调接口
     */
    public DateTimeHelper setOnDateTimePickListener(OnDateTimePickListener l) {
        pickListener = l;
        return this;
    }

    public interface OnDateTimePickListener {
        void onPicked(Date date);
    }
}
