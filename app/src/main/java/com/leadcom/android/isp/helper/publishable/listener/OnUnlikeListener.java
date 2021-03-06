package com.leadcom.android.isp.helper.publishable.listener;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>取消赞接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 18:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public interface OnUnlikeListener {
    void onUnlike(boolean success, Model model);
}
