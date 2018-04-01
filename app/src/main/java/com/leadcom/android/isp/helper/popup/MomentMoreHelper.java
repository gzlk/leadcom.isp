package com.leadcom.android.isp.helper.popup;

import android.view.View;

import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;

/**
 * <b>功能描述：</b>动态的更多按钮对话框<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/31 23:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MomentMoreHelper {

    public static MomentMoreHelper helper() {
        return new MomentMoreHelper();
    }

    private int layout = R.layout.popup_dialog_moment_details;
    private BaseFragment fragment;
    private DialogHelper.OnEventHandlerListener eventHandlerListener;
    private DialogHelper dialogHelper;
    private String privacyText = StringHelper.getString(R.string.ui_text_moment_details_button_privacy);
    private String collectText = StringHelper.getString(R.string.ui_text_moment_details_button_favorite);

    /**
     * 几个按钮的默认显示状态
     */
    private boolean showPrivacy = false, showFavorite = false, showShare = false, showSave = false, showDelete = false;

    public MomentMoreHelper init(BaseFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    public MomentMoreHelper setLayout(int layout) {
        this.layout = layout;
        return this;
    }

    public MomentMoreHelper setOnEventHandlerListener(DialogHelper.OnEventHandlerListener listener) {
        this.eventHandlerListener = listener;
        return this;
    }

    public MomentMoreHelper showPrivacy(boolean shown) {
        showPrivacy = shown;
        return this;
    }

    public MomentMoreHelper showFavorite(boolean shown) {
        showFavorite = shown;
        return this;
    }

    public MomentMoreHelper showShare(boolean shown) {
        showShare = shown;
        return this;
    }

    public MomentMoreHelper showSave(boolean shown) {
        showSave = shown;
        return this;
    }

    public MomentMoreHelper showDelete(boolean shown) {
        showDelete = shown;
        return this;
    }

    public MomentMoreHelper setPrivacyText(int res) {
        privacyText = StringHelper.getString(res);
        return this;
    }

    public MomentMoreHelper setPrivacyText(String text) {
        privacyText = text;
        return this;
    }

    public MomentMoreHelper setCollectText(int res) {
        collectText = StringHelper.getString(res);
        return this;
    }

    public MomentMoreHelper setCollectText(String text) {
        collectText = text;
        return this;
    }

    private View dialogView;
    private CorneredButton privacy, favorite, share, save, delete;

    public void show() {
        if (null == dialogHelper) {
            dialogHelper = DialogHelper.init(fragment.Activity()).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    return true;
                }
            });
        }
        dialogHelper.addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = View.inflate(fragment.Activity(), layout, null);
                    privacy = dialogView.findViewById(R.id.ui_dialog_moment_details_button_privacy);
                    favorite = dialogView.findViewById(R.id.ui_dialog_moment_details_button_favorite);
                    share = dialogView.findViewById(R.id.ui_dialog_moment_details_button_share);
                    save = dialogView.findViewById(R.id.ui_dialog_moment_details_button_save);
                    delete = dialogView.findViewById(R.id.ui_dialog_moment_details_button_delete);
                }
                return dialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                privacy.setText(privacyText);
                favorite.setText(collectText);
                privacy.setVisibility(showPrivacy ? View.VISIBLE : View.GONE);
                favorite.setVisibility(showFavorite ? View.VISIBLE : View.GONE);
                share.setVisibility(showShare ? View.VISIBLE : View.GONE);
                save.setVisibility(showSave ? View.VISIBLE : View.GONE);
                delete.setVisibility(showDelete ? View.VISIBLE : View.GONE);
            }
        }).addOnEventHandlerListener(eventHandlerListener).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true).show();
    }
}
