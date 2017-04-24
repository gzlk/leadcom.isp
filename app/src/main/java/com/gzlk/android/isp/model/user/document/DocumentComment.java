package com.gzlk.android.isp.model.user.document;

import com.gzlk.android.isp.model.user.moment.Moment;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>个人档案的评论<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 23:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 23:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocumentComment extends DocumentLike {

    //组织档案内容
    @Column(Moment.Field.Content)
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
