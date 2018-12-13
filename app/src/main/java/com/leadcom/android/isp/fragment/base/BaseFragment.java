package com.leadcom.android.isp.fragment.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.ContainerActivity;
import com.leadcom.android.isp.activity.LoginActivity;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.organization.GroupPickerFragment;
import com.leadcom.android.isp.helper.ClipboardHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.TooltipHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.ShareItemViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.model.common.ShareItem;
import com.leadcom.android.isp.share.ShareToQQ;
import com.leadcom.android.isp.share.ShareToWeiBo;
import com.leadcom.android.isp.share.ShareToWeiXin;

import java.util.ArrayList;
import java.util.Locale;

/**
 * <b>功能描述：</b>提供一些基本方法的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 20:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 20:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseFragment extends BasePermissionHandleSupportFragment {

    /**
     * 空的json数组
     */
    public static final String EMPTY_ARRAY = "[]";
    // 各种app图标资源
    public static final String ICON_RECT_1024x1024 = "http://image.py17w.net:8008/group1/M00/00/4B/eBk66ltxeseAPGPqAAgbo9Gnh5g589.png";
    public static final String ICON_RECT_256x256 = "http://image.py17w.net:8008/group1/M00/00/4B/eBk66ltxfCCABDEZAABzH4SMLsk068.png";
    public static final String ICON_OVAL_512x512 = "http://image.py17w.net:8008/group1/M00/00/50/cErYIVtxe5KAfvKUAAF1AduIvXE159.png";
    /**
     * fragment的根UI
     */
    protected View mRootView;

    private Handler mHandler = new Handler();

    public Handler Handler() {
        return mHandler;
    }

    /**
     * 当前设备屏幕宽度像素
     */
    protected int mScreenWidth;
    /**
     * 当前设备屏幕高度像素
     */
    protected int mScreenHeight;

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * 将字符串以半角逗号分隔成字符数组
     */
    protected static String[] splitParameters(String params) {
        if (params.charAt(0) == '[') {
            // json 对象
            return Json.gson().fromJson(params, new TypeToken<String[]>() {
            }.getType());
        } else {
            // 普通字符串
            return splitParameters(params, -1);
        }
    }

    protected static String[] splitParameters(String params, int limit) {
        return params.split(",", limit);
    }

    /**
     * 格式化字符串
     */
    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String string) {
        return StringHelper.isEmpty(string);
    }

    /**
     * 判断字符串是否为空，"null"也当作空
     */
    public static boolean isEmpty(String string, boolean canBeStringNull) {
        return StringHelper.isEmpty(string, canBeStringNull);
    }

    /**
     * 清空指定View的父级容器
     */
    public void clearDirectParent(View view) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        }
    }

    /**
     * 将json的“,”替换成“@”以便传输到intent里去
     *
     * @param string 字符串
     * @param toJson true=反转回json，false=替换掉json中的","为"@"
     */
    public static String replaceJson(String string, boolean toJson) {
        return StringHelper.replaceJson(string, toJson);
    }

    /**
     * 获取颜色
     */
    public int getColor(int res) {
        return ContextCompat.getColor(Activity(), res);
    }

    /**
     * 获取dimention尺寸
     */
    public int getDimension(int res) {
        return Activity().getDimension(res);
    }

    /**
     * 获取integer值
     */
    public int getInteger(int res) {
        return Activity().getInteger(res);
    }

    /**
     * 获取字体大小，单位sp/dp
     */
    public int getFontDimension(int res) {
        return Utility.ConvertPx(getDimension(res));
    }

    /**
     * 获取默认的动画时间长度
     */
    public int duration() {
        return getInteger(R.integer.integer_default_animate_duration);
    }

    /**
     * BackKey事件，子类需要重载onBackKeyPressed方法来实现自己的事件处理逻辑
     */
    public final boolean onBackKeyEvent() {
        return onBackKeyPressed();
    }

    /**
     * 关闭Activity页面
     */
    public void finish() {
        finish(false);
    }

    /**
     * 返回上一页Activity并指定返回成功，以便上一页通过onActivityResult捕获消息
     */
    public void resultSucceededActivity() {
        resultData("");
    }

    /**
     * 返回数据时的字段名称
     */
    public static final String RESULT_STRING = "_result_string_";
    public static final String RESULT_ARCHIVE = "_result_archive_";

    /**
     * 返回上一页Activity并指定要返回的数据内容，以便上一页通过onActivityResult捕获消息
     */
    public void resultData(String data) {
        Intent intent = new Intent();
        if (!isEmpty(data)) {
            intent.putExtra(RESULT_STRING, data);
        }
        resultData(intent);
    }

    public void resultData(Intent intent) {
        if (null == intent) {
            Activity().setResult(Activity.RESULT_OK);
        } else {
            Activity().setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    public static String getResultedData(Intent data) {
        if (null == data) return null;
        return data.getStringExtra(RESULT_STRING);
    }

    public static Model getResultModel(Intent data, String name) {
        return null == data ? null : (Model) data.getSerializableExtra(name);
    }

    protected void closeWithWarning(int text) {
        ToastHelper.helper().showMsg(text);
        finish();
    }

    /**
     * 关闭Activity
     *
     * @param toMainUI true=直接返回主页面并且固定显示页，false=普通Activity关闭（受backState堆栈的顺序影响）
     */
    public void finish(boolean toMainUI) {
        // 这里固定为2，也即首页中
        finish(toMainUI ? 2 : -1);
    }

    /**
     * 直接返回到主页面，并且指定主页面要显示的页面index
     *
     * @param mainPageDisplayIndex 指定主页面中ViewPager要显示的index。小于0时跟普通Activity的backKey事件一样（并不一定能够返回到主页面）
     */
    public void finish(int mainPageDisplayIndex) {
        Utils.hidingInputBoard(mRootView);
        Activity().finish();
        if (mainPageDisplayIndex >= 0) {
            // 打开主UI
            Intent intent = new Intent(Activity(), MainActivity.class);

            Bundle bundle = new Bundle();
            // 这里默认返回到主页的时候都显示“找”页面
            bundle.putInt(MainActivity.PARAM_SELECTED, mainPageDisplayIndex);
            intent.putExtras(bundle);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * 回到登录页面
     */
    public void finishToSignIn() {
        // 先关闭本页面
        Activity().finish();

        Intent intent = new Intent(Activity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 不需要新打开的Activity返回结果
     */
    public static final int RESULT_NONE = -1;
    /**
     * 需求新打开的Activity返回结果
     */
    public static final int ACTIVITY_BASE_REQUEST = 10000;
    /**
     * 需求照相机
     */
    public static final int REQUEST_CAMERA = ACTIVITY_BASE_REQUEST + 1;
    /**
     * 需求相册
     */
    public static final int REQUEST_GALLERY = ACTIVITY_BASE_REQUEST + 2;
    /**
     * 第三方相册预览
     */
    public static final int REQUEST_PREVIEW = ACTIVITY_BASE_REQUEST + 3;
    /**
     * 需求剪切照片
     */
    public static final int REQUEST_CROP = ACTIVITY_BASE_REQUEST + 4;
    /**
     * 选择视频
     */
    public static final int REQUEST_VIDEO = ACTIVITY_BASE_REQUEST + 5;
    /**
     * 选择音频
     */
    public static final int REQUEST_MUSIC = ACTIVITY_BASE_REQUEST + 6;
    /**
     * 选择附件(任意文件)
     */
    public static final int REQUEST_ATTACHMENT = ACTIVITY_BASE_REQUEST + 7;
    /**
     * 隐私设置
     */
    public static final int REQUEST_SECURITY = ACTIVITY_BASE_REQUEST + 10;
    /**
     * 标签选择
     */
    public static final int REQUEST_LABEL = ACTIVITY_BASE_REQUEST + 11;
    /**
     * 封面选择
     */
    public static final int REQUEST_COVER = ACTIVITY_BASE_REQUEST + 12;
    /**
     * 成员选择
     */
    public static final int REQUEST_MEMBER = ACTIVITY_BASE_REQUEST + 13;
    /**
     * 草稿选择
     */
    public static final int REQUEST_DRAFT = ACTIVITY_BASE_REQUEST + 14;
    /**
     * 选择
     */
    public static final int REQUEST_SELECT = ACTIVITY_BASE_REQUEST + 15;
    /**
     * 新增
     */
    public static final int REQUEST_CREATE = ACTIVITY_BASE_REQUEST + 16;
    /**
     * 更改
     */
    public static final int REQUEST_CHANGE = ACTIVITY_BASE_REQUEST + 17;
    /**
     * 删除
     */
    public static final int REQUEST_DELETE = ACTIVITY_BASE_REQUEST + 18;
    /**
     * 组织选择
     */
    public static final int REQUEST_GROUP = ACTIVITY_BASE_REQUEST + 19;
    /**
     * 请求地址
     */
    public static final int REQUEST_ADDRESS = ACTIVITY_BASE_REQUEST + 20;
    /**
     * 编辑请求
     */
    public static final int REQUEST_EDIT = ACTIVITY_BASE_REQUEST + 21;
    protected static final int REQUEST_PHONE = ACTIVITY_BASE_REQUEST + 22;
    protected static final int REQUEST_PHONE_CONFIRM = ACTIVITY_BASE_REQUEST + 23;
    /**
     * 动态图片页中的评论
     */
    public static final int REQUEST_COMMENT = ACTIVITY_BASE_REQUEST + 24;
    /**
     * 小组选择
     */
    public static final int REQUEST_SQUAD = ACTIVITY_BASE_REQUEST + 25;
    /**
     * 档案性质选择
     */
    public static final int REQUEST_PROPERTY = ACTIVITY_BASE_REQUEST + 26;
    /**
     * 档案类型选择
     */
    public static final int REQUEST_CATEGORY = ACTIVITY_BASE_REQUEST + 27;
    /**
     * 档案、活动的回复
     */
    public static final int REQUEST_REPLY = ACTIVITY_BASE_REQUEST + 28;
    /**
     * 感兴趣
     */
    protected static final int REQUEST_INTEREST = ACTIVITY_BASE_REQUEST + 100;
    /**
     * 关注组织
     */
    protected static final int REQUEST_CONCERNED = ACTIVITY_BASE_REQUEST + 101;

    /**
     * 启动容器Activity(此时打开的新Activity不需要返回确认)
     *
     * @param fullClassName  fragment的类全名
     * @param params         参数列表
     * @param supportToolbar 是否支持toolbar
     * @param supportBackKey 是否要处理backKey事件
     */
    public void openActivity(String fullClassName, String params, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, ACTIVITY_BASE_REQUEST, supportToolbar, supportBackKey);
    }

    /**
     * 启动容器Activity
     *
     * @param fullClassName  fragment的类全名
     * @param params         参数列表
     * @param requestCode    请求码
     * @param supportToolbar 是否支持toolbar
     * @param supportBackKey 是否要处理backKey事件
     */
    public void openActivity(String fullClassName, String params, int requestCode, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, requestCode, supportToolbar, supportBackKey, false);
    }

    /**
     * 启动容器Activity(此时打开的新Activity不需要返回确认)
     *
     * @param fullClassName        fragment的类全名
     * @param params               参数列表
     * @param supportToolbar       是否支持toolbar
     * @param supportBackKey       是否要处理backKey事件
     * @param transparentStatusBar 是否需要状态栏透明化
     */
    public void openActivity(String fullClassName, String params, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        openActivity(fullClassName, params, ACTIVITY_BASE_REQUEST, supportToolbar, supportBackKey, transparentStatusBar);
    }

    /**
     * 启动容器Activity
     *
     * @param fullClassName        fragment的类全名
     * @param params               参数列表
     * @param requestCode          请求码
     * @param supportToolbar       是否支持toolbar
     * @param supportBackKey       是否要处理backKey事件
     * @param transparentStatusBar 是否需要状态栏透明化
     */
    public void openActivity(String fullClassName, String params, int requestCode, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        Intent intent = new Intent(Activity(), ContainerActivity.class);
        Bundle b = new Bundle();
        b.putInt(ContainerActivity.REQUEST_CODE, requestCode);
        b.putString(ContainerActivity.REQUEST_CLASS, fullClassName);
        b.putString(ContainerActivity.REQUEST_PARAMS, params);
        b.putBoolean(ContainerActivity.REQUEST_TOOL_BAR, supportToolbar);
        b.putBoolean(ContainerActivity.REQUEST_BACK_KEY, supportBackKey);
        b.putBoolean(ContainerActivity.REQUEST_TRANSPARENT_STATUS_BAR, transparentStatusBar);
        intent.putExtra(ContainerActivity.EXTRA_BUNDLE, b);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 启动容器Activity(此时打开的新Activity不需要返回确认)
     *
     * @param fullClassName  fragment的类全名
     * @param params         参数列表
     * @param supportToolbar 是否支持toolbar
     * @param supportBackKey 是否要处理backKey事件
     */
    public void openActivity(String fullClassName, Bundle params, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, ACTIVITY_BASE_REQUEST, supportToolbar, supportBackKey);
    }

    /**
     * 启动容器Activity
     *
     * @param fullClassName  fragment的类全名
     * @param params         参数列表
     * @param requestCode    请求码
     * @param supportToolbar 是否支持toolbar
     * @param supportBackKey 是否要处理backKey事件
     */
    public void openActivity(String fullClassName, Bundle params, int requestCode, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, requestCode, supportToolbar, supportBackKey, false);
    }

    /**
     * 启动容器Activity(此时打开的新Activity不需要返回确认)
     *
     * @param fullClassName        fragment的类全名
     * @param params               参数列表
     * @param supportToolbar       是否支持toolbar
     * @param supportBackKey       是否要处理backKey事件
     * @param transparentStatusBar 是否需要状态栏透明化
     */
    public void openActivity(String fullClassName, Bundle params, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        openActivity(fullClassName, params, ACTIVITY_BASE_REQUEST, supportToolbar, supportBackKey, transparentStatusBar);
    }

    /**
     * 启动容器Activity
     *
     * @param fullClassName        fragment的类全名
     * @param params               参数列表
     * @param requestCode          请求码
     * @param supportToolbar       是否支持toolbar
     * @param supportBackKey       是否要处理backKey事件
     * @param transparentStatusBar 是否需要状态栏透明化
     */
    public void openActivity(String fullClassName, Bundle params, int requestCode, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        Intent intent = new Intent(Activity(), ContainerActivity.class);
        Bundle b = new Bundle();
        b.putInt(ContainerActivity.REQUEST_CODE, requestCode);
        b.putString(ContainerActivity.REQUEST_CLASS, fullClassName);
        b.putBundle(ContainerActivity.REQUEST_BUNDLE, params);
        b.putBoolean(ContainerActivity.REQUEST_TOOL_BAR, supportToolbar);
        b.putBoolean(ContainerActivity.REQUEST_BACK_KEY, supportBackKey);
        b.putBoolean(ContainerActivity.REQUEST_TRANSPARENT_STATUS_BAR, transparentStatusBar);
        intent.putExtra(ContainerActivity.EXTRA_BUNDLE, b);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SHAREABLE) {
            ShareToQQ.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == Activity.RESULT_OK) {
            onActivityResult(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Activity result事件，子类重载此方法以获取自己的处理方式
     */
    public void onActivityResult(int requestCode, Intent data) {
    }

    /**
     * 微博分享时需要用到的 onNewIntent
     */
    public void onNewIntent(Intent intent) {
        ShareToWeiBo stwb = ShareToWeiBo.instance();
        if (null != stwb) {
            stwb.onNewInstance(intent);
        }
    }

    /**
     * fragment是否处于后台状态
     */
    protected boolean isStopped = false;

    @Override
    public void onStart() {
        isStopped = false;
        super.onStart();
    }

    @Override
    public void onStop() {
        isStopped = true;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        ShareToWeiBo.clear();
        super.onDestroy();
    }

    /**
     * 是否可以分享到第三方媒体
     */
    protected boolean SHAREABLE = false;
    protected boolean INTERNAL_SHAREABLE = true;
    // 分享
    private View shareDialog;
    private RelativeLayout shareItemsLayer;
    private LinearLayout shareDots;
    private RecyclerView shareItemsView;
    private ShareItemsAdapter sAdapter;
    protected ShareInfo mShareInfo;
    private DialogHelper shareDialogHelper;
    private CustomTextView dot1, dot2;
    /**
     * 是否允许删除档案、转发档案、推荐档案到首页、取消首页档案的推荐
     */
    protected boolean enableShareWX = true, enableShareTimeLine = true, enableShareQQ = true, enableShareQZone = true,
            enableShareDelete = false, enableShareForward = false, enableShareRecommend = false, enableShareRecommended = false,
            enableAward = false, enableAwarded = false, enableClassify = false, enableReplay = false, enableTransform = false,
            enableProperty = false;

    /**
     * 打开分享选择对话框
     */
    protected void openShareDialog() {
        ShareItem.init();
        if (null == shareDialogHelper) {
            shareDialogHelper = DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {

                private boolean isScrollEnabled = false;

                @Override
                public View onInitializeView() {
                    if (null == shareDialog) {
                        shareDialog = View.inflate(Activity(), R.layout.popup_dialog_share_items, null);
                        shareDots = shareDialog.findViewById(R.id.ui_popup_share_items_dots);
                        dot1 = shareDialog.findViewById(R.id.ui_popup_share_items_dot_1);
                        dot2 = shareDialog.findViewById(R.id.ui_popup_share_items_dot_2);
                        shareItemsLayer = shareDialog.findViewById(R.id.ui_popup_share_items);
                        shareItemsView = shareDialog.findViewById(R.id.ui_tool_swipe_refreshable_recycler_view);
                        shareItemsView.setLayoutManager(new FlexboxLayoutManager(shareItemsView.getContext(), FlexDirection.ROW, FlexWrap.WRAP));
                        shareItemsView.addOnScrollListener(scrollListener);
                    }
                    if (null == sAdapter) {
                        sAdapter = new ShareItemsAdapter();
                        shareItemsView.setAdapter(sAdapter);
                    }
                    return shareDialog;
                }

                private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (isScrollEnabled) {
                            FlexboxLayoutManager manager = (FlexboxLayoutManager) recyclerView.getLayoutManager();
                            assert manager != null;
                            dot1.setTextColor(getColor(manager.findFirstCompletelyVisibleItemPosition() < 4 ? R.color.colorPrimary : R.color.textColorHintLight));
                            dot2.setTextColor(getColor(manager.findLastCompletelyVisibleItemPosition() < 7 ? R.color.textColorHintLight : R.color.colorPrimary));
                        }
                    }
                };

                @Override
                public void onBindData(View dialogView, DialogHelper helper) {
                    sAdapter.clear();
                    int count = 0;
                    for (ShareItem item : ShareItem.items) {
                        if (item.visible()) {
                            if ((item.wx() && enableShareWX) || (item.timeline() && enableShareTimeLine) ||
                                    (item.qq() && enableShareQQ) || (item.qzone() && enableShareQZone)) {
                                sAdapter.add(item);
                                count++;
                            }
                        } else {
                            if ((item.deletable() && enableShareDelete) || (item.forwardable() && enableShareForward) ||
                                    (item.recommendable() && enableShareRecommend) || (item.unrecommendable() && enableShareRecommended) ||
                                    (item.awardable() && enableAward) || (item.unawardable() && enableAwarded) ||
                                    (item.classfiyable() && enableClassify) || (item.repliable() && enableReplay) ||
                                    (item.transformable() && enableTransform) || (item.propertyEditable() && enableProperty)) {
                                sAdapter.add(item);
                                count++;
                            }
                        }
                        if (sAdapter.getItemCount() > 1 && count % 4 == 1) {
                            Model model = new Model();
                            model.setId(format("line%d", count / 4));
                            sAdapter.add(model, sAdapter.getItemCount() - 1);
                        }
                    }
                    if (count / 4 > 1) {
                        isScrollEnabled = true;
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) shareItemsLayer.getLayoutParams();
                        params.height = getDimension(R.dimen.ui_static_dp_220);
                        shareItemsLayer.setLayoutParams(params);
                        shareDots.setVisibility(View.VISIBLE);
                    } else {
                        isScrollEnabled = false;
                        shareItemsLayer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        shareDots.setVisibility(View.INVISIBLE);
                    }
                }
            }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_BOTTOM);
        }
        shareDialogHelper.show();
    }

    private OnViewHolderClickListener shareItemClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            ShareItem item = (ShareItem) sAdapter.get(index);
            switch (item.getIndex()) {
                case 0:
                    shareToWeiXinSession();
                    break;
                case 1:
                    shareToWeiXinTimeline();
                    break;
                case 2:
                    shareToQQ();
                    break;
                case 3:
                    shareToQZone();
                    break;
                case 4:
                    break;
                case 5:
                    shareToDelete();
                    break;
                case 6:
                    shareToForward();
                    break;
                case 7:
                    shareToRecommend();
                    break;
                case 8:
                    shareToRecommended();
                    break;
                case 9:
                    shareToAward();
                    break;
                case 10:
                    shareToAwarded();
                    break;
                case 11:
                    shareToClassify();
                    break;
                case 12:
                    shareToReply();
                    break;
                case 13:
                    transform();
                    break;
                case 14:
                    archiveSetting();
                    break;
            }
            shareDialogHelper.dismiss();
        }
    };

    private class ShareItemsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_ITEM = 0, VT_LINE = 1;
        private int width;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            gotSize();
            if (viewType == VT_ITEM) {
                ShareItemViewHolder sivh = new ShareItemViewHolder(itemView, BaseFragment.this);
                sivh.addOnViewHolderClickListener(shareItemClickListener);
                return sivh.setSize(width);
            }
            return new TextViewHolder(itemView, BaseFragment.this);
        }

        private void gotSize() {
            if (width <= 0) {
                int w = getScreenWidth() - (getDimension(R.dimen.ui_base_dimen_margin_padding) * 2);
                width = w / 4;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return (get(position) instanceof ShareItem) ? VT_ITEM : VT_LINE;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_ITEM ? R.layout.holder_view_share_item : R.layout.tool_view_half_line_horizontal;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ShareItemViewHolder) {
                ((ShareItemViewHolder) holder).showContent((ShareItem) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }

    /**
     * 关闭分享对话框
     */
    protected void dismissShareDialog() {
        if (null != shareDialogHelper) {
            shareDialogHelper.dismiss();
        }
    }

    protected void shareToApp() {
        // 打开群聊列表选择要分享到的群聊
    }

    protected void shareToQQ() {
        ShareToQQ.shareToQQ(ShareToQQ.TO_QQ, Activity(), mShareInfo.getTitle(), Utils.clearHtml(mShareInfo.getDescription()), mShareInfo.getTargetPath(), mShareInfo.getImageUrl(), null);
    }

    protected void shareToQZone() {
        ArrayList<String> img = new ArrayList<>();
        if (!isEmpty(mShareInfo.getImageUrl())) {
            img.add(mShareInfo.getImageUrl());
        } else {
            // 如果没有图片，则加入默认的图片
            img.add(ICON_RECT_256x256);
        }
        ShareToQQ.shareToQQ(ShareToQQ.TO_QZONE, Activity(), mShareInfo.getTitle(), Utils.clearHtml(mShareInfo.getDescription()), mShareInfo.getTargetPath(), mShareInfo.getImageUrl(), img);
    }

    protected void shareToWeiXinSession() {
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_SESSION, mShareInfo.getTitle(), Utils.clearHtml(mShareInfo.getDescription()), mShareInfo.getTargetPath(), mShareInfo.getImageUrl());
    }

    protected void shareToWeiXinTimeline() {
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_TIMELINE, mShareInfo.getTitle(), Utils.clearHtml(mShareInfo.getDescription()), mShareInfo.getTargetPath(), mShareInfo.getImageUrl());
    }

    protected void shareToWeiBo() {
        ShareToWeiBo.init(Activity()).share(mShareInfo.getTitle(), Utils.clearHtml(mShareInfo.getDescription()), mShareInfo.getTargetPath(), mShareInfo.getImageUrl());
    }

    protected void shareToLink() {
        ClipboardHelper.copyToClipboard(Activity(), mShareInfo.getTargetPath());
        ToastHelper.helper().showMsg(R.string.ui_base_share_text_share_to_link_copied);
    }

    /**
     * 删除档案
     */
    protected void shareToDelete() {
    }

    /**
     * 转发档案到别的组织
     */
    protected void shareToForward() {
        GroupPickerFragment.open(BaseFragment.this, "", true);
    }

    /**
     * 推荐档案到首页
     */
    protected void shareToRecommend() {
    }

    /**
     * 取消首页已推荐的档案
     */
    protected void shareToRecommended() {
    }

    /**
     * 设置档案获奖
     */
    protected void shareToAward() {
    }

    /**
     * 取消档案获奖
     */
    protected void shareToAwarded() {
    }

    /**
     * 档案归类
     */
    protected void shareToClassify() {
    }

    /**
     * 档案回复
     */
    protected void shareToReply() {
    }

    /**
     * 下发活动
     */
    protected void transform() {
    }

    /**
     * 打开设置窗口
     */
    protected void archiveSetting() {
    }

    /**
     * 未读消息数量
     */
    public static String formatUnread(int num) {
        return format("%s", (num > 99 ? "99+" : (num <= 0 ? "" : num)));
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss的时间格式化成yyyy年mm月dd日
     */
    public String formatDate(String date) {
        return formatDate(date, R.string.ui_base_text_date_format_chs);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss的时间格式化成指定的格式内容
     */
    public String formatDate(String date, int toFormat) {
        return formatDate(date, StringHelper.getString(toFormat));
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss的时间格式化成指定的格式内容
     */
    public String formatDate(String date, String toFormat) {
        if (isEmpty(date)) return "";
        // 增加秒的格式化内容，避免ios不同的格式化公式
        String ss = (date.length() < 19) ? ":00" : "";
        return Utils.format(date + ss, StringHelper.getString(R.string.ui_base_text_date_time_format), toFormat);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss的时间格式化成yyyy年mm月dd日 hh时mm分
     */
    public String formatDateTime(String date) {
        return formatDate(date, R.string.ui_base_text_date_time_format_chs_min);
    }

    /**
     * 格式化xxx时间之前
     */
    public String formatTimeAgo(String time) {
        if (isEmpty(time)) return "";
        int len = time.length();
        // 增加秒的格式化内容，避免ios不同的格式化公式
        String ss = (len <= 10) ? " 00:00:10" : ((time.length() < 19) ? ":00" : "");
        return Utils.formatTimeAgo(StringHelper.getString(R.string.ui_base_text_date_time_format), time + ss);
    }

    /**
     * 子类重载此方法判断是否正处于编辑状态
     */
    protected boolean checkStillEditing() {
        return false;
    }

    /**
     * 询问用户是否放弃编辑
     */
    protected void warningStillInEditing() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                finish();
                return true;
            }
        }).setTitleText(R.string.ui_base_text_still_editing).setConfirmText(R.string.ui_base_text_yes).show();
    }

    protected boolean onBackKeyPressed() {
        if (mCurPopupWindow != null && mCurPopupWindow.isShowing()) {
            mCurPopupWindow.dismiss();
            return true;
        }
        boolean stillEditing = checkStillEditing();
        if (stillEditing) {
            warningStillInEditing();
        }
        return stillEditing;
    }

    private PopupWindow mCurPopupWindow;

    private boolean dismissPopupWindow() {
        if (null != mCurPopupWindow && mCurPopupWindow.isShowing()) {
            mCurPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    public void showTooltip(View anchorView, String text, View.OnClickListener onClickListener) {
        if (!dismissPopupWindow()) {
            mCurPopupWindow = TooltipHelper.showTooltip(anchorView, text, onClickListener);
        }
    }
//
//    public void showTooltip(View anchorView, View.OnClickListener onClickListener) {
//        if (!dismissPopupWindow()) {
//            mCurPopupWindow = TooltipHelper.showTooltip(anchorView, onClickListener);
//        }
//    }

    public void showTooltip(View anchorView, int viewId, boolean belowAnchor, int arrowType, View.OnClickListener clickListener) {
        if (!dismissPopupWindow()) {
            mCurPopupWindow = TooltipHelper.showTooltip(anchorView, viewId, belowAnchor, arrowType, clickListener);
        }
    }
}
