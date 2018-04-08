package com.leadcom.android.isp.nim.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.helper.DownloadingHelper;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.listener.OnTaskFailureListener;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.user.Collection;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;

import java.io.File;

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
    @ViewId(R.id.ui_viewer_image_title_container)
    private View titleLayout;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleView;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightView;
    @ViewId(R.id.ui_tool_loading_container)
    private View loadingView;
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
        videoUrl = getIntent().getStringExtra(PARAM_URL);
        //url="http://120.25.124.199:8008/group1/M00/00/07/cErYIVlRFM-AGxabANsDmhQhlq8675.mp4";
        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(videoUrl + "#.mp4"));

        //ToastHelper.make(this).showMsg("建议在wifi环境下播放视频");
        // From here, the player view will show a progress indicator until the player is prepared.
        // Once it's prepared, the progress indicator goes away and the controls become enabled for the user to begin playback.

        titleLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLocalStorage();
            }
        }, 300);
    }

    /**
     * 检测本地缓存的视频文件
     */
    private void checkLocalStorage() {
        boolean isNim = FilePreviewHelper.isNimFile(videoUrl);
        String local = HttpHelper.helper().getLocalFilePath(videoUrl, App.VIDEO_DIR);
        String extension = Attachment.getExtension(videoUrl);
        if (isNim) {
            // 易信文件需要后续加.mp4才行，否则有可能打不开视频
            local += ".mp4";
            extension = "mp4";
        }
        File file = new File(local);
        if (file.exists()) {
            resetPlayer(local);
        } else {
            warningNeedDownload(local, extension);
        }
    }

    private void warningNeedDownload(final String local, final String extension) {
        DialogHelper.init(this).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return View.inflate(VideoPlayerActivity.this, R.layout.popup_dialog_video_need_download, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 下载后播放
                startDownload(local, extension);
                return true;
            }
        }).addOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                player.start();
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private void startDownload(final String local, String extension) {
        loadingView.setVisibility(View.VISIBLE);
        DownloadingHelper.helper().init(this).setOnTaskCompleteListener(new OnTaskCompleteListener() {
            @Override
            public void onComplete() {
                loadingView.setVisibility(View.GONE);
                resetPlayer(local);
            }
        }).setOnTaskFailureListener(new OnTaskFailureListener() {
            @Override
            public void onFailure() {
                loadingView.setVisibility(View.GONE);
            }
        }).setShowNotification(false).download(videoUrl, local, extension, "", "");
    }

    private void resetPlayer(String path) {
        player.stop();
        player.reset();
        player.setSource(Uri.parse("file://" + path));
        player.start();
    }

    @Click({R.id.ui_ui_custom_title_left_container,
            R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                if (player.isPrepared() || player.isPlaying()) {
                    player.stop();
                }
                finish();
                break;
            case R.id.ui_ui_custom_title_right_container:
                collectVideo();
                break;
        }
    }

    private void collectVideo() {
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

    private void toggleTitleView(final boolean shown) {
        //final boolean shown = titleLayout.getAlpha() >= 1;
        int height = getActionBarSize();
        titleLayout.animate().translationY(shown ? 0 : -height).alpha(shown ? 1 : -1).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (shown) {
                    titleLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!shown) {
                    titleLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
        toggleTitleView(true);
    }

    @Override
    public void onStarted(EasyVideoPlayer easyVideoPlayer) {
        toggleTitleView(false);
    }

    @Override
    public void onPaused(EasyVideoPlayer easyVideoPlayer) {
        toggleTitleView(true);
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
        toggleTitleView(true);
    }

    @Override
    public void onCompletion(EasyVideoPlayer easyVideoPlayer) {
        toggleTitleView(true);
    }

    @Override
    public void onRetry(EasyVideoPlayer easyVideoPlayer, Uri uri) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer easyVideoPlayer, Uri uri) {

    }
}
