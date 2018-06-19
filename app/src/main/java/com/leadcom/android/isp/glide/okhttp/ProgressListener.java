package com.leadcom.android.isp.glide.okhttp;

/**
 * <b>功能描述：</b>网络流进度的的监听<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/06/19 09:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/06/19 09:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public interface ProgressListener {
    void onProgress(int progress);
}
