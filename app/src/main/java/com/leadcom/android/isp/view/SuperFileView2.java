package com.leadcom.android.isp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;


/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/26 09:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class SuperFileView2 extends FrameLayout implements TbsReaderView.ReaderCallback {

    private TbsReaderView mTbsReaderView;
    private Context context;

    public SuperFileView2(@NonNull Context context) {
        this(context, null, 0);
    }

    public SuperFileView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperFileView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        this.context = context;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    public void displayFile(File mFile) {

        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //加载文件
            Bundle localBundle = new Bundle();
            localBundle.putString("filePath", mFile.toString());

            localBundle.putString("tempPath", App.app().getCachePath(App.TEMP_DIR));

            if (this.mTbsReaderView == null)
                this.mTbsReaderView = getTbsReaderView(context);
            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
            } else {
                ToastHelper.helper().showMsg("Cannot open file with X5 core.");
            }
        } else {
            ToastHelper.helper().showMsg("文件路径无效！");
        }

    }

    /***
     * 获取文件类型
     */
    private String getFileType(String paramString) {
        String str = "";

        String TAG = "SuperFileView";
        if (TextUtils.isEmpty(paramString)) {
            LogHelper.log(TAG, "paramString---->null");
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            LogHelper.log(TAG, "last index of dot(.): i <= -1");
            return str;
        }


        str = paramString.substring(i + 1);
        return str;
    }

    public void onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }
}
