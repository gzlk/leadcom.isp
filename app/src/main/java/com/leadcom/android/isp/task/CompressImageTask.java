package com.leadcom.android.isp.task;

import android.graphics.BitmapFactory;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTaskPreparedListener;
import com.hlk.hlklib.etc.Cryptography;
import com.hlk.hlklib.tasks.AsyncedTask;
import com.leadcom.android.isp.model.common.Attachment;

import java.io.File;
import java.util.ArrayList;
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

    private String errors;
    private boolean debuggable = false;

    /**
     * 已压缩了的图片列表
     */
    private ArrayList<String> compressed = new ArrayList<>();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (null != preparedListener) {
            preparedListener.onPrepared();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        int ret = 0;
        if (params.length != 4) {
            errors = "Not enough parameters for compress image.";
            ret = 1;
        }
        String json = params[0];
        ArrayList<String> images = Json.gson().fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
        String to = params[1];
        int toWidth = Integer.valueOf(params[2]);
        int toHeight = Integer.valueOf(params[3]);

        for (String image : images) {
            File f = new File(image);
            if (f.exists()) {
                String ext = Attachment.getExtension(image);
                assert ext != null;
                if (!ImageCompress.isNeedCompress(ext)) {
                    this.compressed.add(image);
                    continue;
                }
                if (!ImageCompress.isImage(ext)) {
                    // 不是图片文件时，不需要压缩
                    this.compressed.add(image);
                    continue;
                }
                if (!StringHelper.isEmpty(ext, true) && (ext.contains("gif") || ext.contains("webp"))) {
                    // gif 不要压缩，webp 也不需要压缩
                    log("not need compress: " + image);
                    this.compressed.add(image);
                } else if (f.length() < 1024 * 1024) {
                    // 图片小于1M时，不需要压缩
                    log("less than 1M: " + image);
                    this.compressed.add(image);
                } else {
                    BitmapFactory.Options options = ImageCompress.getBitmapOptions(image);
                    if (null != options && options.outHeight > options.outWidth * 1.5) {
                        // 如果图片的原始高度大于宽度的1.5倍，则可以判定为长图，不需要压缩
                        log("not need compress: " + image);
                        this.compressed.add(image);
                    } else {
                        String compressed = compressImage(image, to, toWidth, toHeight);
                        if (StringHelper.isEmpty(errors) && ret == 0) {
                            ret = 3;
                        }
                        // 压缩失败的图片也要加入列表，只是路径为空
                        log("compressed: " + image);
                        this.compressed.add(compressed);
                    }
                }
            } else {
                errors = "The image you wanna compress is not exist.";
                ret = 2;
                // 源文件不存在时，也加入一个空的路径，后续再预览的时候显示空白就行了
                compressed.add("");
            }
        }
        return ret;
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
        String compressedPath = "";
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
            compressCompleteListener.onComplete(compressed);
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
        void onComplete(ArrayList<String> compressed);
    }
}
