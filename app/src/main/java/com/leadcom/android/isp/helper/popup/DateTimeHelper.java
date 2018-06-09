package com.leadcom.android.isp.helper.popup;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Model;

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

    private TimePickerView timePickerView;

    public void show(BaseFragment fragment, boolean hasYear, boolean hasMonth, boolean hasDay, boolean hasHour, boolean hasMinute, boolean hasSecond, boolean isDialog, String date) {
        if (null == timePickerView) {
            timePickerView = new TimePickerBuilder(fragment.Activity(), new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    if (null != pickListener) {
                        pickListener.onPicked(date);
                    }
                    timePickerView.dismiss();
                }
            }).setLayoutRes(R.layout.tool_view_custom_time_picker_contianer, new CustomListener() {
                @Override
                public void customLayout(View root) {
                    root.findViewById(R.id.timepicker_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 确定时间
                            timePickerView.returnData();
                        }
                    });
                    ((TextView) root.findViewById(R.id.timepicker_cancel)).setText(R.string.ui_base_text_cancel);
                    root.findViewById(R.id.timepicker_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timePickerView.dismiss();
                        }
                    });
                }
            }).setType(new boolean[]{hasYear, hasMonth, hasDay, hasHour, hasMinute, hasSecond})
                    .setTitleBgColor(fragment.getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentTextSize(fragment.getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(false)
                    .isDialog(true)
                    .isCenterLabel(true)
                    .build();
        }
        if (StringHelper.isEmpty(date) || date.equals(Model.DFT_DATE)) {
            timePickerView.setDate(Calendar.getInstance());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), date));
            timePickerView.setDate(calendar);
        }
        timePickerView.show(true);
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
