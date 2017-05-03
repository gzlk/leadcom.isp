package com.gzlk.android.isp.model.user.moment;

import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
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

@Table(Moment.Table.MOMENT)
public class Moment extends Model {

    static class Table {
        /**
         * 个人说说
         */
        static final String MOMENT = "moment";
        /**
         * 说说统计信息表
         */
        static final String ADDITIONAL = "momentAdditional";
        /**
         * 说说评论表
         */
        static final String COMMENT = "momentComment";
        /**
         * 说说点赞表
         */
        static final String LIKE = "momentLike";
    }

    public static class Field {
        public static final String Location = "location";
        public static final String Content = "content";
        public static final String Image = "image";
    }

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
    @Column(Field.Location)
    private String location;
    //动态内容
    @Column(Field.Content)
    private String content;
    //图片
    @Column(Field.Image)
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

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return StringHelper.format("[id=%s,createDate=%s]", getId(), createDate);
    }
}
