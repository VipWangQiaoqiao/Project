package net.oschina.app.improve.detail.general;

import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailFragment;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class NewsDetailFragment extends DetailFragment {
    @Bind(R.id.tv_title)
    TextView mTextTitle;

    @Bind(R.id.tv_info_comment)
    TextView mTextComCount;

    @Bind(R.id.tv_info_view)
    TextView mTextViewCount;

    public static NewsDetailFragment newInstance() {
        NewsDetailFragment fragment = new NewsDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail_v2;
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mTextTitle.setText(bean.getTitle());
        mTextComCount.setText(String.valueOf(bean.getStatistics().getComment()));
        mTextViewCount.setText(String.valueOf(bean.getStatistics().getView()));
    }
}
