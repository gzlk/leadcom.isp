package com.gzlk.android.isp.helper.publishable.listener;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Comment;

/**
 * <b>功能描述：</b>发布评论的处理接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 16:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 16:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnCommentAddListener {
    /**
     * 评论发布成功
     *
     * @param success 是否发布成功
     * @param comment 新发布的评论
     * @param model   发布成功之后返回宿主model
     */
    void onComplete(boolean success, Comment comment, Model model);
}
