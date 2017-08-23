package com.gzlk.android.isp.model.user;

/**
 * <b>功能描述：</b>用户的附加属性<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/19 12:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/19 12:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserExtra {

    private String title;   //标题
    private String content; //内容
    private int show;    //是否隐藏(0.隐藏,1.显示)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }
}
