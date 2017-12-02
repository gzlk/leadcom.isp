package com.leadcom.android.isp.helper.publishable.listener;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>点赞接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 18:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 18:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public interface OnLikeListener {
    /**
     * 点赞结果处理回调
     *
     * @param success api是否处理成功
     * @param model   被赞的实体，一般为Archive或Moment
     */
    void onLiked(boolean success, Model model);
}
