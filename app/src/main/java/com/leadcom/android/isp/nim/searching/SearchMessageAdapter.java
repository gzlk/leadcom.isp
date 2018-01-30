package com.leadcom.android.isp.nim.searching;

import android.content.Context;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.impl.cache.NimUserInfoCache;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.List;

public class SearchMessageAdapter extends BaseAdapter {

    private Context context;
    private List<IMMessage> messages;
    private String keyword;

    public SearchMessageAdapter(Context context, List<IMMessage> messages) {
        this.context = context;
        this.messages = messages;

        this.keyword = "";
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextSearchResultViewHolder holder;
        if (convertView != null) {
            holder = (TextSearchResultViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.nim_activity_search_list_item, parent, false);
            holder = new TextSearchResultViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.refresh(messages.get(position));

        return convertView;
    }

    private class TextSearchResultViewHolder {
        private HeadImageView imgHead;
        private TextView tvNickname;
        private TextView tvMessage;
        private TextView tvDatetime;
        private ImageView imgMsgStatus;


        public TextSearchResultViewHolder(View view) {
            this.imgHead = (HeadImageView) view.findViewById(R.id.img_head);

            this.tvNickname = (TextView) view.findViewById(R.id.tv_nick_name);
            this.tvMessage = (TextView) view.findViewById(R.id.tv_message);
            this.tvDatetime = (TextView) view.findViewById(R.id.tv_date_time);
            this.imgMsgStatus = (ImageView) view.findViewById(R.id.img_msg_status);
        }

        public void refresh(IMMessage message) {
            imgHead.loadBuddyAvatar(message.getFromAccount());

            refreshNickname(message);
            refreshContent(message);
            refreshTime(message);
        }

        private void refreshNickname(IMMessage message) {
            int labelWidth = ScreenUtil.screenWidth;
            // 减去固定的头像和时间宽度
            labelWidth -= ScreenUtil.dip2px(70 + 70);
            tvNickname.setMaxWidth(labelWidth);
            if (message.getSessionType() == SessionTypeEnum.Team) {
                TeamMember member = TeamDataCache.getInstance().getTeamMember(message.getSessionId(), message.getFromAccount());
                tvNickname.setText(null == member ? "[未知成员]" : member.getTeamNick());
            } else {
                UserInfo user = NimUserInfoCache.getInstance().getUserInfo(message.getFromAccount());
                tvNickname.setText(null == user ? "[未知用户]" : user.getName());
            }
        }

        private void refreshContent(IMMessage message) {
            MoonUtil.identifyFaceExpressionAndTags(context, tvMessage, message.getContent(), ImageSpan.ALIGN_BOTTOM, 0.45f);
//            SpanUtil.makeKeywordSpan(context, tvMessage, keyword);

            switch (message.getStatus()) {
                case fail:
                    imgMsgStatus.setImageResource(R.drawable.nim_g_ic_failed_small);
                    imgMsgStatus.setVisibility(View.VISIBLE);
                    break;
                default:
                    imgMsgStatus.setVisibility(View.GONE);
                    break;
            }
        }

        private void refreshTime(IMMessage messageHistory) {
            String timeString = TimeUtil.getTimeShowString(messageHistory.getTime(), true);
            tvDatetime.setText(timeString);
        }
    }
}
