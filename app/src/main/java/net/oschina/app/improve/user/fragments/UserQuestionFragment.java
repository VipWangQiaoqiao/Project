package net.oschina.app.improve.user.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Question;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.user.adapter.UserQuestionAdapter;

import java.lang.reflect.Type;

/**
 * @author thanatosx
 */
public class UserQuestionFragment extends BaseRecyclerViewFragment<Question> {

    public static final String HISTORY_MY_QUESTION = "history_my_question";
    public static final String BUNDLE_KEY_AUTHOR_ID = "author_id";
    private long userId;

    public static Fragment instantiate(int authorId) {
        UserQuestionFragment fragment = new UserQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_AUTHOR_ID, authorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getLong(BUNDLE_KEY_AUTHOR_ID, 0);
    }

    @Override
    protected void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getUserQuestionList(token, userId, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<Question> getRecyclerAdapter() {
        return new UserQuestionAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Question>>>() {
        }.getType();
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Question question = mAdapter.getItem(position);
        if (question == null)
            return;
        QuestionDetailActivity.show(getActivity(), question.getId());
    }
}
