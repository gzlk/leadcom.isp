package com.leadcom.android.isp.task;

import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.listener.OnLiteOrmTaskExecutingListener;
import com.leadcom.android.isp.listener.OnLiteOrmTaskExecutedListener;
import com.leadcom.android.isp.listener.OnTaskProcessingListener;
import com.leadcom.android.isp.listener.OnTaskPreparedListener;
import com.leadcom.android.isp.model.Model;
import com.hlk.hlklib.tasks.AsyncedTask;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * <b>功能</b>：提供LiteOrm数据存取的线程<br />
 * <b>作者</b>：Hsiang Leekwok <br />
 * <b>时间</b>：2016/06/09 14:15 <br />
 * <b>邮箱</b>：xiang.l.g@gmail.com <br />
 */
public class OrmTask<E extends Model> extends AsyncedTask<Void, Integer, Void> {

    /**
     * 当前表
     */
    private Class<E> classTable;

    private List<E> list;

    private boolean modify = false;

    public OrmTask() {
        handleParameterType();
    }

    /**
     * 获取参数列表中的类
     */
    @SuppressWarnings("unchecked")
    private void handleParameterType() {
        classTable = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    protected void log(String string) {
        LogHelper.log(this.getClass().getSimpleName(), string, true);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (null != preparedListener) {
            preparedListener.onPrepared();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (null != executingListener) {
            modify = executingListener.isModifiable();
            list = executingListener.executing(this);
        }
        return null;
    }

    /**
     * 外部更改task的执行进度
     */
    public void progressing(int percentage) {
        publishProgress(percentage);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (null != progressingListener) {
            progressingListener.progressing(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if (null != executedListener) {
            executedListener.onExecuted(modify, list);
        }
        super.onPostExecute(result);
    }

    private OnLiteOrmTaskExecutingListener<E> executingListener;

    /**
     * 添加task执行过程
     */
    public OrmTask<E> addOnLiteOrmTaskExecutingListener(OnLiteOrmTaskExecutingListener<E> l) {
        executingListener = l;
        return this;
    }

    private OnTaskPreparedListener preparedListener;

    /**
     * 添加准备执行时的监听
     */
    public OrmTask<E> addOnTaskPreparedListener(OnTaskPreparedListener l) {
        preparedListener = l;
        return this;
    }

    private OnTaskProcessingListener progressingListener;

    /**
     * 添加执行进度监听
     */
    public OrmTask<E> addOnLiteOrmTaskProgressingListener(OnTaskProcessingListener l) {
        progressingListener = l;
        return this;
    }

    private OnLiteOrmTaskExecutedListener<E> executedListener;

    /**
     * 添加task执行完毕时的监听
     */
    public OrmTask<E> addOnLiteOrmTaskExecutedListener(OnLiteOrmTaskExecutedListener<E> l) {
        executedListener = l;
        return this;
    }
}
