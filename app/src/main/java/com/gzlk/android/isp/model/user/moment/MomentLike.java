package com.gzlk.android.isp.model.user.moment;

import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>个人动态相关基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 23:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 23:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Moment.Table.LIKE)
public class MomentLike extends SubMoment {

    //姓名
//    @Column(Model.Field.UserName)
//    private String userName;
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
}
