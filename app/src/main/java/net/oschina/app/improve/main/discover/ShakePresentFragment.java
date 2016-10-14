package net.oschina.app.improve.main.discover;

import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.shake.ShakePresent;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.common.verify.Verifier;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/11.
 */

public class ShakePresentFragment extends BaseSensorFragment<ShakePresent> {

    private Button btn_shake_again, btn_get;
    private ImageView iv_pig;
    private TextView tv_name;
    private boolean mCanAgain;

    public static ShakePresentFragment newInstance() {
        ShakePresentFragment fragment = new ShakePresentFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shake_present;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mShakeView = mInflater.inflate(R.layout.view_present, null);
        btn_shake_again = (Button) mShakeView.findViewById(R.id.btn_shake_again);
        btn_get = (Button) mShakeView.findViewById(R.id.btn_get);
        iv_pig = (ImageView) mShakeView.findViewById(R.id.iv_pig);
        tv_name = (TextView) mShakeView.findViewById(R.id.tv_name);
        btn_shake_again.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        SPEED_SHRESHOLD = 90;
        mCardView.setVisibility(View.GONE);
        mTvState.setText("摇一摇抢礼品");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shake_again:
                if (mLoading && mCanAgain) {
                    mCardView.removeAllViews();
                    mCardView.setVisibility(View.GONE);
                    mCanAgain = false;
                    mLoading = false;
                }
                break;
            case R.id.btn_get:
                if (mBean != null) {
                    UIHelper.showUrlRedirect(mContext, mBean.getResult().getHref());
                }
                break;
            default:

                break;
        }
    }

    @Override
    public void onShake() {
        if (!TDevice.hasInternet()) {
            Toast.makeText(mContext, "网络连接失败", Toast.LENGTH_SHORT).show();
            mLoading = false;
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            Toast.makeText(mContext, "摇礼品需要登陆", Toast.LENGTH_LONG).show();
            return;
        }
        String appToken = Verifier.getPrivateToken(getActivity().getApplication());
        appToken = "1";
        long time = 15428467;
        String sign = Verifier.signStringArray(String.valueOf(time), String.valueOf(AppContext.getInstance().getLoginId()),
                appToken);
        OSChinaApi.getShakePresent(time, appToken, sign, mHandler);
    }

    @Override
    protected void onTimeProgress() {

        if (mContext != null) {
            if (mTimeHandler == null)
                mTimeHandler = new Handler();
            mLoadingView.setVisibility(View.GONE);
            tv_time.setVisibility((mBean == null || mBean.getResult() == null) ? View.VISIBLE : View.INVISIBLE);
            tv_time.setText(String.format("%d秒后可再摇一次", timeDelay));
            mTimeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    --timeDelay;
                    if (tv_time == null)
                        return;
                    if (mBean == null || mBean.getResult() == null) {
                        tv_time.setText(String.format("%d秒后可再摇一次", timeDelay));
                    } else {
                        btn_shake_again.setText(String.format("再摇一次(%d)", timeDelay));
                    }
                    if (timeDelay > 0)
                        mTimeHandler.postDelayed(this, 1000);
                    else {
                        btn_shake_again.setText("再摇一次");
                        mTvState.setText("摇一摇抢礼品");
                        mCanAgain = true;
                        tv_time.setVisibility(View.INVISIBLE);
                        mLoading = mBean != null && mBean.getResult() != null;
                        timeDelay = 5;
                    }
                }
            }, 1000);
        }
    }

    @Override
    protected void initShakeView() {
        ShakePresent present = mBean.getResult();
        mCardView.setVisibility(View.VISIBLE);
        getImgLoader().load(present.getPic()).placeholder(R.mipmap.ic_split_graph).into(iv_pig);
        tv_name.setText(present.getName());
        mTvState.setText("恭喜您中奖了");
        MediaPlayer.create(mContext, R.raw.shake).start();
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<ShakePresent>>() {
        }.getType();
    }
}
