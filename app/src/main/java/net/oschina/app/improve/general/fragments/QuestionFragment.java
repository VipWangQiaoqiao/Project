package net.oschina.app.improve.general.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.bean.Question;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.QuestionDetailActivity;
import net.oschina.app.improve.general.adapter.QuesActionAdapter;
import net.oschina.app.improve.general.adapter.QuestionAdapter;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 技术问答界面
 * <p/>
 * desc
 */
public class QuestionFragment extends BaseGeneralListFragment<Question> {

    public static final String QUES_ASK = "ques_ask";
    public static final String QUES_SHARE = "ques_share";
    public static final String QUES_COMPOSITE = "ques_composite";
    public static final String QUES_PROFESSION = "ques_profession";
    public static final String QUES_WEBSITE = "ques_website";


    public static final String QUES_TYPE_KEY = "blog_type_key";
    public static final int QUES_ASK_TYPE = 3;//3:技术问答
    public static final int QUES_SHARE_TYPE = 4;  // 4:技术分享
    public static final int QUES_COMPOSITE_TYPE = 5;   // 5:行业杂烩
    public static final int QUES_PROFESSION_TYPE = 6;   // 6:职业生涯
    public static final int QUES_WEBSITE_TYPE = 7;// 7:站务建议

    private int catalog = 1;
    private QuesActionAdapter quesActionAdapter;
    private int[] positions = {1, 0, 0, 0, 0};

    public static Fragment instantiate(Context context, int subtype){
        Fragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(QUES_TYPE_KEY, subtype);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        if (bundle == null) return;
        int blogType = bundle.getInt(QUES_TYPE_KEY, 0);
        switch (blogType) {
            case QUES_ASK_TYPE:
                catalog = 1;
                break;
            case QUES_SHARE_TYPE:
                catalog = 2;
                break;
            case QUES_COMPOSITE_TYPE:
                catalog = 3;
                break;
            case QUES_PROFESSION_TYPE:
                catalog = 4;
                break;
            case QUES_WEBSITE_TYPE:
                catalog = 5;
                break;
            default:
                catalog = 1;
                break;
        }


    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        @SuppressLint("InflateParams")
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_question_header, null, false);
        headView.setVisibility(View.GONE);
        GridView quesGridView = (GridView) headView.findViewById(R.id.gv_ques);
        quesGridView.setVisibility(View.GONE);
        quesActionAdapter = new QuesActionAdapter(getActivity(), positions);
        quesGridView.setAdapter(quesActionAdapter);
        quesGridView.setItemChecked(0, true);
        quesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                catalog = (position + 1);
                ((QuestionAdapter) mAdapter).setActionPosition(position + 1);
                if (!mIsRefresh) {
                    mIsRefresh = true;
                }
                updateAction(position);
                if (positions[position] == 1) {
                    requestEventDispatcher();
                }
            }
        });
        mListView.addHeaderView(headView);

    }
    /**
     * According to the distribution network is events
     */
    private void requestEventDispatcher() {
        if (TDevice.hasInternet()) {
            mRefreshLayout.setRefreshing(true);
            onRefreshing();
            //requestData();
        } else {
            requestLocalCache();
        }
    }


    /**
     * notify action data
     *
     * @param position position
     */
    private void updateAction(int position) {
        int len = positions.length;
        for (int i = 0; i < len; i++) {
            if (i != position) {
                positions[i] = 0;
            } else {
                positions[i] = 1;
            }
        }
        quesActionAdapter.notifyDataSetChanged();
    }

    /**
     * request local cache
     */
    @SuppressWarnings("unchecked")
    private void requestLocalCache() {
        verifyCacheType();
        mBean = (PageBean<Question>) CacheManager.readObject(getActivity(), CACHE_NAME);
        if (mBean != null) {
            mAdapter.clear();
            mAdapter.addItem(mBean.getItems());
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.setCanLoadMore();
        } else {
            mBean = new PageBean<>();
            mBean.setItems(new ArrayList<Question>());
            onRefreshing();
        }
    }

    @Override
    protected void initData() {
        CACHE_NAME = QUES_ASK;
        super.initData();
        if (!mIsRefresh) {
            mIsRefresh = true;
//            mBean.setPrevPageToken(null);
//            mBean.setNextPageToken(null);
        }
        requestEventDispatcher();
    }

    @Override
    protected void onRequestError(int code) {
        super.onRequestError(code);
        requestLocalCache();
    }

    @Override
    protected BaseListAdapter<Question> getListAdapter() {
        return new QuestionAdapter(this);
    }

    @Override
    protected void requestData() {
        super.requestData();
        verifyCacheType();
        OSChinaApi.getQuestionList(catalog, mIsRefresh ? /*(mBean != null ? mBean.getPrevPageToken() : null)*/null
                : (mBean != null ? mBean.getNextPageToken() : null), mHandler);
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
         super.onItemClick(parent, view, position, id);
        Question question = mAdapter.getItem(position - 1);
        if (question != null) {
            // UIUtil.showPostDetail(getActivity(), (int) question.getId(), question.getCommentCount());
            QuestionDetailActivity.show(getActivity(), question.getId());
            TextView title = (TextView) view.findViewById(R.id.tv_ques_item_title);
            TextView content = (TextView) view.findViewById(R.id.tv_ques_item_content);
            updateTextColor(title, content);
            verifyCacheType();
            saveToReadedList(CACHE_NAME, String.valueOf(question.getId()));
        }
    }
}
