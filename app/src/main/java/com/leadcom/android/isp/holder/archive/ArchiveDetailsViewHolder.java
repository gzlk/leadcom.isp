package com.leadcom.android.isp.holder.archive;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/27 14:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/27 14:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_details_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_archive_details_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_archive_details_author)
    private TextView authorView;
    @ViewId(R.id.ui_holder_view_archive_details_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_holder_view_archive_details_content)
    private WebView contentView;

    private int width, height;

    public ArchiveDetailsViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        contentView.getSettings().setDefaultTextEncodingName("UTF-8");
        resetCoverSize();
    }

    public void onFragmentDestroy() {
        contentView.destroy();
    }

    private void resetCoverSize() {
        width = fragment().getScreenWidth() - getDimension(R.dimen.ui_base_dimen_margin_padding) * 2;
        height = width / 2;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) coverView.getLayoutParams();
        params.height = height;
        coverView.setLayoutParams(params);
    }

    public void showContent(Archive archive) {
        titleView.setText(archive.getTitle());
        timeView.setText(fragment().formatDate(archive.getCreateDate(), R.string.ui_base_text_date_time_format));
        String source = (isEmpty(archive.getSource()) || archive.getSource().equals(archive.getUserName())) ? "" : StringHelper.getString(R.string.ui_text_archive_details_source_text, archive.getSource());
        authorView.setText(StringHelper.getString(R.string.ui_text_archive_details_author_text, archive.getUserName(), source));
        String cover = archive.getCover();
//        if (isEmpty(cover)) {
//            Random random = new Random(10);
//            cover = "drawable://" + (random.nextInt() % 2 == 0 ? R.drawable.img_activity_cover_1 : R.drawable.img_activity_cover_2);
//        }
        coverView.displayImage(cover, width, height, false, false);
        coverView.setVisibility(isEmpty(cover) ? View.GONE : View.VISIBLE);
        String content = archive.getContent();
        contentView.setVisibility((isEmpty(content) || content.equals("null")) ? View.GONE : View.VISIBLE);
        content = isEmpty(content) ? "" : Utils.clearStyleEqualsXXX(content);
        contentView.loadData(StringHelper.getString(R.string.ui_text_archive_details_content_html, content).replace("width: 100per", "width: 100%"), "text/html; charset=UTF-8", null);
        //bottomLine.setVisibility(archive.getCmtNum() > 0 ? View.VISIBLE : View.GONE);
    }
}
