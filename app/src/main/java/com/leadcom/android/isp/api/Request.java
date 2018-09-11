package com.leadcom.android.isp.api;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.LoginActivity;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.query.NumericQuery;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.StringQuery;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.NetworkUtil;
import com.leadcom.android.isp.etc.ReflectionUtil;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnHttpListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.query.FullTextQuery;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;

import java.lang.reflect.Type;
import java.util.List;

/**
 * <b>功能描述：</b>所有网络请求的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/24 16:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/24 16:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class Request<T> {

    /**
     * 默认多页查询时页大小
     */
    protected static final int PAGE_SIZE = 10;

    protected static final String URL = format("%s/%s", BaseApi.URL, BaseApi.API_VER);
    /**
     * 新增
     */
    protected static final String ADD = "/add";
    /**
     * 删除
     */
    protected static final String DELETE = "/delete";
    /**
     * 退出
     */
    protected static final String EXIT = "/exit";
    /**
     * 更新
     */
    protected static final String UPDATE = "/update";
    /**
     * 单查找
     */
    protected static final String FIND = "/find";
    /**
     * 列表
     */
    protected static final String LIST = "/list";
    /**
     * 列表
     */
    protected static final String SELECT = "/select";
    /**
     * 搜索
     */
    protected static final String SEARCH = "/search";
    /**
     * 转发、推送
     */
    protected static final String PUSH = "/push";
    /**
     * 分享
     */
    protected static final String SHARE = "/share";
    /**
     * 清空
     */
    protected static final String CLEAN = "/clean";

    protected static final int MAX_PAGE_SIZE = 1000;
    protected static final String SUMMARY = format("abstrSize=%d&abstrRow=%d", 100, 5);

    /**
     * 组合url
     */
    protected abstract String url(String action);

    /**
     * http网络访问层
     */
    protected LiteHttp http;

    protected String accessToken;

    public Request() {
        HttpLog.isPrint = false;
        http = LiteHttp.build(App.app()).create();
        // 15 秒网络超时
        http.getConfig().setDebugged(false)
                .setConnectTimeout(15000)
                .setSocketTimeout(15000);
        accessToken = Cache.cache().accessToken;
        initializeDao();
        relogin = false;
    }

    protected static String format(String fmt, Object... args) {
        return StringHelper.format(fmt, args);
    }

    protected static boolean isEmpty(String text) {
        return isEmpty(text, false);
    }

    protected static boolean isEmpty(String text, boolean nullable) {
        return StringHelper.isEmpty(text, nullable);
    }

    protected void log(String log) {
        LogHelper.log(this.getClass().getSimpleName(), log);
    }

    /**
     * 检测给定值是否为null，是则返回""字符串，而不是null
     */
    protected String checkNull(String value) {
        return isEmpty(value) ? "" : value;
    }

    /**
     * 发起网络请求
     */
    protected void executeHttpRequest(JsonRequest request) {
        if (NetworkUtil.isNetAvailable(App.app())) {
            http.executeAsync(request);
        } else {
            ToastHelper.make().showMsg(R.string.ui_base_text_network_invalid);
        }
    }

    private boolean relogin = false;
    protected Dao<T> dao;
    /**
     * 是否支持直接保存
     */
    protected boolean directlySave = true;

    protected abstract Class<T> getType();

    private void initializeDao() {
        if (null == dao) {
            dao = new Dao<>(getType());
        }
    }

    protected void save(T t) {
        if (!directlySave) {
            directlySave = true;
            return;
        }
        if (!relogin && null != t) {
            initializeDao();
            try {
                dao.save(t);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    protected void save(List<T> list) {
        if (!directlySave) {
            directlySave = true;
            return;
        }
        if (!relogin && null != list && list.size() > 0) {
            initializeDao();
            try {
                dao.save(list);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }

    /**
     * 组合请求
     */
    protected JsonRequest<Api<T>> getRequest(Type resultType, String action, final String body, final HttpMethods methods) {
        final String url = format("%s%s", URL, action);
        final long start = Utils.timestamp();
        OnHttpListener<Api<T>> listener = new OnHttpListener<Api<T>>() {

            @Override
            public void onSucceed(Api<T> data, Response<Api<T>> response) {
                super.onSucceed(data, response);
                long end = Utils.timestamp();
                log(format("\nurl(%s): %s\naccessToken: %s, terminalType: android\n%ssuccess: %s(%s,%s, time used: %dms)\nraw: %s", methods, url, accessToken,
                        (isEmpty(body) ? "" : format("body: %s\n", body)), (null == data ? "null" : data.success()),
                        (null == data ? "null" : data.getCode()), (null == data ? "null" : data.getMsg()), (end - start), response.getRawString()));
                if (null != data && data.success()) {
                    if (data instanceof PaginationQuery) {
                        if (null != onMultipleRequestListener) {
                            PaginationQuery<T> paginationQuery = (PaginationQuery<T>) data;
                            Pagination<T> pagination = paginationQuery.getData();
                            save(pagination.getList());
                            onMultipleRequestListener.invtNum = paginationQuery.getInvtNum();
                            onMultipleRequestListener.userInfoNum = paginationQuery.getUserInfoNum();
                            onMultipleRequestListener.lastHeadPhoto = paginationQuery.getLastHeadPhoto();
                            onMultipleRequestListener.onResponse(pagination.getList(), data.success(),
                                    pagination.getTotalPages(), pagination.getPageSize(),
                                    pagination.getTotal(), pagination.getPageNumber());
                        }
                    } else if (data instanceof PageQuery) {
                        if (null != onMultipleRequestListener) {
                            PageQuery<T> pageQuery = (PageQuery<T>) data;
                            onMultipleRequestListener.onResponse(pageQuery.getRows(), pageQuery.success(), pageQuery.getPages(),
                                    pageQuery.getSize(), pageQuery.getTotal(), pageQuery.getCurrent());
                        }
                    } else if (data instanceof ListQuery) {
                        if (null != onMultipleRequestListener) {
                            ListQuery<T> listQuery = (ListQuery<T>) data;
                            save(listQuery.getData());
                            onMultipleRequestListener.unreadNum = listQuery.getUnreadNum();
                            onMultipleRequestListener.onResponse(listQuery.getData(), data.success(),
                                    1, PAGE_SIZE, listQuery.getData().size(), 1);
                        }
                    } else if (data instanceof SingleQuery) {
                        SingleQuery<T> singleQuery = (SingleQuery<T>) data;
                        save(singleQuery.getData());
                        if (null != onSingleRequestListener) {
                            onSingleRequestListener.query = singleQuery;
                            onSingleRequestListener.userRelateGroupList = singleQuery.getUserRelateGroupList();
                            onSingleRequestListener.actInviteStatus = singleQuery.getActInvtStatus();
                            if (singleQuery.getData() instanceof FullTextQuery) {
                                onSingleRequestListener.onResponse(singleQuery.getData(), data.success(), response.getRawString());
                            } else {
                                onSingleRequestListener.onResponse(singleQuery.getData(), data.success(), data.getMsg());
                            }
                        }
                    } else if (data instanceof BoolQuery) {
                        BoolQuery<T> boolQuery = (BoolQuery<T>) data;
                        boolean hasData = response.getRawString().contains("data");
                        if (null != onSingleRequestListener) {
                            onSingleRequestListener.onResponse(null, hasData ? boolQuery.getData() : data.success(), data.getMsg());
                        }
                    } else if (data instanceof StringQuery) {
                        StringQuery<T> stringQuery = (StringQuery<T>) data;
                        if (null != onSingleRequestListener) {
                            onSingleRequestListener.onResponse(newInstance(stringQuery.getData()), stringQuery.success(), data.getMsg());
                        }
                    } else if (data instanceof NumericQuery) {
                        NumericQuery<T> query = (NumericQuery<T>) data;
                        if (null != onSingleRequestListener) {
                            onSingleRequestListener.onResponse(newInstance(String.valueOf(query.getData())), query.success(), query.getMsg());
                        }
                    } else {
                        if (null != onSingleRequestListener) {
                            onSingleRequestListener.onResponse(null, data.success(), data.getMsg());
                        }
                        if (null != onMultipleRequestListener) {
                            onMultipleRequestListener.onResponse(null, data.success(), 0, 0, 0, 1);
                        }
                    }
                } else {
                    ToastHelper.make().showMsg(null == data ? "content is null" : data.getMsg());
                    fireFailedListenerEvents(null == data ? "content is null" : data.getMsg());
                    if (null != data && data.relogin()) {
                        relogin = true;
                        App.app().logout();
                        LoginActivity.start(App.app());
                    }
                }
            }

            @SuppressWarnings("unchecked")
            private T newInstance(String data) {
                T obj;
                try {
                    obj = getType().newInstance();
                    if (null != obj) {
                        ReflectionUtil.invokeMethod(obj, "setId", new Object[]{data});
                    }
                } catch (Exception e) {
                    obj = null;
                }
                return obj;
            }

            @Override
            public void onFailed() {
                super.onFailed();
                long end = Utils.timestamp();
                log(format("url(%s): %s\naccessToken: %s, terminalType: android%s\nsuccess: failed(time used: %dms)", methods, url, accessToken,
                        (isEmpty(body) ? "" : format("\nbody: %s\n", body)), (end - start)));
                fireFailedListenerEvents("");
            }
        };
        JsonRequest<Api<T>> request = new JsonRequest<Api<T>>(url, resultType)
                .setHttpListener(listener)
                .addHeader("accessToken", accessToken)
                .addHeader("terminalType", "android")
                .setHttpBody(new JsonBody(body), methods);
        if (App.app().needSetConnectionCloseHeader()) {
            // 如果Ok Http size=0错误超过了10次，则为http头添加close参数
            request.addHeader("Connection", "close");
            log("Add \"Connection: close\" header.");
        }
        return request;
    }

    /**
     * 通知失败
     */
    protected void fireFailedListenerEvents(String message) {
        if (null != onSingleRequestListener) {
            onSingleRequestListener.onResponse(null, false, message);
        }
        if (null != onMultipleRequestListener) {
            onMultipleRequestListener.onResponse(null, false, 0, 0, 0, 0);
        }
    }

    protected OnSingleRequestListener<T> onSingleRequestListener;

    protected void fireOnSingleRequestListener(T t) {
        if (null != onSingleRequestListener) {
            onSingleRequestListener.onResponse(t, true, "");
        }
    }

    /**
     * 设置网络调用成功之后的回调
     */
    public abstract Request<T> setOnSingleRequestListener(OnSingleRequestListener<T> listener);

    protected OnMultipleRequestListener<T> onMultipleRequestListener;

    protected void fireOnMultipleRequestListener(List<T> list, boolean success, int totalCount, int pageNumber) {
        if (null != onMultipleRequestListener) {
            int page = totalCount / PAGE_SIZE + (totalCount % PAGE_SIZE > 0 ? 1 : 0);
            onMultipleRequestListener.onResponse(list, success, page, PAGE_SIZE, totalCount, pageNumber);
        }
    }

    /**
     * 添加请求列表时的处理回调
     */
    public abstract Request<T> setOnMultipleRequestListener(OnMultipleRequestListener<T> listListener);

}
