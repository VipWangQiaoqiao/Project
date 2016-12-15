package net.oschina.app.improve.detail.general;

import net.oschina.app.R;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class QuestionDetailFragment extends DetailFragment {

    public static QuestionDetailFragment newInstance() {
        QuestionDetailFragment fragment = new QuestionDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_question_detail_v2;
    }
}
