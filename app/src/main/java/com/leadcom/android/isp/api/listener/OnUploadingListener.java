package com.leadcom.android.isp.api.listener;

/**
 * <b>功能描述：</b>上传进度监听<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 17:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 17:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class OnUploadingListener<Data> {

    /**
     * 上传进度
     */
    public void onUploading(Data data, long total, long length) {
    }
}
