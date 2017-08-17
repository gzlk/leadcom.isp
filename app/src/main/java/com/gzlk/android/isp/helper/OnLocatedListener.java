package com.gzlk.android.isp.helper;

import com.gzlk.android.isp.model.common.HLKLocation;

/**
 * <b>功能描述：</b>定位成功的回调<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/16 23:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/16 23:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnLocatedListener {

    /**
     * 定位成功的回调处理过程
     *
     * @param success  定位是否成功
     * @param location 定位的详细信息
     */
    void onLocated(boolean success, HLKLocation location);
}
