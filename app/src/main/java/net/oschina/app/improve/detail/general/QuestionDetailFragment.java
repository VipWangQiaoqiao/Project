package net.oschina.app.improve.detail.general;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.util.StringUtils;
import net.oschina.common.widget.FlowLayout;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class QuestionDetailFragment extends DetailFragment {
    @Bind(R.id.tv_title)
    TextView mTextTitle;
    @Bind(R.id.tv_author)
    TextView mTextAuthor;
    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;
    @Bind(R.id.tv_info_view)
    TextView mTextViewCount;
    @Bind(R.id.tv_info_comment)
    TextView mTextCommentCount;
    @Bind(R.id.fl_lab)
    FlowLayout mFlowLayout;

    public static QuestionDetailFragment newInstance() {
        return new QuestionDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_question_detail_v2;
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_QUESTION;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mTextTitle.setText(bean.getTitle());
        mTextAuthor.setText(bean.getAuthor().getName());
        mTextPubDate.setText(StringUtils.formatYearMonthDay(bean.getPubDate()));
        mTextCommentCount.setText(String.valueOf(bean.getStatistics().getComment()));
        mTextViewCount.setText(String.valueOf(bean.getStatistics().getView()));
        for (String tag : bean.getTags()) {
            TextView tvTag = (TextView) getActivity().getLayoutInflater().inflate(R.layout.flowlayout_item, mFlowLayout, false);
            if (!TextUtils.isEmpty(tag))
                tvTag.setText(tag);
            mFlowLayout.addView(tvTag);
        }
    }

    @Override
    protected int getCommentOrder() {
        return OSChinaApi.COMMENT_NEW_ORDER;
    }
}
