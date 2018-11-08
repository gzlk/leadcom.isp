package com.leadcom.android.isp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.leadcom.android.isp.R;

/**
 * 渐变颜色的TextView
 */
public class GradientTextView extends AppCompatTextView {

    public GradientTextView(Context context) {
        this(context, null);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView, defStyleAttr, 0);
        try {
            int colorStart = array.getColor(R.styleable.GradientTextView_colorStart, Color.parseColor("#ff3cbaff"));
            int colorEnd = array.getColor(R.styleable.GradientTextView_colorEnd, Color.parseColor("#ff52a2fb"));
            int colorCenter = 0;
            if (array.hasValue(R.styleable.GradientTextView_colorCenter)) {
                colorCenter = array.getColor(R.styleable.GradientTextView_colorCenter, Color.parseColor("#ffdfbb82"));
            }
            colors = 0 == colorCenter ? new int[]{colorStart, colorEnd} : new int[]{colorStart, colorCenter, colorEnd};
            points = 0 == colorCenter ? new float[]{0f, 1f} : new float[]{0f, 0.5f, 1f};
        } finally {
            array.recycle();
        }
    }

    private int[] colors;
    private float[] points;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getPaint().setShader(new LinearGradient(0, 0, getWidth(), getHeight(), colors, points, Shader.TileMode.CLAMP));
        }
    }
}
