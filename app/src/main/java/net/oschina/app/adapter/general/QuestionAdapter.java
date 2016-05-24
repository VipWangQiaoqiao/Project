package net.oschina.app.adapter.general;

import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.question.Question;

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

    }

    @Override
    protected int getLayoutId(int position, Question item) {
        return 0;
    }
}
