package com.leadcom.android.isp.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;

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

    // 最大初始展示行数，动画时长
    private int expandLines, duration;
    // 实际文本的高度
    private int realTextHeight;
    // 剩余展示的高度
    private int lastHeight;
    // 收起时的高度
    private int collapsedHeight;
    // 文本是否更改
    private boolean isChanged = false;
    // 初始化是收缩状态
    private boolean isCollapsed = true;
    // 是否正在执行动画
    private boolean isAnimate = false;
    // 是否允许动画
    private boolean animatable = true;
    // 当前内容是否可以展开、收缩
    private boolean isExpandCollapseEnable = false;

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
            duration = array.getInteger(R.styleable.ExpandableView_animationDuration, getResources().getInteger(R.integer.integer_default_animate_duration));
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
            animatable = array.getBoolean(R.styleable.ExpandableView_animatable, true);
        } finally {
            array.recycle();
        }
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
                    ExpandCollapseAnimation anim = null;
                    isCollapsed = !isCollapsed;
                    if (isCollapsed) {
                        if (animatable) {
                            anim = new ExpandCollapseAnimation(getHeight(), collapsedHeight);
                        } else {
                            handlerView.setText(expandText);
                        }
                    } else {
                        if (animatable) {
                            anim = new ExpandCollapseAnimation(getHeight(), realTextHeight + lastHeight);
                        } else {
                            handlerView.setText(collapseText);
                        }
                    }
                    if (animatable && null != anim) {
                        anim.setFillAfter(true);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isAnimate = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //clearAnimation();
                                isAnimate = false;
                                handlerView.setText(isCollapsed ? expandText : collapseText);
                                if (null != listener) {
                                    listener.onExpandStateChange(!isCollapsed);
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        //clearAnimation();
                        startAnimation(anim);
                    } else {

                        // 不带动画的处理方式
                        isChanged = true;
                        requestLayout();

                        if (null != listener) {
                            listener.onExpandStateChange(!isCollapsed);
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isAnimate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果隐藏控件或者textview的值没有发生改变，那么不进行测量
        if (getVisibility() == GONE || !isChanged) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        isChanged = false;

        // 初始时
        handlerView.setVisibility(GONE);
        textView.setMaxLines(Integer.MAX_VALUE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 如果本身行数没有达到预定，则不需要再继续
        final int w = widthMeasureSpec, h = heightMeasureSpec;
        textView.post(new Runnable() {
            @Override
            public void run() {
                int lines = textView.getLineCount();
                if (lines <= expandLines) {
                    isExpandCollapseEnable = false;
                } else {
                    handlerView.setVisibility(VISIBLE);
                    isExpandCollapseEnable = true;
                    realTextHeight = getRealTextHeight();
                    if (isCollapsed) {
                        textView.setMaxLines(expandLines);
                    }
                    measure(w, h);

                    if (isCollapsed) {
                        lastHeight = getHeight() - textView.getHeight();
                        collapsedHeight = getMeasuredHeight();
                    }
                }
            }
        });
    }

    public int getLineCount() {
        return textView.getLineCount();
    }

    /**
     * 当前显示的内容是否需要展开、收缩
     */
    public boolean isExpandCollapseEnable() {
        return isExpandCollapseEnable;
    }

    /**
     * 获取文本的真实高度
     */
    private int getRealTextHeight() {
        // getLineTop 返回值是一个根据行数而形成等差序列，如果参数为行数，则值即为文本的高度
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        return textHeight + textView.getCompoundPaddingBottom() + textView.getCompoundPaddingTop();
    }

    public void setText(int res) {
        setText(getResources().getString(res));
    }

    public void setText(CharSequence text) {
        isChanged = true;
        textView.setText(text);
    }

    public void setText(CharSequence text, boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
        if (isCollapsed) {
            handlerView.setText(collapseText);
        } else {
            handlerView.setText(expandText);
        }
        //clearAnimation();
        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    /**
     * 展开、收缩动画
     */
    private class ExpandCollapseAnimation extends Animation {
        int startValue;
        int endValue;

        ExpandCollapseAnimation(int startValue, int endValue) {
            setDuration(duration);
            this.startValue = startValue;
            this.endValue = endValue;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            int height = (int) ((endValue - startValue) * interpolatedTime + startValue);
            textView.setMaxHeight(height - lastHeight);
            getLayoutParams().height = height;
            requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    private OnExpandStateChangeListener listener;

    /**
     * 设置展开、收起回调
     */
    public void setOnExpandStateChangeListener(OnExpandStateChangeListener l) {
        listener = l;
    }

    /***/
    public interface OnExpandStateChangeListener {
        void onExpandStateChange(boolean isExpanded);
    }
}
