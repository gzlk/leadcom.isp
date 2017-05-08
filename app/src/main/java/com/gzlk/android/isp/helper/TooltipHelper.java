package com.gzlk.android.isp.helper;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/01 22:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/01 22:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class TooltipHelper {

    public static final int TYPE_LEFT = -1;
    public static final int TYPE_CENTER = 0;
    public static final int TYPE_RIGHT = 1;

    private static final int[] events = new int[]{
            R.id.ui_tool_popup_menu_text,
            R.id.ui_tool_popup_menu_document_comment_copy,
            R.id.ui_tool_popup_menu_document_comment_delete,
            R.id.ui_tool_popup_menu_squad_contact_organization,
            R.id.ui_tool_popup_menu_squad_contact_phone
    };

    public static PopupWindow showTooltip(final View anchorView, int viewLayout, boolean belowAnchor, int arrowType, View.OnClickListener onClickListener) {
        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.tool_view_tooltip_layout, null);
        visibleView(viewLayout, contentView);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return make(contentView, belowAnchor, arrowType, anchorView, onClickListener);
    }

    public static PopupWindow showTooltip(final View anchorView, String text, final View.OnClickListener onClickListener) {
        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.tool_view_tooltip_layout, null);
        visibleView(R.id.ui_tool_popup_menu_text, contentView);
        TextView textView = (TextView) contentView.findViewById(R.id.ui_tool_popup_menu_text);
        textView.setText(text);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return make(contentView, true, TYPE_CENTER, anchorView, onClickListener);
    }

    private static void visibleView(int resId, View contentView) {
        View view = contentView.findViewById(resId);
        view.setVisibility(View.VISIBLE);
    }

    private static void adjustElement(boolean belowAnchor, int arrowType, View contentView) {
        // 上下箭头的位置
        View upArrow = contentView.findViewById(R.id.ui_tool_view_tooltip_up_arrow);
        View downArrow = contentView.findViewById(R.id.ui_tool_view_tooltip_down_arrow);
        upArrow.setVisibility(belowAnchor ? View.VISIBLE : View.GONE);
        downArrow.setVisibility(belowAnchor ? View.GONE : View.VISIBLE);
        adjustArrow(upArrow, arrowType);
        adjustArrow(downArrow, arrowType);
    }

    private static void adjustArrow(View arrow, int arrowType) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) arrow.getLayoutParams();
        params.gravity = (arrowType < 0 ? Gravity.START : (arrowType == 0 ? Gravity.CENTER : Gravity.END));
    }

    private static void adjustEvents(final PopupWindow popupWindow, View contentView, final View.OnClickListener onClickListener) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (null != onClickListener) {
                    onClickListener.onClick(v);
                }
            }
        };
        contentView.setOnClickListener(clickListener);
        for (int i : events) {
            View view = contentView.findViewById(i);
            if (null != view) {
                view.setOnClickListener(clickListener);
            }
        }

    }

    private static PopupWindow make(View contentView, boolean belowAnchor, int arrowType, View anchorView, final View.OnClickListener onClickListener) {
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
        final PopupWindow popupWindow = new PopupWindow(contentView, contentView.getMeasuredWidth(), contentView.getMeasuredHeight());

        adjustEvents(popupWindow, contentView, onClickListener);

        adjustElement(belowAnchor, arrowType, contentView);

        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        popupWindow.setOutsideTouchable(false);

        // 设置为true之后，PopupWindow内容区域 才可以响应点击事件
        popupWindow.setTouchable(true);

        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键
        popupWindow.setFocusable(true);

        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        int x = location[0] + ((anchorView.getWidth() / 2) * (location[0] > 0 ? -1 : 1)) - (location[0] > 0 ? 0 : (contentView.getWidth() / 2));
        int y = location[1] + (anchorView.getHeight() * (belowAnchor ? 1 : -1));
        // 显示在点击控件的正中间
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
        return popupWindow;
    }

    private static void autoAdjustArrowPos(PopupWindow popupWindow, View contentView, View anchorView) {
        View upArrow = contentView.findViewById(R.id.ui_tool_view_tooltip_up_arrow);
        View downArrow = contentView.findViewById(R.id.ui_tool_view_tooltip_down_arrow);
        int pos[] = new int[2];
        contentView.getLocationOnScreen(pos);
        int popLeftPos = pos[0];
        anchorView.getLocationOnScreen(pos);
        int anchorLeftPos = pos[0];
        //int arrowLeftMargin = anchorLeftPos - popLeftPos + anchorView.getWidth() / 2 - upArrow.getWidth() / 2;
        boolean above = popupWindow.isAboveAnchor();
        upArrow.setVisibility(above ? View.INVISIBLE : View.VISIBLE);
        downArrow.setVisibility(above ? View.VISIBLE : View.INVISIBLE);

        int arrowLeftMargin = contentView.getWidth() / 2 - upArrow.getWidth() / 2;
        RelativeLayout.LayoutParams upArrowParams = (RelativeLayout.LayoutParams) upArrow.getLayoutParams();
        upArrowParams.leftMargin = arrowLeftMargin;
        RelativeLayout.LayoutParams downArrowParams = (RelativeLayout.LayoutParams) downArrow.getLayoutParams();
        downArrowParams.leftMargin = arrowLeftMargin;
    }
}
