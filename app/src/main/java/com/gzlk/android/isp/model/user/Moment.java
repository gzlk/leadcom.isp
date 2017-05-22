package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.archive.Additional;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>个人动态<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 14:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 14:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.USER_MOMENT)
public class Moment extends Additional {

    //用户姓名
    @Column(Model.Field.UserId)
    private String userId;
    //姓名
    @Column(Model.Field.UserName)
    private String userName;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //创建地点
    @Column(Archive.Field.Location)
    private String location;
    //动态内容
    @Column(Archive.Field.Content)
    private String content;
    //档案附加信息
    @Ignore
    private Additional addition;
    //图片
    @Column(Archive.Field.Image)
    private ArrayList<String> image;

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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Additional getAddition() {
        return addition;
    }

    public void setAddition(Additional addition) {
        this.addition = addition;
        resetAdditional(this.addition);
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }
}
