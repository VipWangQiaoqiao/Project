package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Question;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos on 16/8/17.
 */
public class UserQuestionAdapter extends BaseRecyclerAdapter<Question> {

    public UserQuestionAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_item_question, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, Question item, int position) {
        ViewHolder holder = (ViewHolder) h;
        holder.mViewPortrait.setup(item.getAuthorId(), item.getAuthor(), item.getAuthorPortrait());
        holder.mViewTitle.setText(item.getTitle());
        holder.mViewContent.setText(item.getBody());
        String nick = item.getAuthor();
        holder.mViewHistory.setText(nick.length() > 9
                ? nick.substring(0, 9)
                : nick + " " + StringUtils.formatSomeAgo(item.getPubDate()));
        holder.mViewInfoCmm.setText(String.valueOf(item.getCommentCount()));
        holder.mViewInfoVisual.setText(String.valueOf(item.getViewCount()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_ques_item_icon)
        PortraitView mViewPortrait;
        @Bind(R.id.tv_ques_item_title)
        TextView mViewTitle;
        @Bind(R.id.tv_ques_item_content)
        TextView mViewContent;
        @Bind(R.id.tv_ques_item_history)
        TextView mViewHistory;
        @Bind(R.id.tv_info_view)
        TextView mViewInfoVisual;
        @Bind(R.id.tv_info_comment)
        TextView mViewInfoCmm;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
