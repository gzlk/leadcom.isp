package com.gzlk.android.isp.holder.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>提供可搜索的ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 00:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 00:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SearchableViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_searchable_input)
    private CorneredEditText searchInput;
    @ViewId(R.id.ui_holder_view_searchable_icon)
    private CustomTextView searchIcon;

    public SearchableViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                int icon = len > 0 ? R.string.ui_icon_clear : R.string.ui_icon_search;
                searchIcon.setText(icon);
                if (null != onSearchingListener) {
                    onSearchingListener.onSearching(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 搜索
                    String text = searchInput.getText().toString();
                    if (null != onSearchingListener) {
                        onSearchingListener.onSearching(text);
                    }
                    Utils.hidingInputBoard(searchInput);
                    return true;
                }
                return false;
            }
        });
    }

    @Click({R.id.ui_holder_view_searchable_icon})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_searchable_icon:
                searchInput.setText("");
                break;
        }
    }

    private OnSearchingListener onSearchingListener;

    public void setOnSearchingListener(OnSearchingListener l) {
        onSearchingListener = l;
    }

    public interface OnSearchingListener {
        void onSearching(String text);
    }
}
