package com.leadcom.android.isp.listener;

import android.view.View;

/**
 * <b>功能描述：</b>ViewHolder中的元素点击事件<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/18 10:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/18 10:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnViewHolderElementClickListener {
    /**
     * 发生点击事件的元素以及当前ViewHolder所在的索引
     *
     * @param view  被点击的view
     * @param index ViewHolder的索引
     */
    void onClick(View view, int index);
}
