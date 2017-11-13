package com.gzlk.android.isp.nim.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.CollectionRequest;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.model.user.Collection;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>视频播放页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/26 21:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/26 21:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VideoPlayerActivity extends BaseActivity implements EasyVideoCallback {

    private static final String PARAM_URL = "vpa_video_url";

    /**
     * 启动视频预览播放
     */
    public static void start(Context context, String url) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_URL, url);
        intent.setClass(context, VideoPlayerActivity.class);
        context.startActivity(intent);
    }

    @ViewId(R.id.ui_nim_video_player)
    private EasyVideoPlayer player;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleView;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightView;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.nim_activity_video_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewUtility.bind(this);

        titleView.setText(null);
        rightView.setText(R.string.ui_base_text_favorite);
        // Grabs a reference to the player view
        //player = (EasyVideoPlayer) findViewById(R.id.ui_nim_video_player);
        // Sets the callback to this Activity, since it inherits EasyVideoCallback
        player.setCallback(this);

        // Get the video url source from intent
        videoUrl = getIntent().getStringExtra(PARAM_URL) + "#.mp4";
        //url="http://120.25.124.199:8008/group1/M00/00/07/cErYIVlRFM-AGxabANsDmhQhlq8675.mp4";
        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(videoUrl));

        ToastHelper.make(this).showMsg("建议在wifi环境下播放视频");
        // From here, the player view will show a progress indicator until the player is prepared.
        // Once it's prepared, the progress indicator goes away and the controls become enabled for the user to begin playback.
    }

    @Click({R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).add(videoUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer easyVideoPlayer) {

    }

    @Override
    public void onPaused(EasyVideoPlayer easyVideoPlayer) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer easyVideoPlayer) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer easyVideoPlayer) {

    }

    @Override
    public void onBuffering(int i) {

    }

    @Override
    public void onError(EasyVideoPlayer easyVideoPlayer, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer easyVideoPlayer) {

    }

    @Override
    public void onRetry(EasyVideoPlayer easyVideoPlayer, Uri uri) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer easyVideoPlayer, Uri uri) {

    }
}
