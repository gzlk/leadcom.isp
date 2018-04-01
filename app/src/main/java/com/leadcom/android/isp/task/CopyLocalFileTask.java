package com.leadcom.android.isp.task;

import android.content.ContentValues;
import android.os.Environment;
import android.provider.MediaStore;

import com.hlk.hlklib.tasks.AsyncedTask;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;
import com.netease.nim.uikit.common.util.C;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <b>功能描述：</b>复制本地文件到指定目录和文件名<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/21 20:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/21 20:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class CopyLocalFileTask extends AsyncedTask<String, Integer, Boolean> {

    private String error = "";
    private String targetPath = "";

    @Override
    protected Boolean doInBackground(String... params) {
        String url = params[0];
        String localPath = params[1];
        String name = url.substring(url.lastIndexOf('/'));
        if (FilePreviewHelper.isNimFile(url)) {
            name += ".jpg";
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + App.ROOT_DIR;
        try {
            File f = new File(path);
            if (!f.exists()) {
                if (!f.mkdirs()) {
                    error = StringHelper.getString(R.string.ui_base_text_dictionary_create_fail);
                    return false;
                }
            }

            File file = new File(localPath);
            if (file.exists()) {
                long totalLength = file.length();
                InputStream inputStream = new FileInputStream(localPath);
                targetPath = path + name;
                FileOutputStream fos = new FileOutputStream(targetPath);
                byte[] buffer = new byte[4096];
                int handled = 0;
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    handled += read;
                    fos.write(buffer, 0, read);
                    publishProgress((int) (handled * 1.0 / totalLength * 100));
                }
                fos.close();
                inputStream.close();
                return true;
            } else {
                error = StringHelper.getString(R.string.ui_base_text_file_not_exists);
            }
        } catch (IOException e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            if (ImageCompress.isImage(Attachment.getExtension(targetPath))) {
                //Uri uri = NimUIKit.getUriFromFile(App.app(), targetPath);
                //App.app().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                //App.app().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, uri));
                ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.MIME_TYPE, C.MimeType.MIME_JPEG);
                values.put(MediaStore.Images.Media.DATA, targetPath);
                App.app().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                ToastHelper.make().showMsg(R.string.ui_base_text_downloading_image_completed);
            } else {
                ToastHelper.make().showMsg(R.string.ui_base_text_downloading_file_completed);
            }
        } else {
            ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_base_text_downloading_fail, error));
        }
        if (null != completeListener) {
            completeListener.onComplete();
        }
    }

    private OnTaskCompleteListener completeListener;

    public CopyLocalFileTask setOnTaskCompleteListener(OnTaskCompleteListener listener) {
        completeListener = listener;
        return this;
    }
}
