package com.gzlk.android.isp.model.user.moment;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>个人动态附加信息的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 23:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 23:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SubMoment extends Model {

    public static class Field {
        // moment相关的公共字段名
        public static final String MomentId = "momentId";
    }

    //动态ID
    @Column(Field.MomentId)
    private String momentId;

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }
}
