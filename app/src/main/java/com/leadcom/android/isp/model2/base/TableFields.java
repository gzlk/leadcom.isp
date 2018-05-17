package com.leadcom.android.isp.model2.base;

/**
 * <b>功能描述：</b>表格字段相关<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/17 22:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TableFields {
    /**
     * 用户相关的字段名列表
     */
    public interface UserRelated {
        // 用户相关的字段
        String UserId = "userId";
        String UserName = "userName";
        String HeadPhoto = "headPhoto";
        String CreateDate = "createDate";
    }

    /**
     * 档案相关的字段名列表
     */
    public interface ArchiveRelated {
        String OwnType = "ownType";
        String ArchiveType = "archiveType";
        String ArchiveId = "archiveId";
        String Cover = "cover";
        String Title = "title";
        String AbstractContent = "abstractContent";
        String Addition = "addition";
    }
}
