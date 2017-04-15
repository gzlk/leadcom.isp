package com.gzlk.android.isp.task;

import android.content.Intent;

import com.gzlk.android.isp.etc.ImageCompress;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.listener.OnTaskPreparedListener;
import com.gzlk.android.isp.listener.OnTaskProcessingListener;
import com.hlk.hlklib.etc.Cryptography;
import com.hlk.hlklib.tasks.AsyncedTask;

import java.io.File;
import java.util.Date;
import java.util.Locale;

/**
 * <b>功能描述：</b>压缩图片<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 00:16 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 00:16 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 * <p>
 * 参数列表说明：<br>
 * <ul>
 * <li>0: imagePath，本地图片路径</li>
 * <li>1: targetPath，压缩后的图片路径</li>
 * <li>2: targetWidth，想要压缩后的图片的宽度</li>
 * <li>3: targetHeight，想要压缩后的图片的高度</li>
 * </ul>
 * </p>
 */

public final class CompressImageTask extends AsyncedTask<String, Integer, Integer> {

    private String compressedPath;
    private String errors;
    private boolean debuggable = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (null != preparedListener) {
            preparedListener.onPrepared();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        if (params.length != 4) {
            errors = "Not enough parameters for compress image.";
            return 1;
        }
        String local = params[0];
        String to = params[1];
        int toWidth = Integer.valueOf(params[2]);
        int toHeight = Integer.valueOf(params[3]);

        File f = new File(local);
        if (f.exists()) {
            compressedPath = compressImage(local, to, toWidth, toHeight);
            return StringHelper.isEmpty(errors) ? 0 : 3;
        } else {
            errors = "The image you wanna compress is not exist.";
            return 2;
        }
    }

    private static final String _PNG = "png";
    private static final String _JPG = "jpg";
    private static final String _BMP = "bmp";
    private static final String _JPEG = "jpeg";

    private boolean imageSupported(String suffix) {
        return suffix.contains(_JPG) || suffix.contains(_PNG) || suffix.contains(_JPEG) || suffix.contains(_BMP);
    }

    /**
     * 根据传入的指定大小压缩图片并返回压缩后的图片本地缓存地址<br>
     * 如果压缩后的图片存在时直接返回地址
     *
     * @param from     原始图片路径
     * @param to       压缩后的图片的存储目录
     * @param toWidth  要压缩成的图片的宽度
     * @param toHeight 要压缩成的图片的高度
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private String compressImage(String from, String to, int toWidth, int toHeight) {
        String compressedPath = null;
        String suffix = from.substring(from.lastIndexOf(".")).toLowerCase(Locale.getDefault());
        // 只上传 JPG/png 格式的图片
        if (imageSupported(suffix)) {

            compressedPath = chooseFileName(false, from, to, suffix, toWidth, toHeight);
            File file = new File(compressedPath);

            // 如果文件没有被压缩过或压缩后的文件已删除则重新压缩，否则直接使用之前压缩过后的文件
            ImageCompress.compressBitmap(from, compressedPath, toWidth, toHeight);

            // 压缩完毕之后重命名成压缩完成后文件的sha1值
            compressedPath = chooseFileName(true, compressedPath, to, suffix, toWidth, toHeight);

            File f = new File(compressedPath);
            file.renameTo(f);

            errors = "";
        } else {
            errors = "Couldn't support compress \"" + suffix + "\" image.";
            log(errors);
        }
        return compressedPath;
    }

    private String chooseFileName(boolean sha256, String from, String to, String suffix, int width, int height) {
        String path;
        boolean exist;
        do {
            // 暂时使用源文件的 sha1 码当作文件名存储压缩后的图片
            String name = sha256 ? Cryptography.getFileSHA256(from) : Cryptography.getFileSHA1(from);
            // 增加时间变量
            name = StringHelper.format("%s_%d_%d_%d", name, StringHelper.timestamp(), width, height);
            // 二次 sha
            name = sha256 ? Cryptography.sha256(name) : Cryptography.sha1(name);

            path = name + suffix;
            log("temporary compressed image was save to: " + path);

            // 得到完整路径，并检测文件是否存在，如果存在了则另外再换一个文件名
            path = to + path;
            File file = new File(path);
            exist = file.exists();
            if (exist) {
                log("temporary file is exist, now change another one.");
            }
        } while (exist);

        return path;
    }

    @Override
    protected void log(String string) {
        if (debuggable) {
            super.log(string);
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (null != compressCompleteListener) {
            compressCompleteListener.onComplete(compressedPath);
        }
        super.onPostExecute(result);
    }

    /**
     * 设置是否显示debug信息
     */
    public CompressImageTask setDebuggable(boolean debuggable) {
        this.debuggable = debuggable;
        return this;
    }

    private OnTaskPreparedListener preparedListener;

    /**
     * 添加task准备执行时的处理回调
     */
    public CompressImageTask addOnTaskPreparedListener(OnTaskPreparedListener l) {
        preparedListener = l;
        return this;
    }

    private OnCompressCompleteListener compressCompleteListener;

    /**
     * 添加图片压缩处理完毕后的处理回调
     */
    public CompressImageTask addOnCompressCompleteListener(OnCompressCompleteListener l) {
        compressCompleteListener = l;
        return this;
    }

    /**
     * 图片压缩完成事件的处理接口
     */
    public interface OnCompressCompleteListener {
        /**
         * 图片压缩处理完毕
         */
        void onComplete(String compressedPath);
    }
}
