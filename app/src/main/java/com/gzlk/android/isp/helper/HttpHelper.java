package com.gzlk.android.isp.helper;

import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.FileRequest;
import com.litesuits.http.response.Response;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <b>功能描述：</b>Http上传下载请求的helper<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/21 18:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/21 18:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HttpHelper {

    private static HttpHelper helper;

    public static HttpHelper helper() {
        if (null == helper) {
            helper = new HttpHelper();
        }
        return helper;
    }

    private HttpHelper() {

    }

    // 要上传或下载的文件集合
    private ArrayList<String> tasks = new ArrayList<>();

    public HttpHelper clearTask() {
        tasks.clear();
        handlingIndex = -1;
        handlingTask = "";
        return this;
    }

    /**
     * 下载一个文件到默认目录
     */
    public HttpHelper addTask(String httpUrl) {
        tasks.add(httpUrl);
        return this;
    }

    /**
     * 增加一个任务集合，且都下载到默认目录
     */
    public HttpHelper addTasks(String[] httpUrls) {
        if (null != httpUrls && httpUrls.length > 0) {
            tasks.addAll(Arrays.asList(httpUrls));
        }
        return this;
    }

    /**
     * 增加一个任务集合，且都下载到默认目录
     */
    public HttpHelper addTasks(ArrayList<String> httpUrls) {
        if (null != httpUrls && httpUrls.size() > 0) {
            tasks.addAll(httpUrls);
        }
        return this;
    }

    /**
     * 停止当前所有动作
     */
    public void cancel() {
        canceled = true;
    }

    private boolean canceled = false;


    public void upload() {
    }

    /**
     * 重新开始下载或从上次失败、取消的文件开始下载
     */
    public void download() {
        if (handlingIndex < 0) {
            // 重新开始下载
            handlingIndex = 0;
        }
        downloading();
    }

    /**
     * 默认下载的目录为图片文件夹
     */
    private String tempDir = App.IMAGE_DIR;

    @SuppressWarnings("ConstantConditions")
    public HttpHelper setLocalDirectory(String dir) {
        tempDir = dir;
        return this;
    }

    private boolean stopWhenFailure = false;

    /**
     * 设置是否在出错的时候停止并清空任务列表
     */
    public HttpHelper setStopWhenFailure(boolean stoppable) {
        stopWhenFailure = stoppable;
        return this;
    }

    private boolean ignoreExist = false;

    /**
     * 设置是否忽略已存在的文件，如果存在则会覆盖掉旧的文件
     *
     * @param ignore true=忽略旧文件，也即不下载已经存在了的文件；反之覆盖掉原有的文件
     */
    public HttpHelper setIgnoreExist(boolean ignore) {
        ignoreExist = ignore;
        return this;
    }

    /**
     * 当前正在处理的索引
     */
    private int handlingIndex = -1;
    /**
     * 当前正在处理的任务地址
     */
    private String handlingTask = "";

    @SuppressWarnings("ConstantConditions")
    private void downloading() {
        if (canceled) {
            notifyCancel();
            return;
        }
        int size = tasks.size();
        if (size > 0 && handlingIndex >= 0 && handlingIndex < size) {
            String url = tasks.get(handlingIndex);
            String local = getLocalFilePath(url, tempDir);
            File file = new File(local);
            boolean needDown = true;
            if (file.exists()) {
                if (ignoreExist) {
                    // 文件存在且设置了忽略已存在的文件则跳过此task
                    needDown = false;
                }
            }
            if (needDown) {
                // 文件不存在时才下载
                handlingTask = local;
                FileRequest request = new FileRequest(url, local).setHttpListener(fileHttpListener);
                http().executeAsync(request);
            } else {
                notifySuccess(local);
                handlingIndex++;
                // 继续下载
                downloading();
            }
        }
    }

    /**
     * 获取http url在本地缓存的路径
     */
    @SuppressWarnings("ConstantConditions")
    public String getLocalFilePath(String httpUrl, String dir) {
        return App.app().getLocalFilePath(httpUrl, dir);
    }

    private LiteHttp http() {
        LiteHttp liteHttp = LiteHttp.build(App.app()).create();
        // 10秒网络超时
        liteHttp.getConfig().setDebugged(true).setConnectTimeout(10000);
        return liteHttp;
    }

    private OnHttpListener<File> fileHttpListener = new OnHttpListener<File>(true, true) {
        @Override
        public void onSucceed(File data, Response<File> response) {
            super.onSucceed(data, response);
            notifySuccess(handlingTask);
        }

        @Override
        public void onFailed() {
            super.onFailed();
            notifyFailure();
            if (stopWhenFailure) {
                // 失败时退出并清空任务
                notifyStop();
                // 清空任务列表
                clearTask();
            }
        }

        @Override
        public void onStart(AbstractRequest<File> request) {
            super.onStart(request);
            notifyStart();
        }

        @Override
        public void onLoading(AbstractRequest<File> request, long total, long len) {
            super.onLoading(request, total, len);
            notifyProgressing((int) total, (int) len);
        }

        @Override
        public void onUploading(AbstractRequest<File> request, long total, long len) {
            super.onUploading(request, total, len);
            notifyProgressing((int) total, (int) len);
        }

        @Override
        public void onEnd(Response<File> response) {
            super.onEnd(response);
            handlingIndex++;
            if (handlingIndex >= tasks.size()) {
                // 下载完成
                notifyStop();
                clearTask();
            } else {
                // 继续下载
                downloading();
            }
        }
    };

    private String[] callbacksKeys() {
        int size = callbacks.size();
        if (size > 0) {
            return callbacks.keySet().toArray(new String[size]);
        }
        return new String[]{};
    }

    // 弱引用
    private HashMap<String, SoftReference<HttpHelperCallback>> callbacks = new HashMap<>();

    private void notifyCancel() {
        for (String key : callbacksKeys()) {
            HttpHelperCallback callback = callbacks.get(key).get();
            if (null != callback) {
                callback.onCancel(handlingIndex + 1, tasks.size());
            }
        }
    }

    private void notifyStart() {
        for (String key : callbacksKeys()) {
            HttpHelperCallback callback = callbacks.get(key).get();
            if (null != callback) {
                callback.onStart(handlingIndex + 1, tasks.size());
            }
        }
    }

    private void notifyProgressing(int currentTotal, int currentHandled) {
        for (String key : callbacksKeys()) {
            HttpHelperCallback callback = callbacks.get(key).get();
            if (null != callback) {
                callback.onProgressing(handlingIndex + 1, tasks.size(), currentHandled, currentTotal);
            }
        }
    }

    private void notifySuccess(String successPath) {
        for (String key : callbacksKeys()) {
            HttpHelperCallback callback = callbacks.get(key).get();
            if (null != callback) {
                callback.onSuccess(handlingIndex + 1, tasks.size(), successPath);
            }
        }
    }

    private void notifyFailure() {
        for (String key : callbacksKeys()) {
            HttpHelperCallback callback = callbacks.get(key).get();
            if (null != callback) {
                callback.onFailure(handlingIndex + 1, tasks.size());
            }
        }
    }

    private void notifyStop() {
        for (String key : callbacksKeys()) {
            HttpHelperCallback callback = callbacks.get(key).get();
            if (null != callback) {
                callback.onStop(handlingIndex + 1, tasks.size());
            }
        }
    }

    private void clearNoneUsedCallback() {
        for (String key : callbacksKeys()) {
            HttpHelperCallback callback = callbacks.get(key).get();
            if (null == callback) {
                callbacks.remove(key);
            }
        }
    }

    /**
     * 添加callback
     */
    public HttpHelper addCallback(HttpHelperCallback callback, String hashCodeOfHost) {
        if (!callbacks.containsKey(hashCodeOfHost)) {
            callbacks.put(hashCodeOfHost, new SoftReference<>(callback));
        }
        return this;
    }

    /**
     * 移除callback
     */
    public void removeCallback(String hashCodeOfHost) {
        if (callbacks.containsKey(hashCodeOfHost)) {
            callbacks.remove(hashCodeOfHost);
        }
    }

    /**
     * Http执行进度回调
     */
    public static class HttpHelperCallback extends OnHttpListener<File> {

        public void onCancel(int current, int total) {
        }

        public void onStart(int current, int total) {
        }

        /**
         * task完成进度
         *
         * @param current        当前正在进行的任务
         * @param total          总共需要完成的任务
         * @param currentHandled 当前任务的进行进度
         * @param currentTotal   当前任务需要完成的总量
         */
        public void onProgressing(int current, int total, int currentHandled, int currentTotal) {
        }

        public void onSuccess(int current, int total, String currentPath) {
        }

        public void onFailure(int current, int total) {
        }

        public void onStop(int current, int total) {
        }
    }
}
