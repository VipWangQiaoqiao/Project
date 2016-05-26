package net.oschina.app.adapter.general;

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

        vh.setPortrait(R.id.iv_ques_item_icon, item.getAuthorPortrait());
        TextView title = vh.getView(R.id.tv_ques_item_title);
        title.setText(item.getTitle());
        TextView content = vh.getView(R.id.tv_ques_item_content);
        content.setText(item.getBody());
        TextView history = vh.getView(R.id.tv_ques_item_history);
        history.setText(item.getAuthor().length() > 9 ? item.getAuthor().substring(0, 9) : item.getAuthor() + "\t" + StringUtils.friendly_time(item.getPubDate()));
        TextView see = vh.getView(R.id.tv_ques_item_see);
        see.setText(item.getViewCount() + "");
        TextView answer = vh.getView(R.id.tv_ques_item_comment);
        answer.setText(item.getCommentCount() + "");
    }

    @Override
    protected int getLayoutId(int position, Question item) {
        return R.layout.fragment_item_question;
    }
}
