package com.leadcom.android.isp.model.user;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
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
     * 隐私类型
     */
    public interface Status {
        /**
         * 完全公开
         */
        int PUBLIC = 1;
        /**
         * 私密
         */
        int PRIVATE = 2;
        /**
         * 对某人公开
         */
        int SOMEONE = 3;
        /**
         * 对某群公开
         */
        int GROUP = 4;
    }

    /**
     * 设置隐私的文档类型
     */
    public interface Source {
        /**
         * 个人资料
         */
        int DATA = 1;
        /**
         * 个人档案
         */
        int ARCHIVE = 2;
        /**
         * 个人日志
         */
        int MOMENT = 3;
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
