package net.oschina.app.improve.main.discover;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.shake.ShakeNews;
import net.oschina.app.util.StringUtils;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/11.
 */

public class ShakeNewsFragment extends BaseSensorFragment<ShakeNews> {

    private ImageView iv_news;
    private TextView tv_news_name, tv_time;

    public static ShakeNewsFragment newInstance() {
        ShakeNewsFragment fragment = new ShakeNewsFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shake_news;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mShakeView = mInflater.inflate(R.layout.view_news, null);
        iv_news = (ImageView) mShakeView.findViewById(R.id.iv_news);
        tv_news_name = (TextView) mShakeView.findViewById(R.id.tv_news_name);
        tv_time = (TextView) mShakeView.findViewById(R.id.tv_time);
    }

    @Override
    public void onShake() {
        OSChinaApi.getShakeNews(mHandler);
    }

    @Override
    protected void initShakeView() {
        ShakeNews news = mBean.getResult();
        getImgLoader().load(news.getImg()).placeholder(R.mipmap.ic_split_graph).into(iv_news);
        tv_news_name.setText(news.getName());
        tv_time.setText(StringUtils.formatSomeAgo(news.getPubDate()));
    }

    @Override
    protected void onRequestStart() {
        super.onRequestStart();
        mTvState.setText("正在搜寻资讯");
    }

    @Override
    protected void onFailure() {
        super.onFailure();
        mTvState.setText("很遗憾，你没有摇到资讯，请再试一次");
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<ShakeNews>>() {
        }.getType();
    }
}
