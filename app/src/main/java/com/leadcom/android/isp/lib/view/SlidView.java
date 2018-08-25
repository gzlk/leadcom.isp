package com.leadcom.android.isp.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.leadcom.android.isp.R;
import com.hlk.hlklib.etc.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * <b>功能描述：</b>屏幕索引滑动view<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 21:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 21:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SlidView extends View {

    public SlidView(Context context) {
        this(context, null);
    }

    public SlidView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlidView, defStyleAttr, 0);
        initialize(array);
        array.recycle();
        initView();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private void initialize(TypedArray array) {
        normalBackground = array.getColor(R.styleable.SlidView_normalBackground, Color.TRANSPARENT);
        activeBackground = array.getColor(R.styleable.SlidView_activeBackground, Color.parseColor("#66000000"));
        normalTextColor = array.getColor(R.styleable.SlidView_normalTextColor, Color.parseColor("#65656a"));
        activeTextColor = array.getColor(R.styleable.SlidView_activeTextColor, Color.WHITE);
        selectedTextColor = array.getColor(R.styleable.SlidView_selectedColor, Color.parseColor("#ff4081"));
        textSize = array.getDimensionPixelSize(R.styleable.SlidView_textFontSize, Utility.ConvertDp(12));
    }

    private int mWidth, mHeight, mTextHeight, position;
    private Paint paint;
    private Rect mBound;
    private int normalBackground, activeBackground, normalTextColor, activeTextColor, selectedTextColor;
    private int textSize;
    private int yDown, yMove, mTouchSlop;
    private boolean isSlide, isActive = false;
    private String selectTxt;
    private ArrayList<String> list = new ArrayList<>();
    private static final String[] chars = new String[]{"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        mBound = new Rect();
        list.addAll(Arrays.asList(chars));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(isActive ? activeBackground : normalBackground);
        canvas.drawRect(0, 0, (float) mWidth, mHeight, paint);
        for (int i = 0; i < list.size(); i++) {
            String textView = list.get(i);
            if (i == position - 1) {
                paint.setColor(selectedTextColor);
                selectTxt = list.get(i);
                showText(selectTxt, true);
            } else {
                paint.setColor(isActive ? activeTextColor : normalTextColor);
            }
            paint.getTextBounds(textView, 0, textView.length(), mBound);
            canvas.drawText(textView, (mWidth - mBound.width()) / 2, mTextHeight - mBound.height(), paint);
            mTextHeight += mHeight / list.size();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                yDown = y;
                isActive = true;
                break;
            case MotionEvent.ACTION_MOVE:
                yMove = y;
                int dy = yMove - yDown;
                //如果是竖直方向滑动
                if (Math.abs(dy) > mTouchSlop) {
                    isSlide = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isActive = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (list.size() <= 0) {
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTextHeight = mHeight / list.size();
                position = y / (mHeight / (list.size() + 1));
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSlide) {
                    mTextHeight = mHeight / list.size();
                    position = y / (mHeight / list.size() + 1) + 1;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mTextHeight = mHeight / list.size();
                position = 0;
                invalidate();
                showText(selectTxt, false);
                break;
        }
        return true;
    }

    private void showText(String text, boolean shown) {
        if (null != slidChangedListener) {
            slidChangedListener.slidChanged(text, shown);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = widthSize / 2;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = heightSize / 2;
        }
        mWidth = width;
        mHeight = height;
        if (list.size() <= 0) {
            mTextHeight = 50;
        } else {
            mTextHeight = mHeight / list.size();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE) {
            mTextHeight = (list.size() <= 0) ? 50 : (mHeight / list.size());
        }
    }

    public void clearIndex() {
        list.clear();
        invalidate();
    }

    public void add(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        String chr = text.substring(0, 1);
        if (!list.contains(chr)) {
            list.add(chr);
            Collections.sort(list, comparator);
            invalidate();
        }
    }

    private CharComparator comparator = new CharComparator();

    private static class CharComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    private OnSlidChangedListener slidChangedListener;

    public void setOnSlidChangedListener(OnSlidChangedListener l) {
        slidChangedListener = l;
    }

    public interface OnSlidChangedListener {
        void slidChanged(String text, boolean shown);
    }
}
