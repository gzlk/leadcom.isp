package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.model.common.Leaguer;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>组织内成员<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.MEMBER)
public class Member extends Leaguer {

    /**
     * 成员类别
     */
    public interface Type {
        /**
         * 组织成员
         */
        int GROUP = 1;
        /**
         * 小组成员
         */
        int SQUAD = 2;
    }

    @Column(Organization.Field.GroupId)
    private String groupId;        //群体ID

    @Column(Organization.Field.SquadId)
    private String squadId;        //小组ID

    @Column(User.Field.Phone)
    private String phone;          //用户手机

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSquadId() {
        return squadId;
    }

    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
