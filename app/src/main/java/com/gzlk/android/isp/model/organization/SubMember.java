package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.helper.StringHelper;

import java.io.Serializable;

/**
 * <b>功能描述：</b>只包含userId和userName两个属性的简单member对象<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/09 21:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/09 21:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SubMember implements Serializable {

    private String userId;
    private String userName;

    public SubMember() {
    }

    public SubMember(Member member) {
        userId = member.getUserId();
        userName = member.getUserName();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object object) {
        return null != object && getClass() == object.getClass() && equals((SubMember) object);
    }

    public boolean equals(SubMember member) {
        return null != member &&
                !StringHelper.isEmpty(getUserId()) &&
                !StringHelper.isEmpty(member.getUserId()) &&
                member.getUserId().equals(getUserId());
    }
}
