package com.gzlk.android.isp.task;

import com.hlk.hlklib.tasks.AsyncedTask;

/**
 * 作者：Hsiang Leekwok on 2015/08/31 09:52<br />
 * 邮箱：xiang.l.g@gmail.com<br />
 */
public abstract class AsyncExecutableTask<Params, Progress, Result> extends AsyncedTask<Params, Progress, Result> {

    @SuppressWarnings("unchecked")
    @Override
    protected Result doInBackground(Params... params) {
        try {
            return doInTask(params);
        } catch (Exception ignore) {
            ignore.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        doBeforeExecute();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Result result) {
        doAfterExecute();
        super.onPostExecute(result);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    @Override
    protected void onProgressUpdate(Progress... values) {
        doProgress(values);
        super.onProgressUpdate(values);
    }

    /**
     * Task执行前需要做的工作，如更新UI等
     */
    protected void doBeforeExecute() {
    }

    /**
     * Task执行过程中执行进度更新时要做的工作，如更新UI等
     */
    @SuppressWarnings({"unchecked", "varargs"})
    protected void doProgress(Progress... values) {
    }

    /**
     * Task的执行的方法，更改UI的方法不要放在这里
     */
    @SuppressWarnings({"unchecked", "varargs"})
    protected abstract Result doInTask(Params... params);

    /**
     * Task执行完毕之后需要做的工作，如更新UI等
     */
    protected void doAfterExecute() {
    }
}
