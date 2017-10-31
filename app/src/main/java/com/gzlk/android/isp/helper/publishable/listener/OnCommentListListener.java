package com.gzlk.android.isp.helper.publishable.listener;

import com.gzlk.android.isp.model.archive.Comment;

import java.util.List;

/**
 * <b>功能描述：</b>加载评论列表的处理接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 16:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 16:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnCommentListListener {
    /**
     * 评论列表
     */
    void onList(List<Comment> list);
}
