package com.leadcom.android.isp.http

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


/**
 * <b>功能描述：</b>网络环境相关方法<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/28 10:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/28 10:43  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

/**当前是否有可用的网络连接*/
fun hasNetwork(context: Context): Boolean? {
    var isConnected: Boolean? = false // Initial Value
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}