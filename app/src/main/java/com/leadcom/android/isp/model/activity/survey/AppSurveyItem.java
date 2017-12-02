package com.leadcom.android.isp.model.activity.survey;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>问卷调查中的问卷项目<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/02 14:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/02 14:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppSurveyItem extends Model {

    @Column(Archive.Field.Title)
    private String title;

    @Column(Archive.Field.Type)
    private int type;

    @Column(Activity.Field.Sequence)
    private int seq;              //序号

    @Column(Field.CreateDate)
    private String createDate;

    @Column(Field.CreatorId)
    private String creatorId;

    @Column(Field.CreatorName)
    private String creatorName;
}
