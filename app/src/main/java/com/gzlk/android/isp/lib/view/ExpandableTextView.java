package com.gzlk.android.isp.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.gzlk.android.isp.R;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 06:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 06:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = "ExpandableTextView";
    private static final String ELLIPSIZE = "... ";
    private static final String MORE = "more";
    private static final String LESS = "less";

    private String mFullText, mMoreText, mLessText;
    private int mMaxLines;

    public ExpandableTextView(Context context) {
        super(context);
        initialize(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        try {
            mMoreText = array.getString(R.styleable.ExpandableTextView_moreText);
            if (TextUtils.isEmpty(mMoreText)) {
                mMoreText = MORE;
            }
            mLessText = array.getString(R.styleable.ExpandableTextView_lessText);
            if (TextUtils.isEmpty(mLessText)) {
                mLessText = LESS;
            }
            mMaxLines = array.getInteger(R.styleable.ExpandableTextView_maxLines, 3);
        } finally {
            array.recycle();
        }
    }

    public void makeExpandable() {
        if (mMaxLines > 0) {
            makeExpandable(mMaxLines);
        }
    }

    public void makeExpandable(int maxLines) {
        makeExpandable(getText().toString(), maxLines);
    }

    public void makeExpandable(String fullText, final int maxLines) {
        mFullText = fullText;
        mMaxLines = maxLines;
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                if (getLineCount() <= maxLines) {
                    setText(mFullText);
                } else {
                    setMovementMethod(LinkMovementMethod.getInstance());
                    showLess();
                }
            }
        });
    }

    /**
     * truncate text and append a clickable {@link #MORE}
     */
    private void showLess() {
        int lineEndIndex = getLayout().getLineEnd(mMaxLines - 1);
        String newText = mFullText.substring(0, lineEndIndex - (ELLIPSIZE.length() + mMoreText.length() + 1))
                + ELLIPSIZE + mMoreText;
        SpannableStringBuilder builder = new SpannableStringBuilder(newText);
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showMore();
            }
        }, newText.length() - mMoreText.length(), newText.length(), 0);
        setText(builder, BufferType.SPANNABLE);
    }

    /**
     * show full text and append a clickable {@link #LESS}
     */
    private void showMore() {
        SpannableStringBuilder builder = new SpannableStringBuilder(mFullText + mLessText);
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showLess();
            }
        }, builder.length() - mLessText.length(), builder.length(), 0);
        setText(builder, BufferType.SPANNABLE);
    }
}
