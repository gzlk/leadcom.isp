package com.leadcom.android.isp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;

import com.leadcom.android.isp.R;

/**
 * <b>功能描述：</b>视频播放器（横屏）<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/10 09:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/10 09:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VideoPlayerActivity extends BaseActivity  {

    private static final String TAG_TITLE = "vpla_title";
    private static final String TAG_URL = "vpla_url";

    public static void open(Context context, String videoTitle, String videoUrl) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(TAG_TITLE, videoTitle);
        intent.putExtra(TAG_URL, videoUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_player_landscape);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
