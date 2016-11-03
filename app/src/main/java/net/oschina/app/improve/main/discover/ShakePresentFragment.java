package net.oschina.app.improve.main.discover;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.shake.ShakePresent;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.common.verify.Verifier;

import java.lang.reflect.Type;

/**
 * 摇一摇礼品相关实现
 */
public class ShakePresentFragment extends BaseSensorFragment<ShakePresent> {
    private Button mBtnShakeAgain, mBtnGet;
    private ImageView mImgPig;
    private TextView mTxtName;
    private boolean mCanAgain;

    public static ShakePresentFragment newInstance() {
        ShakePresentFragment fragment = new ShakePresentFragment();
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 1) {
            mLoading = false;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shake_present;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mShakeView = mInflater.inflate(R.layout.view_present, null);
        mBtnShakeAgain = (Button) mShakeView.findViewById(R.id.btn_shake_again);
        mBtnGet = (Button) mShakeView.findViewById(R.id.btn_get);
        mImgPig = (ImageView) mShakeView.findViewById(R.id.iv_pig);
        mTxtName = (TextView) mShakeView.findViewById(R.id.tv_name);
        mBtnShakeAgain.setOnClickListener(this);
        mBtnGet.setOnClickListener(this);
        mSpeedThreshold = 70;
        mCardView.setVisibility(View.GONE);
        mTvState.setText("摇一摇抢礼品");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shake_again:
                if (mLoading && mCanAgain) {
                    mCardView.clearAnimation();
                    mCardView.setVisibility(View.GONE);
                    mCardView.removeAllViews();
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
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(ShakePresentFragment.this, 1);
            Toast.makeText(mContext, "摇礼品需要登陆", Toast.LENGTH_LONG).show();
            return;
        }
        mCanAgain = false;
        String appToken = Verifier.getPrivateToken(getActivity().getApplication());
        long time = System.currentTimeMillis();
        String sign = Verifier.signStringArray(String.valueOf(time), String.valueOf(AccountHelper.getUserId()),
                appToken);
        OSChinaApi.getShakePresent(time, appToken, sign, mHandler);
    }

    @Override
    protected void onTimeProgress() {
        if (mContext != null) {
            if (mTimeHandler == null)
                mTimeHandler = new Handler();
            mLoadingView.setVisibility(View.GONE);
            if (mBean != null && mBean.getCode() == 251) {//活动进行中，没摇到
                mBtnShakeAgain.setTextColor(0xFFD8D8D8);
                mTxtTime.setVisibility((mBean == null || mBean.getResult() == null) ? View.VISIBLE : View.INVISIBLE);
                mTxtTime.setText(String.format("%s秒后可再摇一次", mDelayTime));
                mTimeHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        --mDelayTime;
                        if (mTxtTime == null)
                            return;
                        if (mBean == null || mBean.getResult() == null) {
                            mTxtTime.setText(String.format("%s秒后可再摇一次", mDelayTime));
                        } else {
                            mBtnShakeAgain.setText(String.format("再摇一次(%s)", mDelayTime));
                        }
                        if (mDelayTime > 0)
                            mTimeHandler.postDelayed(this, 1000);
                        else {
                            mBtnShakeAgain.setText("再摇一次");
                            mBtnShakeAgain.setTextColor(0xFF111111);
                            mTvState.setText("摇一摇抢礼品");
                            mCanAgain = true;
                            mTxtTime.setVisibility(View.INVISIBLE);
                            mLoading = mBean != null && mBean.getResult() != null;
                            mDelayTime = 5;
                        }
                    }
                }, 1000);
            } else {
                mTvState.setText(mBean != null ? mBean.getMessage() : "很抱歉，出现未知错误");
                mLoading = false;
            }
        }
    }

    @Override
    protected void initShakeView() {
        ShakePresent present = mBean.getResult();
        mCardView.setVisibility(View.VISIBLE);
        getImgLoader().load(present.getPic()).placeholder(R.mipmap.ic_split_graph).into(mImgPig);
        mTxtName.setText(present.getName());
        mTvState.setText("恭喜您中奖了");
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<ShakePresent>>() {
        }.getType();
    }
}
