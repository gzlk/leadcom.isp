package com.netease.nim.uikit.session.module.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/06 08:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/06 08:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IconFontTextView extends AppCompatTextView {

    public IconFontTextView(Context context) {
        super(context);
        resetTypeFace(context);
    }

    public IconFontTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resetTypeFace(context);
    }

    public IconFontTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resetTypeFace(context);
    }

    private void resetTypeFace(Context context) {
        setTypeface(LoadingTypefaceHelper.get(context, "fonts/iconfont.ttf"));
    }
}
