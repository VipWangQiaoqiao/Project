package net.oschina.app.improve.main.discover;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Banner;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.shake.ShakeNews;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/11.
 */

public class ShakeNewsFragment extends BaseSensorFragment<ShakeNews> {

    private ImageView iv_news;
    private TextView tv_news_name, tv_pubTime;

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
        tv_pubTime = (TextView) mShakeView.findViewById(R.id.tv_time);
        timeDelay = 3;
        mCardView.setVisibility(View.GONE);
        mTvState.setText("摇一摇获取资讯");
    }

    @Override
    public void onClick(View v) {
        if (mBean != null) {
            Banner banner = new Banner();
            ShakeNews news = mBean.getResult();
            banner.setId(news.getId());
            banner.setType(news.getType());
            banner.setName(news.getName());
            UIHelper.showBannerDetail(mContext, banner);
        }
    }

    @Override
    public void onShake() {
        if(!TDevice.hasInternet()){
            Toast.makeText(mContext,"网络连接失败",Toast.LENGTH_SHORT).show();
            mLoading = false;
            return;
        }
        OSChinaApi.getShakeNews(mHandler);
    }

    @Override
    protected void initShakeView() {
        ShakeNews news = mBean.getResult();
        mCardView.setVisibility(View.VISIBLE);
        getImgLoader().load(news.getImg()).placeholder(R.mipmap.ic_split_graph).into(iv_news);
        tv_news_name.setText(news.getName());
        tv_pubTime.setText(StringUtils.formatSomeAgo(news.getPubDate()));
    }

    @Override
    protected void onRequestStart() {
        super.onRequestStart();
        mTvState.setText("正在搜寻资讯");
    }

    @Override
    protected void onTimeProgress() {
        if (mContext != null) {
            if (mTimeHandler == null)
                mTimeHandler = new Handler();
            mLoadingView.setVisibility(View.GONE);
            tv_time.setVisibility(View.VISIBLE);
            tv_time.setText(String.format("%d秒后可再摇一次", timeDelay));
            mTimeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tv_time == null)
                        return;
                    tv_time.setVisibility(View.VISIBLE);
                    --timeDelay;
                    if (tv_time == null)
                        return;
                    tv_time.setText(String.format("%d秒后可再摇一次", timeDelay));
                    if (timeDelay > 0)
                        mTimeHandler.postDelayed(this, 1000);
                    else {
                        tv_time.setVisibility(View.INVISIBLE);
                        mTvState.setVisibility(View.VISIBLE);
                        mTvState.setText("摇一摇获取资讯");
                        mLoading = false;
                        timeDelay = 3;
                    }
                }
            }, 1000);
        }
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
