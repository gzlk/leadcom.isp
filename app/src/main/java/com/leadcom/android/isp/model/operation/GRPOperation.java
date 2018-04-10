package com.leadcom.android.isp.model.operation;

/**
 * <b>功能描述：</b>组织成员权限动作<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/11 07:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/11 07:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface GRPOperation {

    /* ***********************************************************
       组织架构相关权限
    *************************************************************/
    /**
     * 编辑群资料
     */
    String GROUP_PROPERTY = "ROLE_36f596321d4744969dbb02f020fb9e39";
    /**
     * 关联组织（关注或关联上级组织）
     */
    String GROUP_ASSOCIATION = "ROLE_79fdb066842a481a87abdf596bf9553c";

    /* ***********************************************************
       组织成员相关权限
    *************************************************************/
    /**
     * 添加成员
     */
    String MEMBER_ADD = "ROLE_6cf79d0502e544daa85c8e92f77d90e1";
    /**
     * 删除群成员
     */
    String MEMBER_DELETE = "ROLE_7ec770f217a04ee8bd782bcb279dddbb";
    /**
     * 修改成员角色
     */
    String MEMBER_ROLE = "ROLE_c2b23baf60144b14b40783bf9d9c411f";

    /* ***********************************************************
       档案管理相关
    *************************************************************/
    /**
     * 审批档案
     */
    String ARCHIVE_APPROVAL = "ROLE_cc6a8131b8d447a4accd85fa3208424c";
    /**
     * 编辑档案
     */
    String ARCHIVE_EDIT = "ROLE_fe9bb030bf9445a5bb3d73a2b919fef2";
    /**
     * 删除档案
     */
    String ARCHIVE_DELETE = "ROLE_9fc02c46d90d47a2a97f9894e17d403a";

    /* ***********************************************************
       组织小组相关
    *************************************************************/
    /**
     * 新增小组
     */
    String SQUAD_ADD = "ROLE_d6da8ab0d82d4f5f8693abcbe07b3e5d";
    /**
     * 删除小组
     */
    String SQUAD_DELETE = "ROLE_267e8027b94a425c970c2af1f97ff292";
    /**
     * 修改小组资料
     */
    String SQUAD_PROPERTY = "ROLE_b7fc590cba704218ac37a794d3d506c9";
    /**
     * 邀请成员加入小组
     */
    String SQUAD_MEMBER_INVITE = "ROLE_6cc2e134e87a49ab9204fe85500f0e09";
    /**
     * 删除小组成员
     */
    String SQUAD_MEMBER_DELETE = "ROLE_4d90a8ecec404d1280112fd907c6ae73";

    /* ***********************************************************
       档案相关
    *************************************************************/
    /**
     * 推荐档案
     */
    String ARCHIVE_RECOMMEND = "ROLE_1f62645fd86d44d483c9f6816bf5e639";
    /**
     * 转发档案
     */
    String ARCHIVE_FORWARD = "ROLE_65e81bfb1f5940daa3d86c99bcda79f3";
    /**
     * 分享档案
     */
    String ARCHIVE_SHARE = "ROLE_d10c0bff7cbf41f7a2247e4788996024";
}
