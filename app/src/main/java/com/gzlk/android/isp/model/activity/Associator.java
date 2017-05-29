package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.common.Leaguer;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>活动成员<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 22:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 22:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Activity.Table.ASSOCIATOR)
public class Associator extends Leaguer {

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }
}
