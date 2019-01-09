package com.leadcom.android.isp.apache.poi;

import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.application.BaseApplication;
import com.leadcom.android.isp.helper.LogHelper;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * <b>功能描述：</b>Apache POI 中文件相关方法集合<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/14 10:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/14 10:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FileUtils {

    private final static String TAG = "POIFileUtil";

    public static String getFileName(String pathAndName) {
        int start = pathAndName.lastIndexOf("/");
        int end = pathAndName.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathAndName.substring(start + 1, end);
        } else {
            return "";
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isFileExists(String fileName) {
        String dir_path = App.app().getCachePath(BaseApplication.HTML_DIR);
        String file_path = String.format("%s%s", dir_path, fileName);
        try {
            File myFile = new File(file_path);
            return myFile.exists() && myFile.length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public static String createFile(String fileName) {
        String dirPath = App.app().getCachePath(BaseApplication.HTML_DIR);
        String filePath = String.format("%s%s", dirPath, fileName);
        try {
            File myFile = new File(filePath);
            myFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static void writeFile(String content, String path) {
        //createHtmlDir(path);
        OutputStream os = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (os != null)
                    os.close();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static ZipEntry getPicEntry(ZipFile docxFile, int picIndex) {
        String entry_jpg = "word/media/image" + picIndex + ".jpeg";
        String entry_png = "word/media/image" + picIndex + ".png";
        String entry_gif = "word/media/image" + picIndex + ".gif";
        String entry_wmf = "word/media/image" + picIndex + ".wmf";
        ZipEntry pic_entry;
        pic_entry = docxFile.getEntry(entry_jpg);
        // 以下为读取docx的图片 转化为流数组
        if (pic_entry == null) {
            pic_entry = docxFile.getEntry(entry_png);
        }
        if (pic_entry == null) {
            pic_entry = docxFile.getEntry(entry_gif);
        }
        if (pic_entry == null) {
            pic_entry = docxFile.getEntry(entry_wmf);
        }
        return pic_entry;
    }

    public static byte[] getPictureBytes(ZipFile docxFile, ZipEntry picEntry) {
        byte[] pictureBytes = null;
        try {
            InputStream pictIS = docxFile.getInputStream(picEntry);
            ByteArrayOutputStream pOut = new ByteArrayOutputStream();
            byte[] b = new byte[1000];
            int len;
            while ((len = pictIS.read(b)) != -1) {
                pOut.write(b, 0, len);
            }
            pictIS.close();
            pOut.close();
            pictureBytes = pOut.toByteArray();
            LogHelper.log(TAG, "pictureBytes.length=" + pictureBytes.length);
            pictIS.close();
            pOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pictureBytes;

    }

    public static void writePicture(String picPath, byte[] pictureBytes) {
        File myPicture = new File(picPath);
        try {
            FileOutputStream outputPicture = new FileOutputStream(myPicture);
            outputPicture.write(pictureBytes);
            outputPicture.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 本地文件复制
     */
    public static boolean fileCopy(String oldFilePath, String newFilePath) throws IOException {
        //如果原文件不存在
        if (!fileExists(oldFilePath)) {
            return false;
        }
        //获得原文件流
        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
        byte[] data = new byte[4096];
        //输出流
        FileOutputStream outputStream = new FileOutputStream(new File(newFilePath));
        //开始处理流
        while (inputStream.read(data) != -1) {
            outputStream.write(data);
        }
        inputStream.close();
        outputStream.close();
        return true;
    }

    private static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 获取指定文件夹或文件的大小
     */
    public static long getFileSize(String path) {
        long size = 0;//如果原文件不存在
        if (!fileExists(path)) {
            return size;
        }
        File file = new File(path);
        try {
            if (file.isDirectory()) {
                size = getFileSizes(file);
            } else {
                size = file.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取指定文件夹大小
     */
    private static long getFileSizes(File f) {
        long size = 0;
        File list[] = f.listFiles();
        for (File file : list) {
            if (file.isDirectory()) {
                size = size + getFileSizes(file);
            } else {
                size = size + file.length();
            }
        }
        return size;
    }

    /**
     * 删除指定的文件或文件夹
     */
    public static void removeFile(String path) {
        removeFile(new File(path));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void removeFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                removeFile(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }
}
