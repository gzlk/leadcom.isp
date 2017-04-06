package com.gzlk.android.isp.fragment.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;

/**
 * <b>功能描述：</b>提供Android 6.0+权限相关操作的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 20:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 20:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class PermissionHandleFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) getActivity();
    }

    private BaseActivity mActivity;

    public BaseActivity Activity() {
        if (null == mActivity) {
            // 如果当前attach的activity为null则从activity列表顶层获取一个
            //mActivity = (BaseActivity) LxbgApp.getInstance().getActivity();
        }
        return mActivity;
    }

    // Android 6.0 + 的 permission grant 操作的需求值
    /**
     * app运行时检测基本的权限列表
     */
    public static final int GRANT_BASE = 0x50;
    /**
     * app运行时请求camera的权限
     */
    public static final int GRANT_CAMERA = GRANT_BASE + 1;
    /**
     * app运行时向用户请求位置权限
     */
    public static final int GRANT_LOCATION = GRANT_BASE + 2;
    /**
     * 请求录音设备权限
     */
    public static final int GRANT_RECORD_AUDIO = GRANT_BASE + 3;
    /**
     * 请求拨打电话的权限
     */
    public static final int GRANT_PHONE_CALL = GRANT_BASE + 4;
    /**
     * 请求接收SMS的权限
     */
    public static final int GRANT_SMS = GRANT_BASE + 5;
    /**
     * 请求读取手机联系人
     */
    public static final int GRANT_CONTACTS = GRANT_BASE + 6;

    /**
     * 尝试获取拨打电话的运行时权限
     */
    public void requestPhoneCallPermission() {
        String warning = mActivity.getString(R.string.ui_grant_permission_phone_call);
        String denied = mActivity.getString(R.string.ui_denied_permission_phone_call);
        tryGrantPermission(Manifest.permission.CALL_PHONE, BaseFragment.GRANT_PHONE_CALL, warning, denied);
    }

    /**
     * 尝试申请运行时权限
     *
     * @param permission  权限名称
     * @param requestCode 请求代码，在权限申请结果时可以判断具体的申请请求
     * @param rationale   需要立即申请权限时显示给用户看的信息
     * @param denied      权限被拒绝时显示给用户看的信息
     */
    public void tryGrantPermission(String permission, int requestCode, String rationale, String denied) {
        warningPermissionDenied = denied;
        currentRequestCode = requestCode;
        // Android 6.0+才需要权限申请判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，是否需要向用户显示申请权限的理由
                if (shouldShowRequestPermissionRationale(permission)) {
                    warningPermissionRationale(rationale, permission, requestCode);
                } else {
                    // 用户禁止了弹出权限提示
                    //warningPermissionDenied();
                    requestPermission(permission, requestCode);
                }
            } else {
                // 权限已经申请过了
                permissionGranted(new String[]{permission}, requestCode);
            }
        } else {
            permissionGranted(new String[]{permission}, requestCode);
        }
    }

    /**
     * 检测当前app是否有某个相关运行时权限
     */
    public boolean hasPermission(String permission) {
        // Android 6.0+ 才需要权限申请判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，是否需要向用户显示申请权限的理由
                return false;
            }
        }
        return true;
    }

    private String warningPermissionDenied;
    private int currentRequestCode;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted(permissions, requestCode);
        } else {
            warningPermissionDenied();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 子类需要重载此方法以便处理权限申请成功之后的事情
     */
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_PHONE_CALL) {
            ToastHelper.make(mActivity).showMsg("您已进行授权，请重新点击拨号。");
        }
    }

    /**
     * 子类需要重载此方法以便处理权限申请失败之后的事情
     */
    public void permissionGrantFailed(int requestCode) {
    }

    /**
     * 向用户显示权限申请的理由
     */
    private void warningPermissionRationale(String text, final String permission, final int requestCode) {
        warningDialog(text, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                requestPermission(permission, requestCode);
                return true;
            }
        });
    }

    /**
     * 没有权限或权限被禁止了
     */
    private void warningPermissionDenied() {
        if (!StringHelper.isEmpty(warningPermissionDenied)) {
            warningDialog(warningPermissionDenied, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    permissionGrantFailed(currentRequestCode);
                    return true;
                }
            });
        } else {
            permissionGrantFailed(currentRequestCode);
        }
    }

    private void warningDialog(String text, DialogHelper.OnDialogConfirmListener confirmListener) {
        SimpleDialogHelper.init(mActivity).show(text, confirmListener, null);
    }

    /**
     * 请求运行时权限
     */
    private void requestPermission(String permission, int requestCode) {
        requestPermissions(new String[]{permission}, requestCode);
    }
}
