package com.gzlk.android.isp.fragment.base;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTaskPreparedListener;
import com.gzlk.android.isp.task.CompressImageTask;
import com.netease.nim.uikit.NimUIKit;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.durban.Controller;
import com.yanzhenjie.durban.Durban;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <b>功能描述：</b>提供图片选择相关接口的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 22:25 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 22:25 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseImageSelectableSupportFragment extends BaseDownloadingUploadingSupportFragment {

    private static final String KEY_FOR_CROP = "is_for_crop";
    private static final String KEY_CROPPED_PATH = "cropped_path";
    private static final String KEY_CAMERA_PATH = "camera_path";
    private static final String KEY_CHOOSED_IMAGES = "choosed_images";
    private static final String KEY_DIRECT_COMPRESS = "is_compress_directly";
    /**
     * 通过系统相机拍摄的照片的路径
     */
    private String cameraPicturePath;
    /**
     * 裁剪图片后保存的地址
     */
    private String croppedImagePath;
    /**
     * 设置是否需要剪切图片，默认不需要剪切
     */
    protected boolean isChooseImageForCrop = false;
    /**
     * 裁剪图片的宽高比
     */
    protected int croppedAspectX = 1, croppedAspectY = 1;
    /**
     * 裁剪之后图片的宽高
     */
    protected int mCompressedImageWidth, mCompressedImageHeight;
    /*
      压缩后需要上传的文件
     */
    //private String compressed = null;

    /**
     * 图片文件是否支持压缩
     */
    protected boolean isSupportCompress = false;
    /**
     * 是否支持选择图片后直接压缩
     */
    protected boolean isCompressDirectly = false;

    /*
      最大可选择的图片数量
     */
    //protected int maxCachedImage = DFT_MAX_IMAGE;

    /**
     * 设置需要的图片的宽度尺寸
     */
    public void setCompressedImageWidth(int width) {
        mCompressedImageWidth = width;
    }

    /**
     * 设置需要的图片的高度尺寸
     */
    public void setCompressedImageHeight(int height) {
        mCompressedImageHeight = height;
    }

    private int chooseImageByAlbum = -1;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        croppedImagePath = bundle.getString(KEY_CROPPED_PATH);
        cameraPicturePath = bundle.getString(KEY_CAMERA_PATH);
        isChooseImageForCrop = bundle.getBoolean(KEY_FOR_CROP);
        isCompressDirectly = bundle.getBoolean(KEY_DIRECT_COMPRESS, false);
        String string = bundle.getString(KEY_CHOOSED_IMAGES, "[]");
        waitingFroCompressImages = Json.gson().fromJson(string, new TypeToken<List<String>>() {
        }.getType());
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(KEY_CROPPED_PATH, croppedImagePath);
        bundle.putString(KEY_CAMERA_PATH, cameraPicturePath);
        bundle.putBoolean(KEY_FOR_CROP, isChooseImageForCrop);
        bundle.putBoolean(KEY_DIRECT_COMPRESS, isCompressDirectly);
        bundle.putString(KEY_CHOOSED_IMAGES, Json.gson().toJson(waitingFroCompressImages));
    }

    @Override
    public void onStop() {
        super.onStop();
        //hideImageHandlingDialog();
    }

    /**
     * 等待压缩的图片列表
     */
    protected ArrayList<String> waitingFroCompressImages = new ArrayList<>();

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA:// 相机拍照返回了
            case REQUEST_GALLERY:// 相册选择返回了
            case REQUEST_PREVIEW:
                if (chooseImageByAlbum()) {
                    ArrayList<String> images = Album.parseResult(data);
                    waitingFroCompressImages.clear();
                    waitingFroCompressImages.addAll(images);
                    if (isChooseImageForCrop) {
                        // 如果需要剪切图片
                        if (waitingFroCompressImages.size() < 1)
                            throw new IllegalArgumentException("You need select an image first.");
                        // 第一张图片用来裁剪
                        cameraPicturePath = waitingFroCompressImages.get(0);
                        prepareForCrop();
                    } else {
                        // 图片选择了
                        onImageSelected();
                    }
                } else {
                    if (null != data) {
                        // 相机照相之后返回的有可能是相册的路径，此时需要获取相册路径
                        cameraPicturePath = getGalleryResultedPath(data);
                    }
                    if (isChooseImageForCrop) {
                        prepareForCrop();
                    } else {
                        // 照片已选择了
                        waitingFroCompressImages.clear();
                        waitingFroCompressImages.add(cameraPicturePath);
                        onImageSelected();
                    }
                }
                break;
            case REQUEST_CROP:
                if (chooseImageByAlbum()) {
                    croppedImagePath = Durban.parseResult(data).get(0);
                }
                if (!TextUtils.isEmpty(croppedImagePath)) {
                    // 裁剪后的图片也需要压缩
                    waitingFroCompressImages.clear();
                    waitingFroCompressImages.add(croppedImagePath);
                    onImageSelected();
                } else {
                    // 操作无法继续：数据丢失
                    SimpleDialogHelper.init(Activity()).show(R.string.ui_base_text_invalid_crop_path);
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void prepareForCrop() {
        prepareCroppedImagePath();
        if (TextUtils.isEmpty(cameraPicturePath)) {
            ToastHelper.make(Activity()).showMsg(R.string.ui_base_text_invalid_camera_path);
        } else {
            adjustWannaToImageSize();

            if (chooseImageByAlbum()) {
                cropImageBy3rdPart(cameraPicturePath, REQUEST_CROP, croppedAspectX, croppedAspectY, mCompressedImageWidth, mCompressedImageHeight);
            } else {
                cropImageUri(getUriFromFile(cameraPicturePath), getUriFromFile(croppedImagePath), REQUEST_CROP,
                        croppedAspectX, croppedAspectY, mCompressedImageWidth, mCompressedImageHeight);
            }
        }
    }

    // 处理Android 7.0+的Uri问题
    private Uri getUriFromFile(String filePath) {
        return NimUIKit.getUriFromFile(Activity(), filePath);
    }

    /**
     * 准备剪切之后的照片存储路径
     */
    private void prepareCroppedImagePath() {
        if (null != croppedImagePath) {
            // 删除之前剪切过的图片
            deleteFile(croppedImagePath);
        }
        croppedImagePath = Activity().app().getLocalCroppedDir() + getTempFileName();
    }

    private void adjustWannaToImageSize() {
        if (0 == mCompressedImageHeight) {
            mCompressedImageHeight = getScreenHeight();
        }
        if (0 == mCompressedImageWidth) {
            mCompressedImageWidth = getScreenWidth();
        }
        if (isChooseImageForCrop) {
            // 需要裁剪图片时，重新定义宽高尺寸
            if (mCompressedImageWidth > mCompressedImageHeight) {
                mCompressedImageWidth = mCompressedImageHeight;
            }
            mCompressedImageHeight = (int) (croppedAspectY * 1.0 / croppedAspectX * 1.0 * mCompressedImageWidth);
        }
    }

    // 重置已选择了的文件列表
    private void resetSelectedImage() {
        for (String string : waitingFroCompressImages) {
            if (!getWaitingForUploadFiles().contains(string)) {
                getWaitingForUploadFiles().add(string);
            }
        }
    }

    private void onImageSelected() {
        if (!isSupportCompress) {
            // 如果不支持压缩则把选择了的图片放入待上传队列
            //getWaitingForUploadFiles().clear();
            //getWaitingForUploadFiles().addAll(waitingFroCompressImages);
            resetSelectedImage();
        }
        // 图片选择事件处理
        if (null != mOnImageSelectedListener) {
            mOnImageSelectedListener.onImageSelected(isSupportCompress ? waitingFroCompressImages : getWaitingForUploadFiles());
        } else {
            log("no image selected callback exists.");
        }
        // 是否支持选择图片后直接压缩
        if (isCompressDirectly) {
            // 直接压缩
            compressImage();
        }
    }

    private void onImageCompressed() {
        if (null != mOnImageCompressedListener) {
            mOnImageCompressedListener.onCompressed(getWaitingForUploadFiles());
        } else {
            log("no image compressed callback exists.");
        }
    }

    /**
     * 压缩图片
     */
    protected void compressImage() {
        if (isSupportCompress) {
            adjustWannaToImageSize();
            if (waitingFroCompressImages.size() < 1) {
                throw new IllegalArgumentException("no image(s) is waiting for compress");
            } else {
                new CompressImageTask()
                        .setDebuggable(true)
                        .addOnTaskPreparedListener(taskPreparedListener)
                        .addOnCompressCompleteListener(compressCompleteListener)
                        .exec(Json.gson().toJson(waitingFroCompressImages),
                                Activity().app().getLocalImageDir(),
                                String.valueOf(mCompressedImageWidth),
                                String.valueOf(mCompressedImageHeight));
            }
        } else {
            if (isSupportDirectlyUpload) {
                uploadFiles();
            } else {
                onImageCompressed();
            }
        }
    }

    private OnTaskPreparedListener taskPreparedListener = new OnTaskPreparedListener() {
        @Override
        public void onPrepared() {
            showImageHandlingDialog(R.string.ui_base_text_compressing);
        }
    };

    private CompressImageTask.OnCompressCompleteListener compressCompleteListener = new CompressImageTask.OnCompressCompleteListener() {
        @Override
        public void onComplete(ArrayList<String> compressedPath) {
            getWaitingForUploadFiles().clear();
            getWaitingForUploadFiles().addAll(compressedPath);
            onImageCompressed();
            if (isSupportDirectlyUpload) {
                showImageHandlingDialog(R.string.ui_base_text_uploading);
                uploadFiles();
            } else {
                ToastHelper.make().showMsg("压缩完了，暂时没有下一步任务");
                hideImageHandlingDialog();
            }
        }
    };

    /**
     * 删除指定的文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private synchronized void deleteFile(String path) {
        File file = new File(path);
        if (file.exists())
            file.delete();
    }

    /**
     * 通过相机拍摄的照片名称前缀
     */
    private static final String CAMERA_PRE = "LK_CACHE_IMAGE_";
    /**
     * 通过剪切拿到的照片前缀
     */
    private static final String CROP_PRE = "LK_CACHE_CROP_";

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    /**
     * 获取随机的文件名，后缀.jpg
     */
    public String getTempFileName() {
        return timeFormat.format(new Date()) + ".jpg";
    }

    /**
     * 通过第三方控件剪切照片
     */
    @SuppressWarnings("ConstantConditions")
    private void cropImageBy3rdPart(String imagePath, int requestCode, int aspectX, int aspectY, int outputWidth, int outputHeight) {
        Durban.with(this)
                // Che title of the UI.
                .title(getString(R.string.ui_base_text_crop_image))
                .statusBarColor(getColor(R.color.colorPrimary))
                .toolBarColor(getColor(R.color.colorPrimary))
                //.navigationBarColor(getColor(R.color.colorPrimary))
                // Image path list/array.
                .inputImagePaths(imagePath)
                // Image output directory.
                .outputDirectory(App.app().getCachePath(App.CROPPED_DIR))
                // Image size limit.
                .maxWidthHeight(outputWidth, outputHeight)
                // Aspect ratio.
                .aspectRatio(aspectX, aspectY)
                // Output format: JPEG, PNG.
                .compressFormat(Durban.COMPRESS_JPEG)
                // Compress quality, see Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)
                .compressQuality(100)
                // Gesture: ROTATE, SCALE, ALL, NONE.
                .gesture(Durban.GESTURE_SCALE)
                .controller(Controller.newBuilder() // Create Builder of Controller.
                        .enable(true) // Enable the control panel.
                        .rotation(true) // Rotation button.
                        .rotationTitle(true) // Rotation button title.
                        .scale(true) // Scale button.
                        .scaleTitle(true) // Scale button title.
                        .build()) // Create Controller Config.
                .requestCode(requestCode)
                .start();
    }

    /**
     * 启动裁剪图片界面
     *
     * @param sourceUri    被裁剪的原始文件uri
     * @param destUri      裁剪后文件保存的Uri
     * @param requestCode  Activity 的需求码
     * @param aspectX      裁剪宽度比例
     * @param aspectY      裁剪高度比例
     * @param outputWidth  输出宽度
     * @param outputHeight 输出高度
     */
    protected void cropImageUri(Uri sourceUri, Uri destUri, int requestCode, int aspectX, int aspectY, int outputWidth, int outputHeight) {
        if (null == sourceUri || null == destUri) {
            throw new IllegalArgumentException("cannot crop image with null source/dest. sourceUri: " + sourceUri + ", destUri: " + destUri);
        }

        if (outputWidth <= 0 || outputHeight <= 0) {
            throw new IllegalArgumentException(format("cannot crop image to size %dwx%dh.", outputWidth, outputHeight));
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= 24) {
            //Android N need set permission to uri otherwise system
            //camera don't has permission to access file wait crop
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        // 设置裁剪方式为image
        intent.setDataAndType(sourceUri, "image/*");
        // 设置为裁剪动作
        intent.putExtra("crop", "true");
        // 裁剪宽高比
        intent.putExtra("aspectX", aspectX);
        // 裁剪宽高比
        intent.putExtra("aspectY", aspectY);
        // 输出图片宽度，单位像素
        intent.putExtra("outputX", outputWidth);
        // 输出图片高度，单位像素
        intent.putExtra("outputY", outputHeight);
        // 是否支持裁剪时缩放图片
        intent.putExtra("scale", true);
        // 设置裁剪后文件保存的Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);
        // 是否直接返回数据
        intent.putExtra("return-data", false);
        // 输出图片的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 设置不需要人脸检测
        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, requestCode);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri           The Uri to where.
     * @param selection     (Optional) Filter used in the where.
     * @param selectionArgs (Optional) Selection arguments used in the where.
     * @return The value of the _data column, which is typically a file path.
     */
    private String getDataFromProvider(Uri uri, String selection, String[] selectionArgs) {
        String ret = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = Activity().getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (null != cursor) {
            try {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                ret = cursor.getString(column_index);
            } finally {
                cursor.close();
            }
        }
        //log(format("uri: %s, selection: %s, args: %s, ret: %s", uri, selection, selectionArgs, ret));
        return ret;
    }

    /**
     * 获取从相册返回的数据中的图片地址
     */
    protected String getGalleryResultedPath(@NonNull Intent data) {

        Uri uri = data.getData();
        return getFilePathFromUri(uri);
    }

    /**
     * 通过Uri获取文件的路径
     */
    protected String getFilePathFromUri(Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(Activity(), uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataFromProvider(contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataFromProvider(contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataFromProvider(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        // null
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 是否使用第三方相册功能
     */
    private boolean chooseImageByAlbum() {
        if (chooseImageByAlbum < 0) {
            chooseImageByAlbum = StringHelper.getInteger(R.integer.integer_choose_image_by_album);
        }
        return chooseImageByAlbum > 0;
    }

    /**
     * 打开系统相册选取图片
     */
    protected void startGalleryForResult() {
        if (chooseImageByAlbum()) {
            if (isChooseImageForCrop) {
                // 剪切照片时只能选择1张
                maxSelectable = 1;
            }
            Album.album(this).requestCode(REQUEST_GALLERY)
                    .toolBarColor(getColor(R.color.colorPrimary))
                    .statusBarColor(getColor(R.color.colorPrimary))
                    .title(getString(R.string.ui_base_text_choose_image, getMaxSelectable()))
                    .checkedList(isSupportCompress ? waitingFroCompressImages : getWaitingForUploadFiles())
                    .selectCount(getMaxSelectable()).columnCount(3).camera(true).start();
        } else {
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(imageIntent, REQUEST_GALLERY);
        }
    }

    /**
     * 打开相册预览已选择了的照片列表
     */
    protected void startGalleryPreview(int position) {
        if (chooseImageByAlbum()) {
            Album.gallery(this).checkFunction(true)
                    .requestCode(REQUEST_PREVIEW)
                    .checkedList(isSupportCompress ? waitingFroCompressImages : getWaitingForUploadFiles())
                    .toolBarColor(getColor(R.color.colorPrimary))
                    .statusBarColor(getColor(R.color.colorPrimary))
                    .currentPosition(position)
                    .start();
        }
    }

    /**
     * 设置相机拍摄之后的照片保存在系统默认相册里
     */
    private File createImageFile() {
        // Create an image file name
        String timestamp = timeFormat.format(new Date());
        String imageFileName = format("%s.jpg", timestamp);
        // 拍摄的照片保存在用户自己的相册文件夹中
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(storageDir, imageFileName);
    }

    /**
     * 打开系统相机拍照并获取返回的数据
     */
    protected void startCameraForResult() {
        if (chooseImageByAlbum()) {
            Album.camera(this).requestCode(REQUEST_CAMERA).start();
        } else {
            if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                tryGrantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, GRANT_STORAGE, StringHelper.getString(R.string.ui_text_permission_storage_request), StringHelper.getString(R.string.ui_text_permission_storage_denied));
            } else {
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    if (!hasPermission(Manifest.permission.CAMERA)) {
                        tryGrantPermission(Manifest.permission.CAMERA, GRANT_CAMERA, StringHelper.getString(R.string.ui_text_permission_camera_request), StringHelper.getString(R.string.ui_text_permission_camera_denied));
                    } else {
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        File picture = createImageFile();
                        if (null != picture) {
                            cameraPicturePath = picture.getAbsolutePath();
                            // 存储相机照片的预定路径
                            Uri uri = NimUIKit.getUriFromFile(Activity(), cameraPicturePath);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else {
                            ToastHelper.make(Activity()).showMsg(R.string.ui_base_text_cannot_create_image_in_dcim);
                        }
                    }
                } else {
                    // 没有SD卡
                    ToastHelper.make(Activity()).showMsg(R.string.ui_base_text_no_sdcard_exists);
                }
            }
        }
    }

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_CAMERA && permissions[0].equals(Manifest.permission.CAMERA)) {
            // 授权成功，继续打开照相机
            startCameraForResult();
        } else if (requestCode == GRANT_STORAGE && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // sd卡读写权限授权完毕，继续打开相机
            startCameraForResult();
        }
    }

    private View imageSelector;

    private DialogHelper dialogHelper;

    public void clearCachedImages() {
        waitingFroCompressImages.clear();
    }

    public void openImageSelector(boolean clearFirst) {
        if (clearFirst) {
            waitingFroCompressImages.clear();
        }
        openImageSelector();
    }

    /**
     * 打开图片选择菜单
     */
    public void openImageSelector() {
        int size = isSupportCompress ? waitingFroCompressImages.size() : getWaitingForUploadFiles().size();
        // 裁剪照片时，可以多次选择裁剪
        if (size > 1 && size >= getMaxSelectable()) {
            ToastHelper.make(Activity()).showMsg(R.string.ui_base_text_image_cannot_attach_more);
            return;
        }
        dialogHelper = DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == imageSelector) {
                    imageSelector = View.inflate(Activity(), R.layout.popup_dialog_image_selector, null);
                }
                return imageSelector;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_from_camera, R.id.ui_dialog_button_from_gallery};
            }

            @Override
            public boolean onClick(View view) {
                handleUserRequest(view.getId());
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true);
        dialogHelper.show();
    }

    private void handleUserRequest(final int id) {
        dialogHelper.dismiss();
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case R.id.ui_dialog_button_from_camera:
                        startCameraForResult();
                        break;
                    case R.id.ui_dialog_button_from_gallery:
                        startGalleryForResult();
                        break;
                }
            }
        }, duration());
    }

    private OnImageSelectedListener mOnImageSelectedListener;

    /**
     * 添加照片选择处理回调
     */
    protected void addOnImageSelectedListener(OnImageSelectedListener l) {
        mOnImageSelectedListener = l;
    }

    /**
     * 照片选择之后的处理接口
     */
    protected interface OnImageSelectedListener {
        /**
         * 照片选择之后的回调接口
         *
         * @param selected 选择完毕之后的图片路径列表
         */
        void onImageSelected(ArrayList<String> selected);
    }

    private OnImageCompressedListener mOnImageCompressedListener;

    /**
     * 设置图片压缩完毕后的回调
     */
    protected void setOnImageCompressedListener(OnImageCompressedListener l) {
        mOnImageCompressedListener = l;
    }

    protected interface OnImageCompressedListener {
        void onCompressed(ArrayList<String> compressed);
    }
}
