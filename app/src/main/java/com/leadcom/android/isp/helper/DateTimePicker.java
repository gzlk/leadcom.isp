package com.leadcom.android.isp.helper;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.hlk.hlklib.etc.Utility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <b>功能描述：</b>自定义的时间日期选择器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/28 12:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/28 12:46  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DateTimePicker {

    public static DateTimePicker picker() {
        return new DateTimePicker();
    }

    private ViewGroup decorView;
    private TextView timePickerTitle;
    private TimePickerView timePickerView;

    // 默认选择了时间之后显示title的格式
    private String selectedDateFormat = "yyyy-MM-dd";
    private String cancelText = StringHelper.getString(R.string.ui_text_home_archive_search_clear_time);
    private boolean[] mType = new boolean[]{true, true, true, false, false, false};
    private boolean hasReseatedTypes = false, hasReseatedRang = false;
    private Calendar start, end;

    /**
     * 设置标题显示格式，默认为yyyy
     */
    public DateTimePicker setSelectedTitleFormat(String format) {
        selectedDateFormat = format;
        return this;
    }

    private TimePickerBuilder builder;

    private TimePickerBuilder getBuilder() {
        return new TimePickerBuilder(decorView.getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (null != timePickerTitle) {
                    timePickerTitle.setText(Utils.format(selectedDateFormat, date));
                }
                if (null != onDateTimePickedListener) {
                    onDateTimePickedListener.onPicked(date);
                }
            }
        }).setLayoutRes(R.layout.tool_view_custom_time_picker, new CustomListener() {
            @Override
            public void customLayout(final View root) {
                root.findViewById(R.id.timepicker_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onDateTimePickedListener) {
                            onDateTimePickedListener.onReset();
                        }
                        resetTitle(root);
                    }
                });
                root.findViewById(R.id.timepicker_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 确定时间
                        timePickerView.returnData();
                    }
                });
                ((TextView) root.findViewById(R.id.timepicker_cancel)).setText(cancelText);
                resetTitle(root);
            }

            private void resetTitle(View root) {
                if (null == timePickerTitle) {
                    timePickerTitle = root.findViewById(R.id.timepicker_title);
                }
                timePickerTitle.setText("");
            }
        }).setType(mType)
                .setDecorView(decorView)
                .setOutSideCancelable(false).setDividerColor(ContextCompat.getColor(decorView.getContext(), R.color.textColorHint))
                .setContentTextSize(Utility.ConvertPx(decorView.getResources().getDimensionPixelOffset(R.dimen.ui_base_text_size)))
                .setDate(Calendar.getInstance(Locale.getDefault()))
                .isCenterLabel(false);
    }

    private void initializePickerView() {
        if (null == builder || (hasReseatedRang || hasReseatedTypes)) {
            // 生成新的builder
            builder = getBuilder();
        }
        if (hasReseatedRang) {
            builder.setRangDate(start, end);
        }
        if (hasReseatedTypes) {
            builder = builder.setType(mType);
        }
        if (null == timePickerView || hasReseatedTypes || hasReseatedRang) {
            timePickerView = builder.build();
            hasReseatedTypes = false;
            hasReseatedRang = false;
        }
        timePickerView.setKeyBackCancelable(false);
    }

    /**
     * 设置时间选择范围
     */
    public DateTimePicker setRangDate(Calendar start, Calendar end) {
        this.start = start;
        this.end = end;
        hasReseatedRang = true;
        return this;
    }

    /**
     * 设置需要显示的选择项
     */
    public DateTimePicker setSelectionType(boolean year, boolean month, boolean day, boolean hour, boolean minute, boolean second) {
        if (year != mType[0] || month != mType[1] || day != mType[2] || hour != mType[3] || minute != mType[4] || second != mType[5]) {
            hasReseatedTypes = true;
        }
        mType = new boolean[]{year, month, day, hour, minute, second};
        return this;
    }

    public DateTimePicker setCancelText(int resId) {
        cancelText = StringHelper.getString(resId);
        return this;
    }

    public DateTimePicker setCancelText(String text) {
        cancelText = text;
        return this;
    }

    public void show(ViewGroup decorView) {
        this.decorView = decorView;
        if (null == timePickerView || hasReseatedTypes) {
            initializePickerView();
        }
        timePickerView.show();
    }

    private OnDateTimePickedListener onDateTimePickedListener;

    /**
     * 设置时间选择之后的回调
     */
    public DateTimePicker setOnDateTimePickedListener(OnDateTimePickedListener l) {
        onDateTimePickedListener = l;
        return this;
    }

    public interface OnDateTimePickedListener {
        void onPicked(Date date);

        void onReset();
    }
}
