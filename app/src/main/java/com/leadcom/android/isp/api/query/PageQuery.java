package com.leadcom.android.isp.api.query;

import com.leadcom.android.isp.api.Api;

import java.util.List;

/**
 * <b>功能描述：</b>新的分页查询<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/18 16:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/05/18 16:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class PageQuery<T> extends Api<T> {

    private List<T> rows;
    private int current, size, pages, total;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
