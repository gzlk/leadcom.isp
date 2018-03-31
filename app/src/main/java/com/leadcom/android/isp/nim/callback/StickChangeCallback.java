package com.leadcom.android.isp.nim.callback;

import com.netease.nimlib.sdk.msg.model.RecentContact;

/**
 * <b>功能描述：</b>最近联系人置顶项目更改事件<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/31 13:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public interface StickChangeCallback {
    void onChange(RecentContact contact);
}
