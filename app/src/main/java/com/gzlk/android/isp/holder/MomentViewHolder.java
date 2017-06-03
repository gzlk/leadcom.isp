package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseImageSelectableSupportFragment;
import com.gzlk.android.isp.fragment.individual.MomentDetailsFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.user.Moment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * <b>功能描述：</b>个人动态内容框架<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 21:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 21:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_moment_date)
    private TextView dateTextView;
    @ViewId(R.id.ui_holder_view_moment_month)
    private TextView monthTextView;

    @ViewId(R.id.ui_tool_moment_item_today)
    private CorneredView todayContent;

    @ViewId(R.id.ui_tool_moment_item_content)
    private RelativeLayout momentContent;
    //@ViewId(R.id.ui_tool_moment_item_content_image)
    //private MergeImageView momentMergeImage;
    @ViewId(R.id.ui_tool_moment_item_content_images)
    private View momentImages;
    @ViewId(R.id.ui_tool_moment_item_content_text)
    private TextView momentTextView;
    @ViewId(R.id.ui_tool_moment_item_content_image_count)
    private TextView momentImageCount;

    @ViewId(R.id.ui_tool_image_merge_image_1)
    private ImageDisplayer image1;
    @ViewId(R.id.ui_tool_image_merge_image_2)
    private ImageDisplayer image2;
    @ViewId(R.id.ui_tool_image_merge_image_3)
    private ImageDisplayer image3;
    @ViewId(R.id.ui_tool_image_merge_image_4)
    private ImageDisplayer image4;

    @ViewId(R.id.ui_tool_horizontal_progressbar)
    private MaterialProgressBar progressBar;

    private String today, yesterday, todayId, fmt;

    private int imagesSize, multiImageSize, imageMargin;

    public MomentViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        today = StringHelper.getString(R.string.ui_base_text_today);
        yesterday = StringHelper.getString(R.string.ui_base_text_yesterday);
        imagesSize = getDimension(R.dimen.ui_individual_moment_content_size);
        imageMargin = getDimension(R.dimen.ui_static_dp_1);
        multiImageSize = (imagesSize - imageMargin) / 2;
        // 图3、图4都是固定的尺寸
        resizeImage(image3, multiImageSize, multiImageSize);
        resizeImage(image4, multiImageSize, multiImageSize);
        todayId = StringHelper.getString(R.string.ui_text_moment_item_default_today);
        image1.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        image2.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        image3.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        image4.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        fmt = StringHelper.getString(R.string.ui_base_text_date_time_format);
    }

    private void resizeImage(ImageDisplayer image, int width, int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
        params.width = width;
        params.height = height;
        image.setLayoutParams(params);
    }

    private void showToday() {
        showTime(today, "");
        todayContent.setVisibility(View.VISIBLE);
        momentContent.setVisibility(View.GONE);
    }

    private long dayBegin(boolean today, boolean end, long time) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        if (time > 0) {
            calendar.setTimeInMillis(time);
        }
        if (!today) {
            calendar.add(Calendar.DATE, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, end ? 23 : 0);
        calendar.set(Calendar.MINUTE, end ? 59 : 0);
        calendar.set(Calendar.SECOND, end ? 59 : 0);
        calendar.set(Calendar.MILLISECOND, 1);
        return calendar.getTimeInMillis();
    }

    public void showContent(Moment moment) {
        if (moment.getId().contains(todayId)) {
            showToday();
        } else {
            todayContent.setVisibility(View.GONE);
            momentContent.setVisibility(View.VISIBLE);
            Moment previous = null;
            if (null != gotPositionListener) {
                previous = gotPositionListener.previous(getAdapterPosition());
            }

            long todayBegin = dayBegin(true, false, 0);
            long yesterday = dayBegin(false, false, 0);
            long preDate = null == previous ? todayBegin : Utils.parseDate(fmt, previous.getCreateDate()).getTime();
            long createDate = Utils.parseDate(fmt, moment.getCreateDate()).getTime();
            showTime(preDate, createDate, dayBegin(true, true, createDate), yesterday, todayBegin);
            showMoment(moment);
        }
    }

    private void showTime(long preDay, long createDate, long endThisDay, long yesterday, long today) {
        if (createDate > today) {
            showTime(preDay > today ? "" : this.today, "");
        } else if (createDate > yesterday) {
            showTime(preDay > today ? this.yesterday : "", "");
        } else {
            if (preDay > endThisDay) {
                // 上一条不是此日的，则显示日期
                showTime(Utils.format("dd", createDate), Utils.format("MM月", createDate));
            } else {
                showTime("", "");
            }
        }
    }

    private void showTime(String d, String m) {
        dateTextView.setText(d);
        monthTextView.setText(m);
    }

    private void showMoment(Moment moment) {
        momentTextView.setText(StringHelper.escapeFromHtml(moment.getContent()));
        int size = null == moment.getImage() ? 0 : moment.getImage().size();
        momentImageCount.setText(getContext().getString(R.string.ui_text_moment_item_image_count, size));
        momentImageCount.setVisibility(size <= 0 ? View.GONE : View.VISIBLE);
        momentImages.setVisibility(size <= 0 ? View.GONE : View.VISIBLE);
        clearImages(size);
        if (size > 0) {
            showImages(size, moment.getImage());
        }
    }

    private void clearImages(int size) {
        if (size < 1) {
            image1.clearImage();
        }
        if (size < 2) {
            image2.clearImage();
        }
        if (size < 3) {
            image3.clearImage();
        }
        if (size < 4) {
            image4.clearImage();
        }
    }

    private void showImages(int size, ArrayList<String> urls) {
        int index = 0;
        for (String url : urls) {
            switch (index) {
                case 0:
                    displayImage1(url, size);
                    break;
                case 1:
                    displayImage2(url, size);
                    break;
                case 2:
                    displayImage3(url);
                    break;
                case 3:
                    displayImage4(url);
                    break;
            }
            index++;
        }
    }

    // 显示第一张图片
    private void displayImage1(String url, int size) {
        int width = size <= 1 ? imagesSize : multiImageSize;
        int height = size >= 4 ? multiImageSize : imagesSize;
        resizeImage(image1, width, height);
        image1.displayImage(url, width, height, false, false);
    }

    private void displayImage2(String url, int size) {
        int height = size <= 2 ? imagesSize : multiImageSize;
        resizeImage(image2, multiImageSize, height);
        image2.displayImage(url, multiImageSize, height, false, false);
    }

    private void displayImage3(String url) {
        image3.displayImage(url, multiImageSize, false, false);
    }

    private void displayImage4(String url) {
        image4.displayImage(url, multiImageSize, false, false);
    }

    private Moment getFromAdapter() {
        if (null != dataHandlerBoundDataListener) {
            Object object = dataHandlerBoundDataListener.onHandlerBoundData(this);
            if (null != object && object instanceof Moment) {
                return (Moment) object;
            }
        }
        return null;
    }

    @Click({R.id.ui_holder_view_moment_content_clicker})
    private void viewClick(View view) {
        Moment moment = getFromAdapter();
        if (null != moment) {
            // 点击打开新窗口查看详情
            if (moment.getId().contains(todayId)) {
                ((BaseImageSelectableSupportFragment) fragment()).openImageSelector();
            } else {
                // 默认显示第一张图片
                openActivity(MomentDetailsFragment.class.getName(), format("%s,0", moment.getId()), false, false, true);
            }
        }
    }

    private OnGotPositionListener gotPositionListener;

    public void addOnGotPositionListener(OnGotPositionListener l) {
        gotPositionListener = l;
    }

    public interface OnGotPositionListener {
        Moment previous(int myPosition);
    }
}
