package com.leadcom.android.isp.listener;

/**
 * <b>功能描述：</b>LiteOrmTask 执行精度监听接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/10 22:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/10 22:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnTaskProcessingListener {

    /**
     * 执行进度，主线程中回调
     */
    void progressing(int percentage);

}
