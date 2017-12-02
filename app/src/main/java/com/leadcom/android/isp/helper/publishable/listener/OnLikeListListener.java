package com.leadcom.android.isp.helper.publishable.listener;

import com.leadcom.android.isp.model.archive.ArchiveLike;

import java.util.List;

/**
 * <b>功能描述：</b>加载点赞列表的接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/04 14:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/04 14:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public interface OnLikeListListener {
    /**
     * 点赞列表
     *
     * @param list     拉取到的评论点赞列表
     * @param success  api是否调用成功
     * @param pageSize 页码大小
     */
    void onList(List<ArchiveLike> list, boolean success, int pageSize);
}
