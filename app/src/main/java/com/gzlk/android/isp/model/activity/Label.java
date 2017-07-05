package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动的标签<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 22:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 22:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.LABEL)
public class Label extends Model {

    public static List<Label> getLabels(String activityId) {
        return new Dao<>(Label.class).query(Activity.Field.ActivityId, activityId);
    }

    public static List<Label> getLabels(List<String> ids) {
        if (null != ids && ids.size() > 0) {
            return new Dao<>(Label.class).in(Model.Field.Id, ids);
        }
        return new ArrayList<>();
    }

    public static Label getLabel(String labelId) {
        return new Dao<>(Label.class).query(labelId);
    }

    //名称
    @Column(Model.Field.Name)
    private String name;
    //组织id
    @Column(Organization.Field.GroupId)
    private String groupId;
    //活动id
    @Column(Activity.Field.ActivityId)
    private String actId;

    private int signaNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getSignaNum() {
        return signaNum;
    }

    public void setSignaNum(int signaNum) {
        this.signaNum = signaNum;
    }
}
