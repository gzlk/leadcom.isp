package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>用户隐私设置<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 19:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 19:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(User.Table.PRIVACY)
public class Privacy extends Model {

    /**
     * 隐私类别
     */
    public static class Type {
        /**
         * 完全公开
         */
        public static final String Public = "1";
        /**
         * 完全私有
         */
        public static final String Private = "2";
        /**
         * 对部分人公开
         */
        public static final String Someone = "3";
    }

    //用户名
    @Column(Field.UserId)
    private String userId;
    //隐私设置类型  1.公开  2.不公开 3.对某些人公开
    @Column(Archive.Field.Type)
    private String type;
    //最后修改时间
    @Column(Archive.Field.LastModifiedDate)
    private String lastModifiedDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
