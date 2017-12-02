package com.leadcom.android.isp.helper.publishable.listener;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>收藏的处理接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 21:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 21:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public interface OnCollectedListener {
    /**
     * 收藏处理回调
     */
    void onCollected(boolean success, Model model);
}
