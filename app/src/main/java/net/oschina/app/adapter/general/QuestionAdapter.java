package net.oschina.app.adapter.general;

import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.question.Question;
import net.oschina.app.util.StringUtils;

/**
 * Created by fei on 2016/5/24.
 * desc:
 */
public class QuestionAdapter extends BaseListAdapter<Question> {


    public QuestionAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Question item, int position) {
        String authorPortrait = item.getAuthorPortrait();
        if (authorPortrait != null) {
            vh.setImageForNet(R.id.iv_ques_item_icon, authorPortrait.trim(), R.drawable.widget_dface);
        }
        TextView title = vh.getView(R.id.tv_ques_item_title);
        String title1 = item.getTitle();
        if (title1 != null) {
            title.setText(title1.trim());
        }
        TextView content = vh.getView(R.id.tv_ques_item_content);
        String body = item.getBody();
        if (body != null) {
            body = body.trim();
            if (body.length() > 0 && !body.equals("")) {
                content.setText(body);
                content.setVisibility(View.VISIBLE);
            } else {
                content.setVisibility(View.GONE);
            }
        }
        TextView history = vh.getView(R.id.tv_ques_item_history);
        String author = item.getAuthor();
        if (author != null) {
            author = author.trim();
            history.setText((author.length() > 9 ? author.substring(0, 9) : author.trim()) + "  " + StringUtils.friendly_time(item.getPubDate().trim()));
        }
        TextView see = vh.getView(R.id.tv_info_view);
        see.setText(item.getViewCount() + "");
        TextView answer = vh.getView(R.id.tv_info_comment);
        answer.setText(item.getCommentCount() + "");
    }

    @Override
    protected int getLayoutId(int position, Question item) {
        return R.layout.fragment_item_question;
    }
}
