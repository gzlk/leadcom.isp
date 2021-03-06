package com.leadcom.android.isp.api.query;

import com.leadcom.android.isp.api.Api;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.user.SimpleMoment;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>网络请求返回的数据基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 19:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 19:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SingleQuery<T> extends Api<T> {

    protected T data;
    // 个人消息
    private int userInfoNum;
    private String lastHeadPhoto;
    /**
     * 用户登录后返回的该用户有关联的组织列表
     */
    private ArrayList<RelateGroup> userRelateGroupList;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getUserInfoNum() {
        return userInfoNum;
    }

    public void setUserInfoNum(int userInfoNum) {
        this.userInfoNum = userInfoNum;
    }

    public String getLastHeadPhoto() {
        return lastHeadPhoto;
    }

    public void setLastHeadPhoto(String lastHeadPhoto) {
        this.lastHeadPhoto = lastHeadPhoto;
    }

    public ArrayList<RelateGroup> getUserRelateGroupList() {
        return userRelateGroupList;
    }

    public void setUserRelateGroupList(ArrayList<RelateGroup> userRelateGroupList) {
        this.userRelateGroupList = userRelateGroupList;
    }
}
