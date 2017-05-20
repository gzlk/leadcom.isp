package com.gzlk.android.isp.manager.listener;

import com.gzlk.android.isp.model.Model;

import java.util.List;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/20 23:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/20 23:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface MultipleManageListener<T extends Model> {
    void onLoaded(List<T> list, int pageNumber);
}
