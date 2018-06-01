package com.leadcom.android.isp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.upload.Upload;
import com.leadcom.android.isp.api.upload.UploadRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.task.CompressImageTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <b>功能描述：</b>保存草稿的服务<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/30 20:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DraftService extends Service {

    private static final String TAG = DraftService.class.getSimpleName();
    public static final String ACTION_DRAFT = "com.leadcom.android.isp.service.DRAFT";
    private static final String IMAGE_WIDTH = "IMAGE_WIDTH";
    private static final String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    public static final String TAG_ARCHIVE = "DSS_DRAFT";

    public static void start(Archive draft, int toWidth, int toHeight) {
        Intent intent = new Intent(App.app(), DraftService.class);
        intent.setAction(ACTION_DRAFT);
        intent.putExtra(TAG_ARCHIVE, draft);
        intent.putExtra(IMAGE_WIDTH, toWidth);
        intent.putExtra(IMAGE_HEIGHT, toHeight);
        App.app().startService(intent);
    }

    private void log(String string) {
        LogHelper.log(TAG, string);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        log("onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (ACTION_DRAFT.equals(action)) {
                try {
                    Archive archive = (Archive) intent.getSerializableExtra(TAG_ARCHIVE);
                    int width = intent.getIntExtra(IMAGE_WIDTH, 0);
                    int height = intent.getIntExtra(IMAGE_HEIGHT, 0);
                    if (null != archive) {
                        log(Archive.toJson(archive));
                        saveDraft(archive, width, height);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveDraft(final Archive archive, int toWidth, int toHeight) {
        if (archive.getImage().size() > 0) {
            // 有图片，且有图片是本地图片时，先上传图片
            ArrayList<String> images = new ArrayList<>();
            Iterator<Attachment> iterator = archive.getImage().iterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                if (!Utils.isUrl(attachment.getUrl())) {
                    images.add(attachment.getFullPath());
                    iterator.remove();
                }
            }
            if (images.size() > 0) {
                new CompressImageTask()
                        .setDebuggable(true)
                        .addOnCompressCompleteListener(new CompressImageTask.OnCompressCompleteListener() {
                            @Override
                            public void onComplete(ArrayList<String> compressed) {
                                UploadRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Upload>() {
                                    @Override
                                    public void onResponse(List<Upload> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                                        super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                                        if (success && null != list) {
                                            for (Upload upload : list) {
                                                archive.getImage().add(new Attachment(upload));
                                            }
                                        }
                                        savingDraft(archive);
                                    }
                                }).upload(compressed);
                            }
                        })
                        .exec(Json.gson().toJson(images),
                                App.app().getLocalImageDir(),
                                String.valueOf(toWidth),
                                String.valueOf(toHeight));
            } else {
                savingDraft(archive);
            }
        } else {
            savingDraft(archive);
        }
    }

    private void savingDraft(Archive archive) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    if (null != archive && !StringHelper.isEmpty(archive.getId())) {
                        broadcasting(archive);
                    }
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_draft_saved);
                }
            }
        }).save(archive, true, false);
    }

    private void broadcasting(Archive archive) {
        Intent intent = new Intent(ACTION_DRAFT);
        intent.putExtra(TAG_ARCHIVE, archive);
        sendBroadcast(intent);
        stopSelf();
    }
}
