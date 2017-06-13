package com.gzlk.android.isp.model.organization;

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

public interface Operation {
    /**
     * 编辑群资料
     */
    String GROUP_PROPERTY = "ROLE_36f596321d4744969dbb02f020fb9e39";
    /**
     * 审批档案
     */
    String ARCHIVE_APPROVE = "ROLE_cc6a8131b8d447a4accd85fa3208424c";
    /**
     * 删除群成员
     */
    String MEMBER_DELETE = "ROLE_7ec770f217a04ee8bd782bcb279dddbb";
    /**
     * 修改成员角色
     */
    String MEMBER_ROLE = "ROLE_c2b23baf60144b14b40783bf9d9c411f";
    /**
     * 新增小组
     */
    String SQUAD_ADDABLE = "ROLE_d6da8ab0d82d4f5f8693abcbe07b3e5d";
    /**
     * 删除小组
     */
    String SQUAD_DELETE = "ROLE_267e8027b94a425c970c2af1f97ff292";
}
