package com.leadcom.android.isp.listener;

import java.util.List;

/**
 * <b>功能描述：</b>LiteOrmTask执行完毕的监听接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/10 22:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/10 22:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnLiteOrmTaskExecutedListener<E> {

    /**
     * Task执行完毕，主线程中调用
     */
    void onExecuted(boolean modified, List<E> result);
}
