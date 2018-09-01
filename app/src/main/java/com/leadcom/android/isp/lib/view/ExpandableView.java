package com.leadcom.android.isp.lib.view;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.TextViewUtils;

/**
 * <b>功能描述：</b>可收缩、展开的View<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/08/24 11:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/08/24 11:04 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ExpandableView extends LinearLayout {

    public ExpandableView(Context context) {
        this(context, null);
    }

    public ExpandableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // 最大初始展示行数
    private int expandLines;

    private TextView textView, handlerView;
    private int contentTextColor, handlerTextColor;
    private int contentTextSize, handlerTextSize;
    private String expandText, collapseText;

    private void init(Context context, AttributeSet attrs) {
        // 只能竖向展示
        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableView);
        try {
            expandLines = array.getInteger(R.styleable.ExpandableView_expandLines, 3);
            contentTextColor = array.getColor(R.styleable.ExpandableView_contentTextColor, ContextCompat.getColor(getContext(), R.color.textColor));
            contentTextSize = array.getDimensionPixelSize(R.styleable.ExpandableView_contentTextSize, getResources().getDimensionPixelOffset(R.dimen.ui_base_text_size));
            handlerTextColor = array.getColor(R.styleable.ExpandableView_handlerTextColor, ContextCompat.getColor(getContext(), R.color.colorAccent));
            handlerTextSize = array.getDimensionPixelSize(R.styleable.ExpandableView_handlerTextSize, getResources().getDimensionPixelOffset(R.dimen.ui_base_text_size_small));
            expandText = array.getString(R.styleable.ExpandableView_expandedText);
            if (TextUtils.isEmpty(expandText)) {
                expandText = getResources().getString(R.string.expandable_view_expand_handle_text);
            }
            collapseText = array.getString(R.styleable.ExpandableView_collapsedText);
            if (TextUtils.isEmpty(collapseText)) {
                collapseText = getResources().getString(R.string.expandable_view_collapse_handle_text);
            }
        } finally {
            array.recycle();
        }
    }

    private boolean isCollapsed() {
        return state == STATE_COLLAPSED;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (null == textView) {
            textView = findViewById(R.id.expandable_view_container_id);
            handlerView = findViewById(R.id.expandable_view_handler_id);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
            textView.setTextColor(contentTextColor);
            handlerView.setTextSize(TypedValue.COMPLEX_UNIT_PX, handlerTextSize);
            handlerView.setTextColor(handlerTextColor);
            handlerView.setText(expandText);
            handlerView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 展开、收起只有2种情况下可以点击
                    state = state == STATE_COLLAPSED ? STATE_EXPANDED : STATE_COLLAPSED;
                    //textView.setMaxLines(isCollapsed() ? expandLines : Integer.MAX_VALUE);
                    Animator animator = TextViewUtils.setMaxLinesWithAnimation(textView, isCollapsed() ? expandLines : Integer.MAX_VALUE);
                    assert animator != null;
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            handlerView.setText(isCollapsed() ? expandText : collapseText);
                            if (null != listener) {
                                listener.onStateChanged(state);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            });
        }
    }

    public void setText(int res) {
        setText(getResources().getString(res));
    }

    public void setText(CharSequence text) {
        if (state == STATE_NONE) {
            textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    textView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int line = textView.getLineCount();
                    if (line > expandLines) {
                        textView.setMaxLines(expandLines);
                        handlerView.setVisibility(VISIBLE);
                        state = STATE_COLLAPSED;
                    } else {
                        handlerView.setVisibility(GONE);
                        state = STATE_NOT_OVERFLOW;
                    }
                    if (null != listener) {
                        listener.onStateChanged(state);
                    }
                    return true;
                }
            });
        } else {
            switch (state) {
                case STATE_COLLAPSED:
                    textView.setMaxLines(expandLines);
                    handlerView.setVisibility(VISIBLE);
                    if (null != listener) {
                        listener.onStateChanged(state);
                    }
                    break;
                case STATE_EXPANDED:
                    textView.setMaxLines(Integer.MAX_VALUE);
                    handlerView.setVisibility(VISIBLE);
                    if (null != listener) {
                        listener.onStateChanged(state);
                    }
                    break;
                case STATE_NOT_OVERFLOW:
                    handlerView.setVisibility(GONE);
                    if (null != listener) {
                        listener.onStateChanged(state);
                    }
                    break;
            }
        }
        textView.setText(text);
    }

    public void setText(CharSequence text, boolean isCollapsed) {
        state = isCollapsed ? STATE_COLLAPSED : STATE_EXPANDED;
        handlerView.setText(isCollapsed() ? expandText : collapseText);
        setText(text);
    }

    private OnExpandStateChangeListener listener;

    /**
     * 设置展开、收起回调
     */
    public void setOnExpandStateChangeListener(OnExpandStateChangeListener l) {
        listener = l;
    }

    private static final int STATE_NONE = 0;
    public static final int STATE_NOT_OVERFLOW = 1;
    public static final int STATE_COLLAPSED = 2;
    public static final int STATE_EXPANDED = 3;

    private int state = STATE_NONE;

    /**
     * 展开、收缩状态改变时的回调
     */
    public interface OnExpandStateChangeListener {

        void onStateChanged(int state);
    }
}
