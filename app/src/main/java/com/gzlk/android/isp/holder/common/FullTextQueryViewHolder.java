package com.gzlk.android.isp.holder.common;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.SimpleUser;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/11 23:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/11 23:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FullTextQueryViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_full_text_query_item_image_oval)
    private ImageDisplayer imageOval;
    @ViewId(R.id.ui_holder_view_full_text_query_item_image_round)
    private ImageDisplayer imageRound;
    @ViewId(R.id.ui_holder_view_full_text_query_item_text)
    private TextView textView;

    private int imageSize;
    private String searchingText;

    public FullTextQueryViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size);
        imageOval.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                rootClick();
            }
        });
        imageRound.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                rootClick();
            }
        });
    }

    public void setSearchingText(String text) {
        searchingText = text;
    }

    public void showContent(Model model) {
        if (model instanceof SimpleUser) {
            showContent((SimpleUser) model);
        } else if (model instanceof Organization) {
            showContent((Organization) model);
        } else if (model instanceof Activity) {
            showContent((Activity) model);
        } else if (model instanceof Archive) {
            showContent((Archive) model);
        }
    }

    private void showContent(SimpleUser user) {
        imageOval.setVisibility(View.VISIBLE);
        imageRound.setVisibility(View.GONE);
        imageOval.displayImage(user.getHeadPhoto(), imageSize, false, false);
        String text = user.getUserName();
        text = getSearchingText(text, searchingText);
        textView.setText(Html.fromHtml(text));
    }

    private void showContent(Organization group) {
        imageOval.setVisibility(View.GONE);
        imageRound.setVisibility(View.VISIBLE);
        imageRound.displayImage(group.getLogo(), imageSize, false, false);
        String text = group.getName();
        text = getSearchingText(text, searchingText);
        textView.setText(Html.fromHtml(text));
    }

    private void showContent(Activity activity) {
        imageOval.setVisibility(View.GONE);
        imageRound.setVisibility(View.VISIBLE);
        imageRound.displayImage(activity.getCover(), imageSize, false, false);
        String text = activity.getTitle();
        text = getSearchingText(text, searchingText);
        textView.setText(Html.fromHtml(text));
    }

    private void showContent(Archive archive) {
        imageOval.setVisibility(View.GONE);
        imageRound.setVisibility(View.VISIBLE);
        imageRound.displayImage(archive.getCover(), imageSize, false, false);
        String text = archive.getTitle();
        text = getSearchingText(text, searchingText);
        textView.setText(Html.fromHtml(text));
    }

    @Click({R.id.ui_holder_view_full_text_query_item_root})
    private void elementClick(View view) {
        rootClick();
    }

    private void rootClick() {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
