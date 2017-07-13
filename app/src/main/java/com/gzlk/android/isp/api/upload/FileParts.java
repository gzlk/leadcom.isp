package com.gzlk.android.isp.api.upload;

import com.litesuits.http.request.content.multi.FilePart;
import com.litesuits.http.utils.StringCodingUtils;

import java.io.File;
import java.nio.charset.Charset;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/11 16:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/11 16:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FileParts extends FilePart {

    public FileParts(String key, File file) {
        this(key, file, "application/octet-stream");
    }

    public FileParts(String key, File file, String mimeType) {
        super(key, file, mimeType);
    }

    @Override
    protected byte[] createContentType() {
        return StringCodingUtils.getBytes("Content-Type: " + this.mimeType + "\r\n", Charset.forName("utf-8"));
    }

    @Override
    protected byte[] createContentDisposition() {
        String dis = "Content-Disposition: form-data; name=\"" + this.key;
        return StringCodingUtils.getBytes(dis + "\"; filename=\"" + (this.file.getName()) + "\"\r\n", Charset.forName("utf-8"));
    }
}
