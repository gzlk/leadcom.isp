package com.gzlk.android.isp.listener;

import com.gzlk.android.isp.holder.BaseViewHolder;

/**
 * <b>功能描述：</b>ViewHolder的数据处理接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/23 12:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/23 12:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnHandleBoundDataListener<T> {
    /**
     * 处理holder所在adapter中位置的绑定数据
     */
    T onHandlerBoundData(BaseViewHolder holder);
}
