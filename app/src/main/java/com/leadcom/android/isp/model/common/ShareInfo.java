package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>要分享的内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/07 10:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/12/07 10:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 * <p>
 * {
 * "docType": "2",
 * "imageUrl": "http://120.25.124.199:8008/group1/M00/00/1C/cErYIVoBXcyAQrBmAADrc3U_v_U659.jpg",
 * "description": "<p>新华社北京11月7日电中央宣传部（国务院新闻办公室）会同中央文献研究室、中国外文局编辑的《习近平谈治国理政》第二卷，近日由外文出版社以中英文版出版，面向海内外发行。党的十八大以来，以习近平同志为核心的党中央团结带领全党全国各族人民，推动党和国家事业取得历史性成就、发生历史性变革，中国特色社会主义进入新时代。在治国理政新的实践中，以习近平总书记为主要代表的中国共产党人，顺应时代发展，从理论和实践结合</p>",
 * "targetPath": "http://113.108.144.2:8038/shareFile.html?id=5a0b9b3935241315f8b5dd60&docType=2",
 * "id": "5a0b9b3935241315f8b5dd60",
 * "title": "《习近平谈治国理政》",
 * "contentType": "1"
 * }
 * </p>
 */
public class ShareInfo extends Model {

    private String title;
    private int contentType;
    private int docType;
    private String imageUrl;
    private String description;
    private String targetPath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getDocType() {
        return docType;
    }

    public void setDocType(int docType) {
        this.docType = docType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
}
