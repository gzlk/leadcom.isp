package com.leadcom.android.isp.model2.archive;

import com.leadcom.android.isp.model.archive.Additional;
import com.leadcom.android.isp.model2.base.TableFields;
import com.leadcom.android.isp.model2.base.TableNames;
import com.leadcom.android.isp.model2.base.UserRelated;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;


/**
 * <b>功能描述：</b>首页推荐档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/17 21:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(TableNames.RecommendArchive)
public class RecmdArchive extends UserRelated {

    // 档案归属类型：1组织档案，2个人档案
    @Column(TableFields.ArchiveRelated.OwnType)
    private String ownType;
    // 档案类型：1图文2附件3模板档案
    @Column(TableFields.ArchiveRelated.ArchiveType)
    private String docType;
    // 档案ID
    @Column(TableFields.ArchiveRelated.ArchiveId)
    private String docId;
    // 档案封面
    @Column(TableFields.ArchiveRelated.Cover)
    private String cover;
    // 档案标题
    @Column(TableFields.ArchiveRelated.Title)
    private String title;
    // 档案摘要
    @Column(TableFields.ArchiveRelated.AbstractContent)
    private String abstrContent;

    // 附加信息(点赞数,浏览数,收藏数,评论数)
    @Column(TableFields.ArchiveRelated.Addition)
    private Additional addition;
}
