package net.oschina.app.improve.user.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.base.fragments.BaseListFragment;
import net.oschina.app.improve.bean.Question;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.QuestionDetailActivity;
import net.oschina.app.improve.general.adapter.QuestionAdapter;

import java.lang.reflect.Type;

/**
 * created by fei  on 2016/8/16.
 * desc:user question list module
 */
public class UserQuestionFragment extends BaseListFragment<Question> {

    public static final String HISTORY_MY_QUESTION = "history_my_question";
    public static final String AUTHOR_ID = "author_id";
    private int userId;

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getInt(AUTHOR_ID, 0);
        // mIsRefresh = false;
    }


    /**
     * instantiate fragment
     *
     * @param authorId authorId
     * @return fragment
     */
    public static UserQuestionFragment instantiate(int authorId) {

        UserQuestionFragment fragment = new UserQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(AUTHOR_ID, authorId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected void requestData() {
        super.requestData();

        OSChinaApi.getUserQuestionList((mIsRefresh ? (mBean != null ? mBean.getPrevPageToken() : null) :
                        (mBean != null ? mBean.getNextPageToken() : null))
                , userId, mHandler);

    }

    @Override
    protected BaseListAdapter<Question> getListAdapter() {
        QuestionAdapter questionAdapter = new QuestionAdapter(this);
        questionAdapter.setUserQuestion(true);
        return questionAdapter;
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Question>>>() {
        }.getType();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Question question = mAdapter.getItem(position);
        if (question != null) {
            QuestionDetailActivity.show(getActivity(), question.getId());
            TextView title = (TextView) view.findViewById(R.id.tv_item_blog_title);
            TextView content = (TextView) view.findViewById(R.id.tv_item_blog_body);

            updateTextColor(title, content);
            saveToReadedList(UserQuestionFragment.HISTORY_MY_QUESTION, question.getId() + "");
        }
    }

}
