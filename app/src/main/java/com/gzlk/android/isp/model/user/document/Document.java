package com.gzlk.android.isp.model.user.document;

import com.gzlk.android.isp.model.BaseArchive;
import com.litesuits.orm.db.annotation.Table;


/**
 * <b>功能描述：</b>个人档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 14:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 14:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Document.Table.DOCUMENT)
public class Document extends BaseArchive {

    static class Table {
        /**
         * 个人档案表
         */
        static final String DOCUMENT = "document";
        /**
         * 个人档案统计表
         */
        static final String ADDITIONAL = "documentAdditional";
        /**
         * 个人档案评论表
         */
        static final String COMMENT = "documentComment";
        /**
         * 个人档案点赞表
         */
        static final String LIKE = "documentLke";
    }

    public static class Field {
        public static final String Title = "title";
        public static final String Type = "type";
        public static final String Creator = "userId";
        public static final String CreatorName = "userName";
        public static final String LastModifiedDate = "lastModifiedDate";
    }

    /**
     * 个人档案类型
     */
    public static class Type {
        /**
         * 文本
         */
        public static final String TEXT = "1";
        /**
         * 连接引用
         */
        public static final String LINK = "2";
    }
}
