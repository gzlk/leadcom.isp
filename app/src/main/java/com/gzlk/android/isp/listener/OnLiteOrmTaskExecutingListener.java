package com.gzlk.android.isp.listener;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.task.OrmTask;

import java.util.List;

/**
 * <b>功能描述：</b>LiteOrmTask执行过程<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2016/06/09 14:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2016/06/09 14:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnLiteOrmTaskExecutingListener<E extends Model> {

    /**
     * 标记执行过程是否修改数据
     */
    boolean isModifiable();

    /**
     * Task执行过程，不能在此回调中调用UI元素，否则会出错
     *
     * @param task 执行任务的task对象
     * @return modify为true时，返回可以为null，否则返回查询结果
     */
    List<E> executing(OrmTask<E> task);

}
