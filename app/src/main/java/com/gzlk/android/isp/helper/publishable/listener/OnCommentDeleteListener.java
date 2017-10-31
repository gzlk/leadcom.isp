package com.gzlk.android.isp.helper.publishable.listener;

import com.gzlk.android.isp.model.Model;

/**
 * <b>功能描述：</b>评论删除处理接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 16:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 16:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnCommentDeleteListener {
    /**
     * 评论删除接口
     *
     * @param success 是否删除成功
     * @param model   删除成功之后的实例，可以是Archive或者Moment
     */
    void onDeleted(boolean success, Model model);
}
