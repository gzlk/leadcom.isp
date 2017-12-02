package com.leadcom.android.isp.model.operation;

/**
 * <b>功能描述：</b>活动相关操作权限<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/14 19:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/14 19:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface ACTOperation {
    /**
     * 修改活动属性
     */
    String PROPERTY_EDIT = "ROLE_5d94e5092e5b42f082c8c83b57190a70";
    /**
     * 结束活动
     */
    String CLOSEABLE = "ROLE_8829a528d36e47d2879abd1e68cd97db";
    /**
     * 删除活动
     */
    String DELETABLE = "ROLE_f58c1cbf79e44bf8ab87eb8a1738b6c5";
    /**
     * 查看活动
     */
    String CHECKABLE = "ROLE_c1d93cba7f4d4304b3db17a296e8f8a9";
    /**
     * 添加成员
     */
    String MEMBER_ADDABLE = "ROLE_44f0da4e0b014026980d62f65d7d4b5e";
    /**
     * 删除成员
     */
    String MEMBER_DELETABLE = "ROLE_4396e81ead65457a95531efd063b74a6";
}
