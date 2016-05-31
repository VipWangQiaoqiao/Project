package net.oschina.app.fragment.general;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.QuesActionAdapter;
import net.oschina.app.adapter.general.QuestionAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.question.Question;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.fragment.base.BaseListFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * 技术问答界面
 */
public class QuestionFragment extends BaseListFragment<Question> {

    private static final String TAG = "QuestionFragment";
    private GridView quesGridView = null;
    private View headView;
    private int catalog = 1;

    private static final String QUES_ASK = "ques_ask";
    private static final String QUES_SHARE = "ques_share";
    private static final String QUES_COMPOSITE = "ques_composite";
    private static final String QUES_PROFESSION = "ques_profession";
    private static final String QUES_WEBSITE = "ques_website";

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_question_header, null, false);
        quesGridView = (GridView) headView.findViewById(R.id.gv_ques);

        final int[] positions = {1, 0, 0, 0, 0};
        final QuesActionAdapter quesActionAdapter = new QuesActionAdapter(getActivity(), positions);
        quesGridView.setAdapter(quesActionAdapter);
        quesGridView.setItemChecked(0, true);
        quesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                catalog = (position + 1);

                if (!mIsRefresh) {
                    mIsRefresh = true;
                }
                requestData();
                positions[position] = 1;
                for (int i = 0; i < positions.length; i++) {
                    if (i != position) {
                        if (positions[i] != 0) {
                            positions[i] = 0;
                            Log.d(TAG, "postions=" + positions[i] + " ");
                        }
                    }
                }
                quesActionAdapter.notifyDataSetChanged();
            }
        });
        mListView.addHeaderView(headView);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onRequestError(int code) {
        super.onRequestError(code);
        mExeService.submit(new Runnable() {
            @Override
            public void run() {
                mBeam = (PageBean<Question>) CacheManager.readObject(getActivity(), CACHE_NAME);
                Log.d(TAG, "onRequestError: --->catalog=" + catalog + " mbean=" + mBeam.toString());
                if (mBeam != null) {
                    mAdapter.clear();
                    mAdapter.addItem(mBeam.getItems());
                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected BaseListAdapter<Question> getListAdapter() {
        return new QuestionAdapter(this);
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getQuestionList(catalog, mIsRefresh ? mBeam.getPrevPageToken() : mBeam.getNextPageToken(), mHandler);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Question>>>() {
        }.getType();
    }

    @Override
    protected void setListData(ResultBean<PageBean<Question>> resultBean) {
        verifyCacheType();
        super.setListData(resultBean);
    }

    /**
     * verify cache type
     */
    private void verifyCacheType() {

        switch (catalog) {
            case 1:
                CACHE_NAME = QUES_ASK;
                break;
            case 2:
                CACHE_NAME = QUES_SHARE;
                break;
            case 3:
                CACHE_NAME = QUES_COMPOSITE;
                break;
            case 4:
                CACHE_NAME = QUES_PROFESSION;
                break;
            case 5:
                CACHE_NAME = QUES_WEBSITE;
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // super.onItemClick(parent, view, position, id);
        Question question = mAdapter.getItem(position - 1);
        if (question != null) {
            UIHelper.showPostDetail(getActivity(), (int) question.getId(),
                    question.getCommentCount());
        }
    }
}
