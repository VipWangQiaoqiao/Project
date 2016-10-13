package net.oschina.app.improve.main.discover;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.shake.ShakePresent;
import net.oschina.app.util.UIHelper;
import net.oschina.common.verify.Verifier;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/11.
 */

public class ShakePresentFragment extends BaseSensorFragment<ShakePresent> {

    private ImageView iv_present;
    private TextView tv_present_name;

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
        iv_present = (ImageView) mShakeView.findViewById(R.id.iv_present);
        tv_present_name = (TextView) mShakeView.findViewById(R.id.tv_present_name);
        SPEED_SHRESHOLD = 90;
    }

    @Override
    public void onClick(View v) {
        if (mBean != null) {
            UIHelper.showUrlRedirect(mContext, mBean.getResult().getHref());
        }
    }

    @Override
    public void onShake() {
        String appToken = Verifier.getPrivateToken(getActivity().getApplication());
        long time = System.currentTimeMillis();
        String sign = Verifier.signStringArray(String.valueOf(time), String.valueOf(AppContext.getInstance().getLoginId()),
                appToken);
        OSChinaApi.getShakePresent(time, appToken, sign, mHandler);
    }

    @Override
    protected void initShakeView() {
        ShakePresent present = mBean.getResult();
        getImgLoader().load(present.getPic()).placeholder(R.mipmap.ic_split_graph).into(iv_present);
        tv_present_name.setText(present.getName());
        MediaPlayer.create(mContext, R.raw.shake).start();
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<ShakePresent>>() {
        }.getType();
    }
}
