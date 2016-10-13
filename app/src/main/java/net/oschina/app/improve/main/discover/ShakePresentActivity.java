package net.oschina.app.improve.main.discover;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 摇一摇抽奖
 * Created by haibin
 * on 2016/10/10.
 */

public class ShakePresentActivity extends BaseBackActivity implements View.OnClickListener {

    @Bind(R.id.ll_shake_present)
    LinearLayout ll_shake_present;

    @Bind(R.id.ll_shake_news)
    LinearLayout ll_shake_news;

    private ShakePresentFragment mPresentFragment;
    private ShakeNewsFragment mNewsFragment;

    public static void show(Context context) {
        context.startActivity(new Intent(context, ShakePresentActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_shake_present;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mNewsFragment = ShakeNewsFragment.newInstance();
        mPresentFragment = ShakePresentFragment.newInstance();
        addFragment(R.id.fl_content, mNewsFragment);
        addFragment(R.id.fl_content, mPresentFragment);

    }

    @OnClick({R.id.ll_shake_present, R.id.ll_shake_news})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_shake_present) {
            addFragment(R.id.fl_content, mPresentFragment);
            mNewsFragment.unregisterSensor();
            mPresentFragment.registerSensor();
            setState(ll_shake_news, false);
            setState(ll_shake_present, true);
        } else if (v.getId() == R.id.ll_shake_news) {
            addFragment(R.id.fl_content, mNewsFragment);
            mPresentFragment.unregisterSensor();
            mNewsFragment.registerSensor();
            setState(ll_shake_present, false);
            setState(ll_shake_news, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresentFragment.registerSensor();
        setState(ll_shake_present, true);
    }

    @Override
    public void finish() {
        super.finish();
        mPresentFragment.unregisterSensor();
        mNewsFragment.unregisterSensor();
    }

    private void setState(LinearLayout layout, boolean selected) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setSelected(selected);
        }
    }
}
