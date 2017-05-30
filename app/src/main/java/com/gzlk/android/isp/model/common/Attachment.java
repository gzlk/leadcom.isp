package com.gzlk.android.isp.model.common;

import com.gzlk.android.isp.etc.ImageCompress;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.Locale;

/**
 * <b>功能描述：</b>文件附件<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/28 11:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/28 11:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Attachment.Table.ATTACHMENT)
public class Attachment extends Model {

    public interface Table {
        String ATTACHMENT = "attachment";
    }

    public interface Field {
        String ArchiveId = "archiveId";
        String Ext = "ext";
        String Url = "url";
        String Pdf = "pdf";
        String FullPath = "fullPath";
    }

    /**
     * 附件类型
     */
    public interface Type {
        /**
         * 档案的附件
         */
        int ARCHIVE = 1;
        /**
         * 说说的附件
         */
        int MOMENT = 2;
    }

    public static boolean isOffice(String ext) {
        if (StringHelper.isEmpty(ext)) return false;
        switch (ext) {
            case "doc":
            case "docx":
            case "xls":
            case "xlsx":
            case "ppt":
            case "pptx":
                return true;
        }
        return false;
    }

    public static String getFileName(String path) {
        if (StringHelper.isEmpty(path)) return null;
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static String getExtension(String path) {
        if (StringHelper.isEmpty(path)) return null;
        return path.substring(path.lastIndexOf('.') + 1).toLowerCase(Locale.getDefault());
    }

    @Column(Archive.Field.Type)
    private int type;
    @Column(Field.ArchiveId)
    private String archiveId;
    @Column(Model.Field.Name)
    private String name;
    @Column(Field.FullPath)
    private String fullPath;
    @Column(Field.Ext)
    private String ext;
    @Column(Field.Url)
    private String url;
    @Column(Field.Pdf)
    private String pdf;

    public Attachment() {
    }

    /**
     * 用本地文件路径生成一个附件对象
     */
    public Attachment(String localPath) {
        if (StringHelper.isEmpty(localPath) || localPath.charAt(0) != '/') {
            throw new IllegalArgumentException("Please set a true local file path.");
        }
        fullPath = localPath;
        type = Type.ARCHIVE;
        name = getFileName(localPath);
        ext = getExtension(localPath);
        url = name;
        setId(url);
    }

    public Attachment(int type, String archiveId, String name, String url, String pdf) {
        super();
        this.type = type;
        this.archiveId = archiveId;
        this.name = getFileName(name);
        this.ext = getExtension(name);
        this.url = url;
        this.fullPath = url;
        this.pdf = pdf;
        setId(this.url);
    }

    /**
     * 是否是本地文件
     */
    public boolean isLocalFile() {
        return !StringHelper.isEmpty(fullPath) && fullPath.charAt(0) == '/';
    }

    public boolean isOffice() {
        return isOffice(ext);
    }

    public boolean isImage() {
        return ImageCompress.isImage(ext);
    }

    public boolean isVideo() {
        return ImageCompress.isVideo(ext);
    }

    public void resetInformation() {
        setId(url);
        fullPath = url;
        ext = getExtension(url);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        ext = getExtension(name);
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        ext = getExtension(url);
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
